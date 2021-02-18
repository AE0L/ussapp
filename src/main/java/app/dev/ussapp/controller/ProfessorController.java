package app.dev.ussapp.controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import app.dev.ussapp.model.Comment;
import app.dev.ussapp.model.Document;
import app.dev.ussapp.model.GroupMember;
import app.dev.ussapp.model.Professor;
import app.dev.ussapp.model.ProfessorNotification;
import app.dev.ussapp.model.ResearchGroup;
import app.dev.ussapp.model.ResearchProposal;
import app.dev.ussapp.model.Student;
import app.dev.ussapp.model.SubjClass;
import app.dev.ussapp.model.exception.DocumentStorageException;
import app.dev.ussapp.model.forms.CommentForm;
import app.dev.ussapp.model.forms.ProfessorAccountForm;
import app.dev.ussapp.model.forms.ProfessorCreateClassForm;
import app.dev.ussapp.model.repos.ClassRepository;
import app.dev.ussapp.model.repos.CommentRepository;
import app.dev.ussapp.model.repos.GroupsRepository;
import app.dev.ussapp.model.repos.ProfessorRepository;
import app.dev.ussapp.model.repos.ResearchProposalRepository;
import app.dev.ussapp.model.repos.StudentRepository;
import app.dev.ussapp.model.service.AdmitService;
import app.dev.ussapp.model.service.DocumentService;
import app.dev.ussapp.model.service.NotificationService;

@Controller
public class ProfessorController {

    @Autowired
    StudentRepository studentRepo;

    @Autowired
    ProfessorRepository profRepo;

    @Autowired
    ClassRepository classRepo;

    @Autowired
    GroupsRepository groupRepo;

    @Autowired
    ResearchProposalRepository proposalRepo;

    @Autowired
    DocumentService docService;

    @Autowired
    CommentRepository commentRepo;

    @Autowired
    NotificationService notifService;

    @Autowired
    AdmitService admitService;

    private static final String INDEX = "redirect:/";

    @PostMapping("/create-professor")
    public String createProfessor(HttpSession session, ProfessorAccountForm form) {
        if (isValidUsername(form.getUsername())) {
            if (isValidEmail(form.getEmail())) {
                Professor prof = Professor.createProfessor(form);

                profRepo.save(prof);

                session.setAttribute("professor", prof);

                return "redirect:/professor-home";
            }
        }

        return INDEX;
    }

    private boolean isValidUsername(String user) {
        return studentRepo.findAllByUsername(user).isEmpty() && profRepo.findAllByUsername(user).isEmpty();
    }

    private boolean isValidEmail(String email) {
        return studentRepo.findAllByEmail(email).isEmpty() && profRepo.findAllByEmail(email).isEmpty();
    }

    @GetMapping("/professor-home")
    public String home(HttpSession session, Model model) {
        String sessChk = checkSession(session, "/professor/home");

        if (!sessChk.equals(INDEX)) {
            Professor professor = (Professor) session.getAttribute("professor");
            List<ProfessorNotification> notifs = notifService.getNotifications(professor);

            model.addAttribute("notifications", notifs);
        }

        return sessChk;
    }

    @GetMapping("/professor-class-page")
    public String classPage(HttpSession session) {
        return checkSession(session, "/professor/class-page");
    }

    @GetMapping("/professor-create-class")
    public String createClass(HttpSession session, Model model) {
        String sessChk = checkSession(session, "/professor/create-class");

        if (!sessChk.equals(INDEX)) {
            model.addAttribute("form", new ProfessorCreateClassForm());
        }

        return sessChk;
    }

    @PostMapping("/professor-register-class")
    public String registerClass(HttpSession session, ProfessorCreateClassForm form) {
        String sessChk = checkSession(session, "redirect:/professor-get-class-details");

        if (!sessChk.equals(INDEX)) {
            String className = form.getClassName();
            Professor prof = (Professor) session.getAttribute("professor");

            SubjClass subjClass = new SubjClass(className, prof);

            prof.addClass(subjClass);

            classRepo.save(subjClass);

            for (ResearchGroup group : ResearchGroup.generateGroups(subjClass, form.getGroups())) {
                ResearchProposal proposal = new ResearchProposal(Timestamp.valueOf(form.getDeadline()),
                        form.getLimit());

                subjClass.addGroup(group);
                group.setProposal(proposal);

                groupRepo.save(group);
            }

            sessChk += "/" + subjClass.getCode();
        }

        return sessChk;
    }

    @GetMapping("/professor-get-class-details/{id}")
    public String getClassDetails(@PathVariable String id, HttpSession session, RedirectAttributes redirAttr) {
        String sessChk = checkSession(session, "redirect:/professor-class-details");

        if (!sessChk.equals(INDEX)) {
            Optional<SubjClass> res = classRepo.findById(id);

            if (res.isPresent()) {
                SubjClass subjClass = res.get();

                List<ResearchGroup> groups = subjClass.getGroups().stream()
                        .sorted((obj1, obj2) -> obj1.getName().compareTo(obj2.getName())).collect(Collectors.toList());

                redirAttr.addFlashAttribute("class", subjClass);
                redirAttr.addFlashAttribute("groups", groups);
            }
        }

        return sessChk;
    }

    @GetMapping("/professor-class-details")
    public String classDetails(HttpSession session, Model model) {
        return checkSession(session, "/professor/class-details");
    }

    @GetMapping("/professor-get-group-details/{id}")
    public String getGroupDetails(@PathVariable String id, HttpSession session) {
        String sessChk = checkSession(session, "redirect:/professor-group-details");

        if (!sessChk.equals(INDEX)) {
            Optional<ResearchGroup> res = groupRepo.findById(id);

            if (res.isPresent()) {
                ResearchGroup group = res.get();

                session.setAttribute("group", group);
            }
        }

        return sessChk;
    }

    @GetMapping("/professor-group-details")
    public String groupDetails(HttpSession session, Model model) {
        model.addAttribute("form", new CommentForm());

        return checkSession(session, "/professor/group-details");
    }

    @PostMapping("/professor-submit-comment")
    public String submitComment(HttpSession session, CommentForm form) {
        String sessChk = checkSession(session, "redirect:/professor-group-details");

        if (!sessChk.equals(INDEX)) {
            Professor prof = (Professor) session.getAttribute("professor");
            ResearchGroup group = (ResearchGroup) session.getAttribute("group");
            ResearchProposal proposal = group.getProposal();

            String text = form.getComment().replace("\n", "<br>");
            Timestamp date = Timestamp.valueOf(LocalDateTime.now());
            int order = proposal.getComments().size() + 1;
            Comment comment = new Comment(text, date, prof.getName(), order);

            List<GroupMember> members = group.getGroupMembers();

            String notif = "<strong>" + prof.getName() + "</strong> commented on your proposal, <strong>"
                    + proposal.getTitle() + "</strong>";
            String link = "/student-get-class-group/" + group.getSubjClass().getCode();
            String email = "Comment: <br>" + comment.getText();
            for (GroupMember member : members) {
                Student stud = member.getStudent();

                notifService.sendNotification(stud, notif, email, link);
            }

            proposal.addComment(comment);

            commentRepo.save(comment);
        }

        return sessChk;
    }

    @GetMapping("/professor-download-proposal")
    public ResponseEntity<Resource> downloadProposal(HttpSession session) {
        ResearchGroup group = (ResearchGroup) session.getAttribute("group");
        ResearchProposal proposal = group.getProposal();

        try {
            Document doc = docService.getDocument(proposal.getFile().getId());

            return ResponseEntity.ok().contentType(MediaType.parseMediaType(doc.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getFileName() + "\"")
                    .body(new ByteArrayResource(doc.getFile()));
        } catch (DocumentStorageException e) {
            e.printStackTrace();

            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/professor-see-notification/{id}")
    public String seeNotification(@PathVariable String id, HttpSession session) {
        String sessChk = checkSession(session, "redirect:");

        if (!sessChk.equals(INDEX)) {
            Optional<ProfessorNotification> res = notifService.getProfessorNotification(id);

            if (res.isPresent()) {
                ProfessorNotification notif = res.get();

                Professor prof = notifService.setNotificationAsSeen(notif);

                session.setAttribute("professor", prof);

                return "redirect:" + notif.getLink();
            }
        }

        return sessChk;
    }

    @GetMapping("/professor-accept-proposal")
    public String acceptProposal(HttpSession session) {
        String sessChk = checkSession(session, "redirect:/professor-group-details");

        if (!sessChk.equals(INDEX)) {
            ResearchGroup group = (ResearchGroup) session.getAttribute("group");
            ResearchProposal proposal = group.getProposal();

            proposal.setAccepted(true);

            proposalRepo.save(proposal);

            String subj = "<strong>" + group.getSubjClass().getProfessor().getName() + "</strong> has accepted your proposal, " + proposal.getTitle(); 
            String link = "/student-get-class-group/" + group.getSubjClass().getCode();

            for (GroupMember member : group.getGroupMembers()) {
                notifService.sendNotification(member.getStudent(), subj, "", link);
            }

            session.setAttribute("group", group);
        }

        return sessChk;
    }

    @GetMapping("/professor-admit-student/{id}")
    public String admitStudent(@PathVariable String id, HttpSession session) {
        String sessChk = checkSession(session, "redirect:/professor-group-details");

        if (!sessChk.equals(INDEX)) {
            Optional<Student> res = studentRepo.findById(id);

            if (res.isPresent()) {
                Student student = res.get();

                ResearchGroup group = admitService.acceptAdmittance(student);

                session.setAttribute("group", group);
            }
        }

        return sessChk;
    }


    private String checkSession(HttpSession session, String success) {
        if (session.getAttribute("professor") != null) {
            return success;
        }

        return INDEX;
    }

}

package app.dev.ussapp.controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import app.dev.ussapp.model.Comment;
import app.dev.ussapp.model.Document;
import app.dev.ussapp.model.GroupMember;
import app.dev.ussapp.model.ResearchGroup;
import app.dev.ussapp.model.ResearchProposal;
import app.dev.ussapp.model.Student;
import app.dev.ussapp.model.StudentNotification;
import app.dev.ussapp.model.SubjClass;
import app.dev.ussapp.model.exception.DocumentStorageException;
import app.dev.ussapp.model.forms.CommentForm;
import app.dev.ussapp.model.forms.StudentAccountForm;
import app.dev.ussapp.model.forms.StudentFileSubmitForm;
import app.dev.ussapp.model.forms.StudentJoinClassForm;
import app.dev.ussapp.model.forms.StudentRevisionSubmitForm;
import app.dev.ussapp.model.repos.ClassMemberRepository;
import app.dev.ussapp.model.repos.ClassRepository;
import app.dev.ussapp.model.repos.CommentRepository;
import app.dev.ussapp.model.repos.GroupMemberRepository;
import app.dev.ussapp.model.repos.GroupsRepository;
import app.dev.ussapp.model.repos.ProfessorRepository;
import app.dev.ussapp.model.repos.ResearchProposalRepository;
import app.dev.ussapp.model.repos.StudentRepository;
import app.dev.ussapp.model.service.AdmitService;
import app.dev.ussapp.model.service.DocumentService;
import app.dev.ussapp.model.service.NotificationService;

@Controller
public class StudentController {

    @Autowired
    StudentRepository studentRepo;

    @Autowired
    ProfessorRepository profRepo;

    @Autowired
    ClassRepository classRepo;

    @Autowired
    ClassMemberRepository classMemberRepo;

    @Autowired
    GroupsRepository groupRepo;

    @Autowired
    GroupMemberRepository groupMemberRepo;

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

    @PostMapping("/create-student")
    public String createStudentAccount(HttpSession session, StudentAccountForm form) {
        if (isValidUsername(form.getUsername())) {
            if (isValidEmail(form.getEmail())) {
                Student student = Student.createStudent(form);

                studentRepo.save(student);

                session.setAttribute("student", student);

                return "redirect:/student-home";
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

    @GetMapping("/student-home")
    public String home(HttpSession session, Model model) {
        String sessChk = checkSession(session, "/student/home");

        if (!sessChk.equals(INDEX)) {
            Student student = (Student) session.getAttribute("student");
            List<StudentNotification> notifs = notifService.getNotifications(student);

            model.addAttribute("notifications", notifs);
        }

        return sessChk;
    }

    @RequestMapping("/student-class-page")
    public String getStudentClassPage(HttpSession session, Model model) {
        String error = (String) session.getAttribute("add-class-error");

        if (error != null) {
            model.addAttribute("error", error);
        }
        
        return checkSession(session, "/student/class-page");
    }

    @GetMapping("/student-join-class")
    public String joinClass(HttpSession session, Model model) {
        String sessChk = checkSession(session, "/student/join-class");

        if (!sessChk.equals(INDEX)) {
            model.addAttribute("form", new StudentJoinClassForm());
        }

        return sessChk;
    }

    @PostMapping("/student-register-class")
    public String registerClass(HttpSession session, StudentJoinClassForm form, RedirectAttributes redirAttr) {
        String sessChk = checkSession(session, "redirect:/student-class-page");

        if (!sessChk.equals(INDEX)) {
            Optional<SubjClass> classRes = classRepo.findById(form.getClassCode());
            Optional<ResearchGroup> groupRes = groupRepo.findById(form.getGroupCode());

            if (classRes.isPresent() && groupRes.isPresent()) {
                Student student = (Student) session.getAttribute("student");
                SubjClass subjClass = classRes.get();
                ResearchGroup group = groupRes.get();

                if (!admitService.isAlreadyMember(student, subjClass)) {
                    admitService.addToAdmitList(student, subjClass, group);
                } else {
                    session.setAttribute("add-class-error", "Already a student of this class");
                }
            }
        }

        return sessChk;
    }

    @GetMapping("/student-get-class-group/{id}")
    public String getClassGroup(@PathVariable String id, HttpSession session) {
        String sessChk = checkSession(session, "redirect:/student-group-details");

        if (!sessChk.equals(INDEX)) {
            Optional<SubjClass> classRes = classRepo.findById(id);

            if (classRes.isPresent()) {
                Student student = (Student) session.getAttribute("student");
                SubjClass subjClass = classRes.get();
                List<GroupMember> groupMember = student.getGroupMember().stream()
                        .filter(member -> member.getGroupCode().getSubjClass().getCode().equals(subjClass.getCode()))
                        .collect(Collectors.toList());

                if (!groupMember.isEmpty()) {
                    ResearchGroup group = groupMember.get(0).getGroupCode();

                    session.setAttribute("group", group);
                }
            }
        }

        return sessChk;
    }

    @GetMapping("/student-group-details")
    public String groupDetails(HttpSession session, Model model) {
        String sessChk = checkSession(session, "/student/group-details");

        if (!sessChk.equals(INDEX)) {
            ResearchGroup group = (ResearchGroup) session.getAttribute("group");
            ResearchProposal proposal = group.getProposal();
            boolean overdue = false;

            if (proposal.getFile() == null) {
                overdue = !proposal.isAccepted()
                        && proposal.getDeadline().before(Timestamp.valueOf(LocalDateTime.now()));
            } else {
                overdue = !proposal.isAccepted() && proposal.getDeadline().before(proposal.getLastEdit());
            }

            model.addAttribute("overdue", overdue);
            model.addAttribute("form", new CommentForm());
        }

        return sessChk;
    }

    @PostMapping("/student-submit-comment")
    public String submitComment(HttpSession session, CommentForm form) {
        String sessChk = checkSession(session, "redirect:/student-group-details");

        if (!sessChk.equals(INDEX)) {
            Student student = (Student) session.getAttribute("student");
            ResearchGroup group = (ResearchGroup) session.getAttribute("group");
            ResearchProposal proposal = group.getProposal();
            String text = form.getComment().replace("\n", "<br>");
            Timestamp date = Timestamp.valueOf(LocalDateTime.now());
            int order = proposal.getComments().size() + 1;
            Comment comment = new Comment(text, date, student.getName(), order);

            proposal.addComment(comment);

            commentRepo.save(comment);

            String notif = "<strong>" + student.getName() + "</strong> of class <strong>" + group.getSubjClass().getName() + " commented on the group, <strong>" + group.getName() + "</strong>";
            String email = "Comment:<br>" + comment.getText();
            String link = "/professor-get-group-details/" + group.getCode();

            notifService.sendNotification(group.getSubjClass().getProfessor(), notif, email, link);
        }

        return sessChk;
    }

    @GetMapping("/student-submit-file")
    public String submitFile(HttpSession session, Model model) {
        String sessChk = checkSession(session, "/student/submit-file");

        if (!sessChk.equals(INDEX)) {
            model.addAttribute("form", new StudentFileSubmitForm());
        }

        return sessChk;
    }

    @PostMapping("/student-submit-proposal")
    public String submitProposal(HttpSession session, StudentFileSubmitForm form) {
        String sessChk = checkSession(session, "redirect:/student-group-details");

        if (!sessChk.equals(INDEX)) {
            ResearchGroup group = (ResearchGroup) session.getAttribute("group");
            ResearchProposal proposal = group.getProposal();

            try {
                Document doc = docService.storeDocument(form.getFile());

                proposal.setLastEdit(Timestamp.valueOf(LocalDateTime.now()));
                proposal.setTitle(form.getTitle());
                proposal.setFile(doc);

                proposalRepo.save(proposal);

                String notif = "<strong>" + group.getName() + "</strong> of class <strong>" + group.getSubjClass().getName() + "</strong> has sent their proposal";
                String link = "/professor-get-group-details/" + group.getCode();
                String email = "";

                notifService.sendNotification(group.getSubjClass().getProfessor(), notif, email, link);
            } catch (DocumentStorageException e) {
                e.printStackTrace();

                return INDEX;
            }
        }

        return sessChk;
    }

    @GetMapping("/student-submit-revision")
    public String submitRevision(HttpSession session, Model model) {
        String sessChk = checkSession(session, "/student/submit-revision");

        if (!sessChk.equals(INDEX)) {
            model.addAttribute("form", new StudentRevisionSubmitForm());
        }

        return sessChk;
    }

    @PostMapping("/student-submit-revision")
    public String updateRevision(HttpSession session, StudentRevisionSubmitForm form, Model model) {
        String sessChk = checkSession(session, "redirect:/student-group-details");

        if (!sessChk.equals(INDEX)) {
            ResearchGroup group = (ResearchGroup) session.getAttribute("group");

            try {
                docService.updateDocument(form.getFile(), group.getProposal().getFile());

                String notif = "<strong>" + group.getName() + "</strong> of class <strong>" + group.getSubjClass().getName() + "</strong> has sent a new revision";
                String link = "/professor-get-group-details/" + group.getCode();
                String email = "";

                notifService.sendNotification(group.getSubjClass().getProfessor(), notif, email, link);
            } catch (DocumentStorageException e) {
                e.printStackTrace();

                return INDEX;
            }

            group.getProposal().addRevisions();

            proposalRepo.save(group.getProposal());
        }

        return sessChk;
    }

    @GetMapping("/student-see-notification/{id}")
    public String seeNotification(@PathVariable String id, HttpSession session) {
        String sessChk = checkSession(session, "redirect:");

        if (!sessChk.equals(INDEX)) {
            Optional<StudentNotification> res = notifService.getStudentNotification(id);

            if (res.isPresent()) {
                StudentNotification notif = res.get();

                Student stud = notifService.setNotificationAsSeen(notif);

                session.setAttribute("student", stud);

                return sessChk + notif.getLink();
            }
        }

        return sessChk;
    }

    private String checkSession(HttpSession session, String success) {
        if (session.getAttribute("student") != null) {
            return success;
        }

        return "redirect:/";
    }

}

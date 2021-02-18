package app.dev.ussapp.model.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.dev.ussapp.model.AdmitList;
import app.dev.ussapp.model.ClassMember;
import app.dev.ussapp.model.GroupMember;
import app.dev.ussapp.model.ResearchGroup;
import app.dev.ussapp.model.Student;
import app.dev.ussapp.model.SubjClass;
import app.dev.ussapp.model.repos.AdmitListRepository;
import app.dev.ussapp.model.repos.ClassMemberRepository;
import app.dev.ussapp.model.repos.GroupMemberRepository;

@Service
public class AdmitService {

    @Autowired
    AdmitListRepository admitRepo;

    @Autowired
    ClassMemberRepository classMemberRepo;

    @Autowired
    GroupMemberRepository groupMemberRepo;

    @Autowired
    NotificationService notifService;

    public void addToAdmitList(Student student, SubjClass subjClass, ResearchGroup group) {
        AdmitList admit = new AdmitList();

        student.addAdmit(admit);
        subjClass.addAdmit(admit);
        group.addAdmit(admit);

        admitRepo.save(admit);

        String subj = "<strong>" + student.getName() + "</strong> is requesting for admittance on class, <strong>" + subjClass.getName() + "</strong>";
        String link = "/professor-get-group-details/" + group.getCode();

        notifService.sendNotification(subjClass.getProfessor(), subj, "", link);
    }

    public ResearchGroup acceptAdmittance(Student student) {
        Optional<AdmitList> res = admitRepo.findByStudent_Sid(student.getSid());

        if (res.isPresent()) {
            AdmitList admit = res.get();
            SubjClass subjClass = admit.getSubjClass();
            ResearchGroup group = admit.getGroup();
            GroupMember groupMember = new GroupMember(group, student);

            if (subjClass.getMembers().stream().noneMatch(member -> member.getStudent().equals(student))) {
                ClassMember classMember = new ClassMember(subjClass, student);
                student.addClassMember(classMember);
                subjClass.addClassMember(classMember); 
                subjClass.getAdmitList().remove(admit);
                classMemberRepo.save(classMember);
            }

            student.addGroupMember(groupMember);
            group.addMember(groupMember);
            groupMemberRepo.save(groupMember);

            group.getAdmitList().remove(admit);

            student.getAdmitList().remove(admit);
            admitRepo.delete(admit);

            String subj = "<strong>" + subjClass.getProfessor().getName() + "</strong> accepted your admittance to class, <strong>" + subjClass.getName() + "</strong>";
            String link = "/student-get-class-group/" + group.getSubjClass().getCode();

            notifService.sendNotification(student, subj, "", link);

            return group;
        }

        return null;
    }

    public boolean isAlreadyMember(Student student, SubjClass subjClass) {
        List<ClassMember> res = subjClass.getMembers();

        if (!res.isEmpty()) {
            return res.stream().anyMatch(member -> member.getStudent().getSid().equals(student.getSid()));
        }

        return false;
    }

}

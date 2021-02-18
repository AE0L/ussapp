package app.dev.ussapp.model.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import app.dev.ussapp.model.Professor;
import app.dev.ussapp.model.ProfessorNotification;
import app.dev.ussapp.model.Student;
import app.dev.ussapp.model.StudentNotification;
import app.dev.ussapp.model.repos.ProfessorNotificationRepository;
import app.dev.ussapp.model.repos.StudentNotificationRepository;

@Service
public class NotificationService {

    @Autowired
    StudentNotificationRepository studRepo;

    @Autowired
    ProfessorNotificationRepository profRepo;

    @Autowired
    JavaMailSender mailSender;

    public StudentNotification sendNotification(Student student, String subj, String email, String link) {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        StudentNotification notif = new StudentNotification(subj, now, link);

        student.addNotification(notif);

        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg);

        try {
            helper.setTo(student.getEmail());
            helper.setFrom("noreply.ussapp@gmail.com");
            helper.setSubject("USSAPP Notification");
            helper.setText("<h1>" + subj + "</h1> <br><hr><br> <h2>Content</h2> <br>" + email, true);

            mailSender.send(msg);
        } catch (MessagingException e) {
            e.printStackTrace();

            System.out.println("[ERROR] can't send email");
        }

        return studRepo.save(notif);
    }

    public ProfessorNotification sendNotification(Professor professor, String subj, String email, String link) {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        ProfessorNotification notif = new ProfessorNotification(subj, now, link);

        professor.addNotification(notif);

        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg);

        try {
            helper.setTo(professor.getEmail());
            helper.setFrom("noreply.ussapp@gmail.com");
            helper.setSubject("USSAPP Notification");
            helper.setText("<h1>" + subj + "</h1> <br><hr><br> <h2>Content</h2> <br>" + email, true);

            mailSender.send(msg);
        } catch (MessagingException e) {
            e.printStackTrace();

            System.out.println("[ERROR] can't send email");
        }

        return profRepo.save(notif);
    }

    public List<StudentNotification> getNotifications(Student student) {
        List<StudentNotification> notifications = student.getNotifications();

        return notifications.stream()
            .filter(notification -> !notification.isSeen())
            .sorted((n1, n2) -> n1.getDate().compareTo(n2.getDate()))
            .collect(Collectors.toList());
    }

    public List<ProfessorNotification> getNotifications(Professor professor) {
        List<ProfessorNotification> notifications = professor.getNotifications();

        return notifications.stream()
            .filter(notification -> !notification.isSeen())
            .sorted((n1, n2) -> n1.getDate().compareTo(n2.getDate()))
            .collect(Collectors.toList());
    }

    public Optional<StudentNotification> getStudentNotification(String id) {
        return studRepo.findById(id);
    }

    public Optional<ProfessorNotification> getProfessorNotification(String id) {
        return profRepo.findById(id);
    }

    public Student setNotificationAsSeen(StudentNotification notification) {
        Student stud = notification.getStudent();

        for (StudentNotification notif : stud.getNotifications()) {
            if (notif.getLink().equals(notification.getLink())) {
                notif.setSeen(true);

                studRepo.save(notif);
            } 
        }

        return stud;
    }

    public Professor setNotificationAsSeen(ProfessorNotification notification) {
        Professor prof = notification.getProfessor();

        for (ProfessorNotification notif : prof.getNotifications()) {
            if (notif.getLink().equals(notification.getLink())) {
                notif.setSeen(true);

                profRepo.save(notif);
            } 
        }

        return prof;
    }
    
}

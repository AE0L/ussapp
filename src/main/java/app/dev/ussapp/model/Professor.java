package app.dev.ussapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import app.dev.ussapp.model.forms.ProfessorAccountForm;
import app.dev.ussapp.utils.SecurityUtils;

@Entity
@Table(name = "professor")
public class Professor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "PID")
    private String pid;

    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;
    private String passwordHash;

    @OneToMany(mappedBy = "professor", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    private List<SubjClass> subjClass = new ArrayList<>();

    @OneToMany(mappedBy = "professor", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    private List<ProfessorNotification> notifications = new ArrayList<>();

    public Professor() {
    }

    public Professor(String firstName, String lastName, String username, String email, String password) {
        this.pid = "PID-" + UUID.randomUUID().toString();
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.passwordHash = SecurityUtils.hashPassword(password);
    }

    public static Professor createProfessor(ProfessorAccountForm form) {
        return new Professor(form.getFirstName(), form.getLastName(), form.getUsername(), form.getEmail(),
                form.getPassword());
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isValidPassword(String raw) {
        return SecurityUtils.isPasswordMatch(raw, passwordHash);
    }

    public void addClass(SubjClass subjClass) {
        this.subjClass.add(subjClass);
        subjClass.setProfessor(this);
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public List<SubjClass> getSubjClass() {
        return subjClass;
    }

    public void setSubjClass(List<SubjClass> subjClass) {
        this.subjClass = subjClass;
    }

    public boolean hasClass() {
        return !subjClass.isEmpty();
    }

    public String getName() {
        return lastName + ", " + firstName + " (" + username + ")";
    }

    public List<ProfessorNotification> getNotifications() {
        return notifications.stream()
            .filter(notification -> !notification.isSeen())
            .sorted((n1, n2) -> n1.getDate().compareTo(n2.getDate()))
            .collect(Collectors.toList());
    }

    public void setNotifications(List<ProfessorNotification> notifications) {
        this.notifications = notifications;
    }

    public void addNotification(ProfessorNotification notification) {
        notifications.add(notification);
        notification.setProfessor(this);
    }

    @Override
    public String toString() {
        return "Professor [pid=" + pid + ", username=" + username + "]";
    }

}
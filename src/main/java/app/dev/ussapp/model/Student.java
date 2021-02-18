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

import app.dev.ussapp.model.forms.StudentAccountForm;
import app.dev.ussapp.utils.SecurityUtils;

@Entity
@Table(name = "student")
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "SID")
    private String sid;

    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;
    private String passwordHash;

    @OneToMany(mappedBy = "student", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    private List<ClassMember> classMember = new ArrayList<>();

    @OneToMany(mappedBy = "student", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    private List<GroupMember> groupMember = new ArrayList<>();

    @OneToMany(mappedBy = "student", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    private List<StudentNotification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "student", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    private List<AdmitList> admitList = new ArrayList<>();

    public Student() {
    }

    public Student(String firstName, String lastName, String username, String email, String password) {
        this.sid = "SID-" + UUID.randomUUID().toString();
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.passwordHash = SecurityUtils.hashPassword(password);
    }

    public static Student createStudent(StudentAccountForm form) {
        return new Student(form.getFirstName(), form.getLastName(), form.getUsername(), form.getEmail(),
                form.getPassword());
    }

    public String getSid() {
        return sid;
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

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isValidPassword(String raw) {
        return SecurityUtils.isPasswordMatch(raw, passwordHash);
    }

    public boolean hasClass() {
        if (classMember != null) {
            return !classMember.isEmpty();
        }

        return false;
    }

    public List<ClassMember> getClassMember() {
        return classMember;
    }

    public void setClassMember(List<ClassMember> classMember) {
        this.classMember = classMember;
    }

    public void addClassMember(ClassMember classMember) {
        this.classMember.add(classMember);
        classMember.setStudent(this);
    }

    public List<GroupMember> getGroupMember() {
        return groupMember;
    }

    public void setGroupMember(List<GroupMember> groupMember) {
        this.groupMember = groupMember;
    }

    public void addGroupMember(GroupMember groupMember) {
        this.groupMember.add(groupMember);
        groupMember.setStudent(this);
    }

    public String getName() {
        return lastName + ", " + firstName + " (" + username + ")";
    }

    public List<StudentNotification> getNotifications() {
        return notifications.stream()
            .filter(notification -> !notification.isSeen())
            .sorted((n1, n2) -> n1.getDate().compareTo(n2.getDate()))
            .collect(Collectors.toList());
    }

    public void setNotifications(List<StudentNotification> notifications) {
        this.notifications = notifications;
    }

    public void addNotification(StudentNotification notification) {
        notifications.add(notification);
        notification.setStudent(this);
    }

    @Override
    public String toString() {
        return "Student [sid=" + sid + ", username=" + username + "]";
    }

    public List<AdmitList> getAdmitList() {
        return admitList;
    }

    public void setAdmitList(List<AdmitList> admitList) {
        this.admitList = admitList;
    }

    public void addAdmit(AdmitList admitList) {
        this.admitList.add(admitList);
        admitList.setStudent(this);
    }

}
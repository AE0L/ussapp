package app.dev.ussapp.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class GroupMember {

    @Id
    @Column(name = "GMID")
    private String id;

    @ManyToOne
    @JoinColumn(name = "group_code")
    private ResearchGroup group;

    @ManyToOne
    @JoinColumn(name = "student")
    private Student student;

    public GroupMember() {}

    public GroupMember(ResearchGroup group, Student student) {
        this.id = "GMID-" + UUID.randomUUID().toString();
        this.group = group;
        this.student = student;
    }

    public String getId() {
        return id;
    }

    public ResearchGroup getGroupCode() {
        return group;
    }

    public void setGroupCode(ResearchGroup groupCode) {
        this.group = groupCode;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student studentID) {
        this.student = studentID;
    }

    @Override
    public String toString() {
        return "GroupMember [id=" + id + "]";
    }
    
}

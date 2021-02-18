package app.dev.ussapp.model;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "class_member")
public class ClassMember implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CMID")
    private String id;

    @ManyToOne
    @JoinColumn(name = "class")
    private SubjClass subjClass;

    @ManyToOne
    @JoinColumn(name = "student")
    private Student student;

    public ClassMember() {}

    public ClassMember(SubjClass subjClass, Student student) {
        this.id = "CMID-" + UUID.randomUUID().toString();
        this.subjClass = subjClass;
        this.student = student;
    }

    public String getId() {
        return id;
    }

    public SubjClass getSubjClass() {
        return subjClass;
    }

    public void setSubjClass(SubjClass subjClass) {
        this.subjClass = subjClass;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public String toString() {
        return "ClassMember [id=" + id + "]";
    }
    
}

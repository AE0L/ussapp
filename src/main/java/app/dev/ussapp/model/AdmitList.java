package app.dev.ussapp.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "admit_list")
public class AdmitList {

    @Id
    @Column(name = "ALID")
    private String id;

    @Column(name = "admitted")
    private boolean admitted;

    @ManyToOne
    @JoinColumn(name = "class")
    private SubjClass subjClass;

    @ManyToOne
    @JoinColumn(name = "research_group")
    private ResearchGroup group;

    @ManyToOne
    @JoinColumn(name = "student")
    private Student student;

    public AdmitList() {
        this.id = "ALID-" + UUID.randomUUID().toString();
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

    public ResearchGroup getGroup() {
        return group;
    }

    public void setGroup(ResearchGroup group) {
        this.group = group;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public boolean isAdmitted() {
        return admitted;
    }

    public void setAdmitted(boolean admitted) {
        this.admitted = admitted;
    }

}

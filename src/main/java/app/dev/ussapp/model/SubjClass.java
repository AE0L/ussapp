package app.dev.ussapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import app.dev.ussapp.utils.SecurityUtils;

@Entity
@Table(name ="class")
public class SubjClass implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CCode")
    private String code;

    @Column(name = "className")
    private String name;

    @ManyToOne
    @JoinColumn(name = "professor")
    private Professor professor; 

    @OneToMany(mappedBy = "subjClass")
    private List<ResearchGroup> groups = new ArrayList<>();

    @OneToMany(mappedBy = "subjClass", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    private List<ClassMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "subjClass", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    private List<AdmitList> admitList = new ArrayList<>();

    public SubjClass() {}

    public SubjClass(String name, Professor professor) {
        this.code = SecurityUtils.generateClassCode();
        this.name = name;
        this.professor = professor;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    @Override
    public String toString() {
        return "SubjClass [code=" + code + ", name=" + name + "]";
    }

    public void addGroup(ResearchGroup group) {
        this.groups.add(group);
        group.setSubjClass(this);
    }

    public void addClassMember(ClassMember member) {
        this.members.add(member);
        member.setSubjClass(this);
    }

    public List<ResearchGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<ResearchGroup> groups) {
        this.groups = groups;
    }

    public List<ClassMember> getMembers() {
        return members;
    }

    public void setMembers(List<ClassMember> members) {
        this.members = members;
    }

    public List<AdmitList> getAdmitList() {
        return admitList;
    }

    public void setAdmitList(List<AdmitList> admitList) {
        this.admitList = admitList;
    }

    public void addAdmit(AdmitList admitList) {
        this.admitList.add(admitList);
        admitList.setSubjClass(this);
    }

}

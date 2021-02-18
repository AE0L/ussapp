package app.dev.ussapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import app.dev.ussapp.utils.SecurityUtils;

@Entity
@Table(name = "class_group")
public class ResearchGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "GCode")
    private String code;

    @Column(name = "group_name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "class")
    private SubjClass subjClass;

    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    private List<GroupMember> groupMembers = new ArrayList<>();

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "research_proposal")
    private ResearchProposal proposal;

    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    private List<AdmitList> admitList = new ArrayList<>();

    public ResearchGroup() {}

    public ResearchGroup(String name, SubjClass subjClass) {
        this.code = SecurityUtils.generateGroupCode(); 
        this.name = name;
        this.subjClass = subjClass;
    }

    public static ArrayList<ResearchGroup> generateGroups(SubjClass subjClass, int num) {
        ArrayList<ResearchGroup> groups = new ArrayList<>();

        for (int i = 1; i <= num; i++) {
            groups.add(new ResearchGroup("Group " + i, subjClass));
        }

        System.err.println("[LOG] " + groups.size());

        return groups;
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

    public SubjClass getSubjClass() {
        return subjClass;
    }

    public void setSubjClass(SubjClass subjClass) {
        this.subjClass = subjClass;
    }

    public List<GroupMember> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(List<GroupMember> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public void addMember(GroupMember member) {
        groupMembers.add(member);
        member.setGroupCode(this);
    }

    @Override
    public String toString() {
        return "ResearchGroup [code=" + code + ", name=" + name + "]";
    }

    public ResearchProposal getProposal() {
        return proposal;
    }

    public void setProposal(ResearchProposal proposal) {
        this.proposal = proposal;
    }

    public List<AdmitList> getAdmitList() {
        return admitList;
    }

    public void setAdmitList(List<AdmitList> admitList) {
        this.admitList = admitList;
    }

    public void addAdmit(AdmitList admitList) {
        this.admitList.add(admitList);
        admitList.setGroup(this);
    }
    
}

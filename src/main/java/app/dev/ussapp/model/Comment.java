package app.dev.ussapp.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @Column(name = "CID")
    private String id;

    @Column(name = "cm_text")
    private String text;

    @Column(name = "cm_date")
    private Timestamp date;

    @Column(name = "cm_user")
    private String user;

    @Column(name = "cm_order")
    private int order;

    @ManyToOne
    @JoinColumn(name = "proposal")
    private ResearchProposal proposal;

    public Comment() {}

    public Comment(String text, Timestamp date, String user, int order) {
        this.id = "CID-" + UUID.randomUUID().toString();
        this.text = text;
        this.date = date;
        this.user = user;
        this.order = order;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getFormattedDate() {
        return new SimpleDateFormat("EE, d MMM yyyy h:mm a").format(date);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public ResearchProposal getProposal() {
        return proposal;
    }

    public void setProposal(ResearchProposal proposal) {
        this.proposal = proposal;
    }
    
}

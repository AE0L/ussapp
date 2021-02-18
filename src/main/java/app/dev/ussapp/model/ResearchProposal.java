package app.dev.ussapp.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "research_proposal")
public class ResearchProposal {

    @Id
    @Column(name = "RPID")
    private String id;

    @Column(name = "title", nullable = true, updatable = true)
    private String title;

    @Column(name = "last_edit", nullable = true, updatable = true)
    private Timestamp lastEdit;

    @Column(name = "deadline", nullable = false, updatable = true)
    private Timestamp deadline;

    @Column(name = "revisions", updatable = true)
    private int revisions;

    @Column(name = "revision_limit", updatable = true)
    private int limit;

    @Column(name = "accepted", updatable = true)
    private boolean accepted;

    @OneToOne
    @JoinColumn(name = "file", nullable = true, updatable = true)
    private Document file;

    @OneToMany(mappedBy = "proposal", fetch = FetchType.EAGER)
    private List<Comment> comments = new ArrayList<>();

    public ResearchProposal() {}

    public ResearchProposal(Timestamp deadline, int limit) {
        this.id = "RPID-" + UUID.randomUUID().toString();
        this.deadline = deadline;
        this.limit = limit;
        this.accepted = false;
    }

    public static ArrayList<ResearchProposal> generateProposals(LocalDateTime deadline, int limit, int num) {
        ArrayList<ResearchProposal> proposals = new ArrayList<>();

        for (int i = 1; i <= num; i++) {
            proposals.add(new ResearchProposal(Timestamp.valueOf(deadline), limit));
        }

        return proposals;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getLastEdit() {
        return lastEdit;
    }

    public void setLastEdit(Timestamp lastEdit) {
        this.lastEdit = lastEdit;
    }

    public Timestamp getDeadline() {
        return deadline;
    }

    public void setDeadline(Timestamp deadline) {
        this.deadline = deadline;
    }

    public int getRevisions() {
        return revisions;
    }

    public void setRevisions(int revisions) {
        this.revisions = revisions;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Document getFile() {
        return file;
    }

    public void setFile(Document file) {
        this.file = file;
    }

    public String getFormattedLastEdit() {
        return new SimpleDateFormat("EE, d MMM yyyy h:mm a").format(lastEdit);
    }

    public String getFormattedDeadline() {
        return new SimpleDateFormat("EE, d MMM yyyy h:mm a").format(deadline);
    }

    @Override
    public String toString() {
        return "ResearchProposal [id=" + id + "]";
    }

	public void addRevisions() {
        revisions += 1;
	}

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public List<Comment> getComments() {
        return comments.stream().sorted((obj1, obj2) -> obj1.getOrder() - obj2.getOrder()).collect(Collectors.toList());
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setProposal(this);
    }

}

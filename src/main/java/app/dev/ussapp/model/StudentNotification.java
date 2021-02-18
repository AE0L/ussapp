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
@Table(name = "student_notification")
public class StudentNotification {

    @Id
    @Column(name = "SNID")
    private String id;

    @Column(name = "notif_text")
    private String text;

    @Column(name = "notif_seen")
    private boolean seen;

    @Column(name = "notif_date")
    private Timestamp date;

    @Column(name = "notif_link")
    private String link;

    @ManyToOne
    @JoinColumn(name = "student")
    private Student student;

    public StudentNotification() {
    }

    public StudentNotification(String text, Timestamp date, String link) {
        this.id = "SNID-" + UUID.randomUUID().toString();
        this.text = text;
        this.seen = false;
        this.date = date;
        this.link = link;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public String toString() {
        return "Notification [id=" + id + ", text=" + text + "]";
    }

}

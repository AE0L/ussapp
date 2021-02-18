package app.dev.ussapp.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "document")
public class Document {

    @Id
    @Column(name = "DID")
    private String id;

    private String fileName;
    private String fileType;

    @Lob
    private byte[] file;

    public Document() {
    }

    public Document(String fileName, String fileType, byte[] file) {
        this.id = "DID-" + UUID.randomUUID().toString();
        this.fileName = fileName;
        this.fileType = fileType;
        this.file = file;
    }

    public String getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
    
}

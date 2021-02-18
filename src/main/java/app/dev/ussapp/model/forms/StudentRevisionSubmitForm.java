package app.dev.ussapp.model.forms;

import org.springframework.web.multipart.MultipartFile;

public class StudentRevisionSubmitForm {

    private MultipartFile file;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
    
}

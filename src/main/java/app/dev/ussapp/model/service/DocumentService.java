package app.dev.ussapp.model.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import app.dev.ussapp.model.Document;
import app.dev.ussapp.model.exception.DocumentStorageException;
import app.dev.ussapp.model.repos.DocumentRepository;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository docRepo;

    public Document storeDocument(MultipartFile file) throws DocumentStorageException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            Document doc = new Document(fileName, file.getContentType(), file.getBytes());

            return docRepo.save(doc);
        } catch (IOException e) {
            e.printStackTrace();

            throw new DocumentStorageException("Can't store file!");
        }
    }

    public Document getDocument(String id) throws DocumentStorageException {
        return docRepo.findById(id).orElseThrow(() -> new DocumentStorageException("Document not found!"));
    }

    public Document updateDocument(MultipartFile file, Document doc) throws DocumentStorageException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            doc.setFileName(fileName);
            doc.setFileType(file.getContentType());
            doc.setFile(file.getBytes());

            return docRepo.save(doc);
        } catch (IOException e) {
            e.printStackTrace();

            throw new DocumentStorageException("Can't store file!");
        }
    }
}

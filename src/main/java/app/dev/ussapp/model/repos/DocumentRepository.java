package app.dev.ussapp.model.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.dev.ussapp.model.Document;

@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {
    
}

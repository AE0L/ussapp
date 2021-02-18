package app.dev.ussapp.model.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.dev.ussapp.model.ProfessorNotification;

@Repository
public interface ProfessorNotificationRepository extends JpaRepository<ProfessorNotification, String> {
    
    List<ProfessorNotification> findByProfessor_Pid(String sid);
}

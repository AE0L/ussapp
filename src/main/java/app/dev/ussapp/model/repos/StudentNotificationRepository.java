package app.dev.ussapp.model.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.dev.ussapp.model.StudentNotification;

@Repository
public interface StudentNotificationRepository extends JpaRepository<StudentNotification, String> {

    List<StudentNotification> findByStudent_Sid(String sid);
    
}

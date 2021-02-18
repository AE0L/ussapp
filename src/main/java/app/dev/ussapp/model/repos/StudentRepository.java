package app.dev.ussapp.model.repos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import app.dev.ussapp.model.Student;

@Repository
public interface StudentRepository extends CrudRepository<Student, String> {

    @Query("select s from Student s where s.username = ?1")
    List<Student> findAllByUsername(String username);

    @Query("select s from Student s where s.email = ?1")
    List<Student> findAllByEmail(String email);
    
}

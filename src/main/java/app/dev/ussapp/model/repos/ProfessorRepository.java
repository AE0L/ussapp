package app.dev.ussapp.model.repos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import app.dev.ussapp.model.Professor;

@Repository
public interface ProfessorRepository extends CrudRepository<Professor, String> {

    @Query("select p from Professor p where p.username = ?1")
    List<Professor> findAllByUsername(String username);

    @Query("select p from Professor p where p.email = ?1")
    List<Professor> findAllByEmail(String email);
    
}

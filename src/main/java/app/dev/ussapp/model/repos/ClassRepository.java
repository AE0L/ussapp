package app.dev.ussapp.model.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.dev.ussapp.model.SubjClass;

@Repository
public interface ClassRepository extends JpaRepository<SubjClass, String> {

    List<SubjClass> findAllByProfessorPid(String pid);

}

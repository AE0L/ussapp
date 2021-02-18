package app.dev.ussapp.model.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.dev.ussapp.model.AdmitList;

@Repository
public interface AdmitListRepository extends JpaRepository<AdmitList, String> {

    Optional<AdmitList> findByStudent_Sid(String sid);
    
}

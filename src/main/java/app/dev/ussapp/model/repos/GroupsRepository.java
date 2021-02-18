package app.dev.ussapp.model.repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import app.dev.ussapp.model.ResearchGroup;

@Repository
public interface GroupsRepository extends CrudRepository<ResearchGroup, String> {
    
}

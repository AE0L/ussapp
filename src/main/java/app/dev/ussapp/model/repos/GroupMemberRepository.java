package app.dev.ussapp.model.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.dev.ussapp.model.GroupMember;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, String> {
    
}

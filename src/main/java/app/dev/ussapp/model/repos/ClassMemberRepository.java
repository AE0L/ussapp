package app.dev.ussapp.model.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.dev.ussapp.model.ClassMember;

@Repository
public interface ClassMemberRepository extends JpaRepository<ClassMember, String> {
    
    List<ClassMember> findBySubjClass_Code(String CCode);
}

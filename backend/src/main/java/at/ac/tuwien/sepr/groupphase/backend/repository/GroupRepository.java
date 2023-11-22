package at.ac.tuwien.sepr.groupphase.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Member;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface GroupRepository extends 
    JpaRepository<at.ac.tuwien.sepr.groupphase.backend.entity.Group, Long> {
	
    @Query("SELECT m FROM Group g JOIN g.members m WHERE g.id = :groupId AND LOWER(m.name) LIKE LOWER(CONCAT('%', :memberName, '%'))")
    List<Member> searchForMembers(@Param("groupId") Long groupId, @Param("memberName") String memberName);
	
	@Modifying
    @Query("DELETE FROM Group g WHERE g.id = :groupId AND :memberId MEMBER OF g.members")
    void removeMemberById(@Param("groupId") Long groupId, @Param("memberId") Long memberId);

}

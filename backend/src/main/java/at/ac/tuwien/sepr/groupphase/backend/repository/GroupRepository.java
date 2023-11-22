package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface GroupRepository extends
    JpaRepository<ApplicationGroup, Long> {
    // TODO uncomment
    /*@Query("SELECT m FROM Group g JOIN g.members m WHERE g.id = :groupId AND LOWER(m.name) LIKE LOWER(CONCAT('%', :memberName, '%'))")
    List<UserGroup> searchForMembers(@Param("groupId") Long groupId, @Param("memberName") String memberName);

	@Modifying
    @Query("DELETE FROM Group g WHERE g.id = :groupId AND :memberId MEMBER OF g.members")
    void removeMemberById(@Param("groupId") Long groupId, @Param("memberId") Long memberId);
*/
}

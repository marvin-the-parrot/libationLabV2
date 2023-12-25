package at.ac.tuwien.sepr.groupphase.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.ac.tuwien.sepr.groupphase.backend.entity.Menu;
import at.ac.tuwien.sepr.groupphase.backend.entity.MenuKey;

/**
 * Repository of Menu entity.
 */
@Repository
@Transactional
public interface MenuRepository extends JpaRepository<Menu, MenuKey> {
    List<Menu> findByGroupId(Long groupId);

    void deleteByGroupId(Long groupId);
}

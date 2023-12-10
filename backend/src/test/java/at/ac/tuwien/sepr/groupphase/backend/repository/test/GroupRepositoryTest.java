package at.ac.tuwien.sepr.groupphase.backend.repository.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("generateData")
@SpringBootTest
public class GroupRepositoryTest {

    @Autowired
    private GroupRepository groupRepository;

    @Test
    public void deleteById_deleteGroupByExistingId_expectedFalse() {
        ApplicationGroup newGroup = new ApplicationGroup();
        newGroup.setId(99999L);
        newGroup.setName("newGroup");
        groupRepository.save(newGroup);
        int expected = groupRepository.findAll().size();

        groupRepository.deleteById(newGroup.getId());
        int result = groupRepository.findAll().size();

        assertNotEquals(expected, result);
    }

    @Test
    public void deleteById_deleteGroupByNotExistingId_expectedTrue() {
        int expected = groupRepository.findAll().size();

        groupRepository.deleteById(-60L);
        int result = groupRepository.findAll().size();

        assertEquals(expected, result);
    }

}

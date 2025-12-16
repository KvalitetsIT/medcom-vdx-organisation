package dk.medcom.vdx.organisation.dao;

import dk.medcom.vdx.organisation.dao.entity.Groups;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GroupsDaoTest extends AbstractDaoTest {
    @Autowired
    private GroupsDao groupsDao;

    @Test
    public void testInsertGroup() {
        var group = Groups.createInstance("name", 2, 1, "system");

        var id = groupsDao.insert(group);

        assertTrue(id >= 1);
    }
}

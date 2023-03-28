package dk.medcom.vdx.organisation.dao;


import dk.medcom.vdx.organisation.dao.impl.OrganisationViewsImpl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OrganisationViewsTest extends AbstractDaoTest {
    @Autowired
    private DataSource dataSource;
    private OrganisationViews organisationViews;

    @Before
    public void setup() {
        organisationViews = new OrganisationViewsImpl(dataSource);
    }

    @Test
    public void testGetMultipleRowsFromViewEntitiesMeetingRoom() {
        var result = organisationViews.getGroupIdFromLongLivedMeetingRooms("alias_one");
        assertTrue(result.isPresent());
        assertEquals(Long.valueOf(1L), result.get());
    }

    @Test
    public void testGetNoRowsFromViewEntitiesMeetingRoom() {
        var result = organisationViews.getGroupIdFromLongLivedMeetingRooms("not_found");

        assertTrue(result.isEmpty());
    }
}

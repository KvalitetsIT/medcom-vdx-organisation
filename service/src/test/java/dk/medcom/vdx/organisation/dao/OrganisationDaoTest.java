package dk.medcom.vdx.organisation.dao;


import dk.medcom.vdx.organisation.dao.entity.Organisation;
import dk.medcom.vdx.organisation.dao.impl.OrganisationDaoImpl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.sql.DataSource;

import static org.junit.Assert.*;

public class OrganisationDaoTest extends AbstractDaoTest {
    @Autowired
    private DataSource dataSource;
    private OrganisationDaoImpl organisationDao;

    @Before
    public void setup() {
        organisationDao = new OrganisationDaoImpl(dataSource);
    }

    @Test
    public void testQueryOrganisationWithoutPool() {
        var result = organisationDao.findOrganisation("child");

        assertNotNull(result);
        assertEquals(12L, result.getParentId().longValue());
        assertEquals(13L, result.getGroupId().longValue());
        assertEquals("child org", result.getOrganisationName());
        assertEquals("child", result.getOrganisationId());
        assertEquals("child", result.getGroupName());
        assertNull(result.getSmsCallbackUrl());
        assertNull(result.getSmsSenderName());
        assertNull(result.getPoolSize());
    }

    @Test
    public void testQueryOrganisationWithPool() {
        var result = organisationDao.findOrganisation("parent");

        assertNotNull(result);
        assertEquals(10L, result.getParentId().longValue());
        assertEquals(11L, result.getGroupId().longValue());
        assertEquals(20, result.getPoolSize().intValue());
        assertEquals("parent org", result.getOrganisationName());
        assertEquals("parent", result.getOrganisationId());
        assertEquals("parent", result.getGroupName());
        assertEquals("sms-sender", result.getSmsSenderName());
        assertEquals("callback", result.getSmsCallbackUrl());
    }

    @Test
    public void testOrganisationNotFound() {
        var result = organisationDao.findOrganisation("not_found");
        assertNull(result);
    }

    @Test
    public void testQueryOrganisationWithoutPoolByGroupId() {
        var result = organisationDao.findOrganisationByGroupId(13L);

        assertNotNull(result);
        assertEquals(12L, result.getParentId().longValue());
        assertEquals(13L, result.getGroupId().longValue());
        assertEquals("child org", result.getOrganisationName());
        assertEquals("child", result.getOrganisationId());
        assertEquals("child", result.getGroupName());
        assertNull(result.getSmsCallbackUrl());
        assertNull(result.getSmsSenderName());
        assertNull(result.getPoolSize());
    }

    @Test
    public void testQueryOrganisationWithPoolByGroupId() {
        var result = organisationDao.findOrganisationByGroupId(11L);

        assertNotNull(result);
        assertEquals(10L, result.getParentId().longValue());
        assertEquals(11L, result.getGroupId().longValue());
        assertEquals(20, result.getPoolSize().intValue());
        assertEquals("parent org", result.getOrganisationName());
        assertEquals("parent", result.getOrganisationId());
        assertEquals("parent", result.getGroupName());
        assertEquals("sms-sender", result.getSmsSenderName());
        assertEquals("callback", result.getSmsCallbackUrl());
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void testQueryDeletedGroup() {
        organisationDao.findOrganisationByGroupId(14L);
    }


    @Test(expected = EmptyResultDataAccessException.class)
    public void testOrganisationByGroupIdNotFound() {
        long notFound = 47382;
        organisationDao.findOrganisationByGroupId(notFound);
    }

    @Test
    public void testInsert() {
        var input = new Organisation();
        input.setPoolSize(10);
        input.setOrganisationId("org code");
        input.setOrganisationName("org name");
        input.setSmsSenderName("sms name");
        input.setSmsCallbackUrl("sms callback");
        input.setGroupId(13L);


        var id = organisationDao.insert(input);
        assertTrue(id > 0);

        var dbOrganisation = organisationDao.findOrganisation(input.getOrganisationId());
        assertNotNull(dbOrganisation);

        assertEquals(input.getGroupId(), dbOrganisation.getGroupId());
        assertEquals(input.getOrganisationId(), dbOrganisation.getOrganisationId());
        assertEquals(input.getOrganisationName(), dbOrganisation.getOrganisationName());
        assertEquals(input.getPoolSize(), dbOrganisation.getPoolSize());
        assertEquals(input.getSmsSenderName(), dbOrganisation.getSmsSenderName());
        assertEquals(input.getSmsCallbackUrl(), dbOrganisation.getSmsCallbackUrl());
    }

    @Test
    public void testFindOrganisationByApiKey() {
        var result = organisationDao.findOrganisationByHistoryApiKey("23646a74-7737-4bab-a8a9-07f21471a14a");

        assertNotNull(result);
        assertEquals(15, result.getGroupId().intValue());
        assertEquals("medcom", result.getGroupName());
        assertEquals("medcom", result.getOrganisationId());
        assertEquals("This Is Medcom", result.getOrganisationName());
        assertNull(result.getParentId());
        assertNull(result.getPoolSize());
        assertNull(result.getSmsSenderName());
        assertNull(result.getSmsCallbackUrl());
    }

    @Test
    public void testFindOrganisationByApiKeyNotFound() {
        var result = organisationDao.findOrganisationByHistoryApiKey("NOT FOUND");

        assertNull(result);
    }
}

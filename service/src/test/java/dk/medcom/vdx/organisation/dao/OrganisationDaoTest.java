package dk.medcom.vdx.organisation.dao;


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
}

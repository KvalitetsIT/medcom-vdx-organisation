package dk.medcom.vdx.organisation.dao;


import dk.medcom.vdx.organisation.dao.entity.Organisation;
import dk.medcom.vdx.organisation.dao.impl.OrganisationDaoImpl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;

import java.util.UUID;

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
        assertNull(result.getDeviceWebhookEndpoint());
        assertNull(result.getDeviceWebhookEndpointKey());
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
        assertEquals("device-webhook-endpoint", result.getDeviceWebhookEndpoint());
        assertEquals("device-webhook-endpoint-key", result.getDeviceWebhookEndpointKey());
    }

    @Test
    public void testOrganisationNotFound() {
        var result = organisationDao.findOrganisation("not_found");
        assertNull(result);
    }

    @Test
    public void testQueryOrganisationWithoutPoolByGroupId() {
        var result = organisationDao.findOrganisationByGroupId(13L).orElseThrow(() -> new RuntimeException("Organisation not found"));

        assertNotNull(result);
        assertEquals(12L, result.getParentId().longValue());
        assertEquals(13L, result.getGroupId().longValue());
        assertEquals("child org", result.getOrganisationName());
        assertEquals("child", result.getOrganisationId());
        assertEquals("child", result.getGroupName());
        assertNull(result.getSmsCallbackUrl());
        assertNull(result.getSmsSenderName());
        assertNull(result.getPoolSize());
        assertNull(result.getDeviceWebhookEndpoint());
        assertNull(result.getDeviceWebhookEndpointKey());
    }

    @Test
    public void testQueryOrganisationWithPoolByGroupId() {
        var result = organisationDao.findOrganisationByGroupId(11L).orElseThrow(() -> new RuntimeException("Organisation not found"));

        assertNotNull(result);
        assertEquals(10L, result.getParentId().longValue());
        assertEquals(11L, result.getGroupId().longValue());
        assertEquals(20, result.getPoolSize().intValue());
        assertEquals("parent org", result.getOrganisationName());
        assertEquals("parent", result.getOrganisationId());
        assertEquals("parent", result.getGroupName());
        assertEquals("sms-sender", result.getSmsSenderName());
        assertEquals("callback", result.getSmsCallbackUrl());
        assertEquals("device-webhook-endpoint", result.getDeviceWebhookEndpoint());
        assertEquals("device-webhook-endpoint-key", result.getDeviceWebhookEndpointKey());
    }

    @Test
    public void testQueryOrganisationByParentId() {
        var result = organisationDao.findOrganisationByParentId(12L);

        assertNotNull(result);
        assertEquals(1, result.size());
        var organisation = result.getFirst();
        assertEquals(12L, organisation.getParentId().longValue());
        assertEquals(13L, organisation.getGroupId().longValue());
        assertNull(organisation.getPoolSize());
        assertEquals("child org", organisation.getOrganisationName());
        assertEquals("child", organisation.getOrganisationId());
        assertEquals("child", organisation.getGroupName());
        assertNull(organisation.getSmsSenderName());
        assertNull(organisation.getSmsCallbackUrl());
        assertNull(organisation.getDeviceWebhookEndpoint());
        assertNull(organisation.getDeviceWebhookEndpointKey());
    }

    @Test
    public void testQueryOrganisationByParentIdNotFound() {
        var result = organisationDao.findOrganisationByParentId(123L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testQueryDeletedGroup() {
        var result = organisationDao.findOrganisationByGroupId(14L);
        assertTrue(result.isEmpty());
    }


    @Test
    public void testOrganisationByGroupIdNotFound() {
        long notFound = 47382;
        var result = organisationDao.findOrganisationByGroupId(notFound);
        assertTrue(result.isEmpty());
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
        input.setDeviceWebhookEndpoint(UUID.randomUUID().toString());
        input.setDeviceWebhookEndpointKey(UUID.randomUUID().toString());


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
        assertEquals(input.getDeviceWebhookEndpoint(), dbOrganisation.getDeviceWebhookEndpoint());
        assertEquals(input.getDeviceWebhookEndpointKey(), dbOrganisation.getDeviceWebhookEndpointKey());
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
        assertNull(result.getDeviceWebhookEndpoint());
        assertNull(result.getDeviceWebhookEndpointKey());
    }

    @Test
    public void testFindOrganisationByApiKeyNotFound() {
        var result = organisationDao.findOrganisationByHistoryApiKey("NOT FOUND");

        assertNull(result);
    }
}

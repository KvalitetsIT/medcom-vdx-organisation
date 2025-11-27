package dk.medcom.vdx.organisation.service;

import dk.medcom.vdx.organisation.dao.GroupsDao;
import dk.medcom.vdx.organisation.dao.OrganisationDao;
import dk.medcom.vdx.organisation.dao.entity.Organisation;
import dk.medcom.vdx.organisation.service.exception.DaoException;
import dk.medcom.vdx.organisation.service.exception.InvalidDataException;
import dk.medcom.vdx.organisation.service.exception.OrganisationNotFoundException;
import dk.medcom.vdx.organisation.service.model.OrganisationCreateBasic;
import dk.medcom.vdx.organisation.service.model.OrganisationCreate;
import dk.medcom.vdx.organisation.service.model.OrganisationUpdate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

public class OrganisationServiceImplTest {
    private OrganisationNameService organisationService;
    private OrganisationDao organisationDao;
    private GroupsDao groupsDao;

    @BeforeEach
    public void setup() {
        organisationDao = Mockito.mock(OrganisationDao.class);
        groupsDao = Mockito.mock(GroupsDao.class);

        organisationService = new OrganisationNameService(organisationDao, groupsDao);
    }

    private void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(organisationDao, groupsDao);
    }

    @Test
    public void testCreateOrganisationBasicParentNotFound() {
        var organisation = new OrganisationCreateBasic("org", "name", "parent");

        var result = assertThrows(InvalidDataException.class, () -> organisationService.createOrganisationBasic(organisation));
        assertNotNull(result);
        assertEquals("Parent organisation parent not found", result.getMessage());

        Mockito.verify(organisationDao, times(1)).findOrganisation(organisation.parentOrganisation());
        Mockito.verify(organisationDao, times(1)).findOrganisation(organisation.organisationCode());

        verifyNoMoreInteractions();
    }

    @Test
    public void testCreateOrganisationBasicAlreadyExists() {
        var organisation = new OrganisationCreateBasic("org", "name", "parent");

        Mockito.when(organisationDao.findOrganisation(organisation.organisationCode())).thenReturn(new Organisation());

        var result = assertThrows(InvalidDataException.class, () -> organisationService.createOrganisationBasic(organisation));
        assertNotNull(result);
        assertEquals("Organisation org already exists.", result.getMessage());

        Mockito.verify(organisationDao, times(1)).findOrganisation(organisation.organisationCode());

        verifyNoMoreInteractions();
    }

    @Test
    public void testCreateOrganisationBasic() {
        var code = "org";
        var name = "name";
        var organisation = new OrganisationCreateBasic(code, name, "parent");

        var parentOrganisation = new Organisation();
        parentOrganisation.setGroupId(1L);
        parentOrganisation.setParentId(null);
        parentOrganisation.setPoolSize(2);
        parentOrganisation.setOrganisationId(organisation.parentOrganisation());
        parentOrganisation.setGroupName("group name");
        parentOrganisation.setOrganisationName("org name");
        parentOrganisation.setSmsSenderName("sms sender");
        parentOrganisation.setSmsCallbackUrl("sms callback");

        var newOrganisation = new Organisation();
        newOrganisation.setOrganisationName(organisation.name());
        newOrganisation.setGroupId(10L);
        newOrganisation.setSmsSenderName(null);
        newOrganisation.setAllowCustomUriWithoutDomain(false);
        newOrganisation.setSmsCallbackUrl(null);
        newOrganisation.setGroupId(10L);
        newOrganisation.setPoolSize(null);
        newOrganisation.setOrganisationId(organisation.organisationCode());

        Mockito.when(organisationDao.findOrganisation(code)).thenReturn(null, newOrganisation);
        Mockito.when(organisationDao.findOrganisation(organisation.parentOrganisation())).thenReturn(parentOrganisation);
        Mockito.when(groupsDao.insert(Mockito.any())).thenReturn(10L);

        var result = organisationService.createOrganisationBasic(organisation);

        assertNotNull(result);

        Mockito.verify(organisationDao, times(2)).findOrganisation(code);
        Mockito.verify(organisationDao, times(1)).findOrganisation(organisation.parentOrganisation());
        Mockito.verify(organisationDao, times(1)).insert(Mockito.argThat(x -> {
            assertEquals(10L, x.getGroupId());
            assertNull( x.getPoolSize());
            assertEquals(code, x.getOrganisationId());
            assertEquals(name, x.getOrganisationName());
            assertNull(x.getSmsSenderName());
            assertNull(x.getSmsCallbackUrl());

            return true;
        }));

        Mockito.verify(groupsDao, times(1)).insert(Mockito.argThat(x -> {
            assertEquals(name, x.groupName());
            assertEquals(2, x.groupType());
            assertEquals(parentOrganisation.getGroupId(), x.parentId());
            assertEquals("system", x.createdBy());
            assertNull(x.groupId());
            assertEquals(name, x.groupName());

            return true;
        }));

        verifyNoMoreInteractions();
    }

    @Test
    public void testCreateOrganisationParentNotFound() {
        var organisation = randomOrganisationCreateExt();

        var result = assertThrows(InvalidDataException.class, () -> organisationService.createOrganisation(organisation));
        assertNotNull(result);
        assertEquals("Parent organisation %s not found".formatted(organisation.parentOrganisation()), result.getMessage());

        Mockito.verify(organisationDao, times(1)).findOrganisation(organisation.parentOrganisation());
        Mockito.verify(organisationDao, times(1)).findOrganisation(organisation.organisationCode());

        verifyNoMoreInteractions();
    }

    @Test
    public void testCreateOrganisationAlreadyExists() {
        var organisation = randomOrganisationCreateExt();

        Mockito.when(organisationDao.findOrganisation(organisation.organisationCode())).thenReturn(new Organisation());

        var result = assertThrows(InvalidDataException.class, () -> organisationService.createOrganisation(organisation));
        assertNotNull(result);
        assertEquals("Organisation %s already exists.".formatted(organisation.organisationCode()), result.getMessage());

        Mockito.verify(organisationDao, times(1)).findOrganisation(organisation.organisationCode());

        verifyNoMoreInteractions();
    }

    @Test
    public void testCreateOrganisationFailedToInsert() {
        var organisation = randomOrganisationCreateExt();
        var parentOrganisation = randomOrganisation();

        Mockito.when(organisationDao.findOrganisation(organisation.parentOrganisation())).thenReturn(parentOrganisation);
        Mockito.when(organisationDao.findOrganisation(organisation.organisationCode())).thenReturn(null);
        Mockito.when(groupsDao.insert(Mockito.any())).thenReturn(11L);

        var result = assertThrows(DaoException.class, () -> organisationService.createOrganisation(organisation));
        assertNotNull(result);
        assertEquals("Failed to find organisation %s after creation.".formatted(organisation.organisationCode()), result.getMessage());

        Mockito.verify(organisationDao, times(1)).findOrganisation(organisation.parentOrganisation());
        Mockito.verify(organisationDao, times(2)).findOrganisation(organisation.organisationCode());
        Mockito.verify(organisationDao, times(1)).insert(Mockito.argThat(x -> {
            assertEquals(11L, x.getGroupId());
            assertEquals(organisation.organisationCode(), x.getOrganisationId());
            assertEquals(organisation.name(), x.getOrganisationName());
            assertEquals(organisation.poolSize(), x.getPoolSize());
            assertEquals(organisation.smsSenderName(), x.getSmsSenderName());
            assertEquals(organisation.allowCustomUriWithoutDomain(), x.isAllowCustomUriWithoutDomain());
            assertEquals(organisation.smsCallbackUrl(), x.getSmsCallbackUrl());
            assertEquals(organisation.deviceWebhookEndpoint(), x.getDeviceWebhookEndpoint());
            assertEquals(organisation.deviceWebhookEndpointKey(), x.getDeviceWebhookEndpointKey());

            return true;
        }));

        Mockito.verify(groupsDao, times(1)).insert(Mockito.argThat(x -> {
            assertEquals(organisation.name(), x.groupName());
            assertEquals(2, x.groupType());
            assertEquals(parentOrganisation.getGroupId(), x.parentId());
            assertEquals("system", x.createdBy());
            assertNull(x.groupId());

            return true;
        }));

        verifyNoMoreInteractions();
    }

    @Test
    public void testCreateOrganisation() {
        var organisation = randomOrganisationCreateExt();
        var parentOrganisation = randomOrganisation();
        var newOrganisation = randomOrganisation();

        Mockito.when(organisationDao.findOrganisation(organisation.organisationCode())).thenReturn(null, newOrganisation);
        Mockito.when(organisationDao.findOrganisation(organisation.parentOrganisation())).thenReturn(parentOrganisation);
        Mockito.when(groupsDao.insert(Mockito.any())).thenReturn(10L);

        var result = organisationService.createOrganisation(organisation);

        assertNotNull(result);

        Mockito.verify(organisationDao, times(2)).findOrganisation(organisation.organisationCode());
        Mockito.verify(organisationDao, times(1)).findOrganisation(organisation.parentOrganisation());
        Mockito.verify(organisationDao, times(1)).insert(Mockito.argThat(x -> {
            assertEquals(10L, x.getGroupId());
            assertEquals(organisation.organisationCode(), x.getOrganisationId());
            assertEquals(organisation.name(), x.getOrganisationName());
            assertEquals(organisation.poolSize(), x.getPoolSize());
            assertEquals(organisation.smsSenderName(), x.getSmsSenderName());
            assertEquals(organisation.allowCustomUriWithoutDomain(), x.isAllowCustomUriWithoutDomain());
            assertEquals(organisation.smsCallbackUrl(), x.getSmsCallbackUrl());
            assertEquals(organisation.deviceWebhookEndpoint(), x.getDeviceWebhookEndpoint());
            assertEquals(organisation.deviceWebhookEndpointKey(), x.getDeviceWebhookEndpointKey());

            return true;
        }));

        Mockito.verify(groupsDao, times(1)).insert(Mockito.argThat(x -> {
            assertEquals(organisation.name(), x.groupName());
            assertEquals(2, x.groupType());
            assertEquals(parentOrganisation.getGroupId(), x.parentId());
            assertEquals("system", x.createdBy());
            assertNull(x.groupId());

            return true;
        }));

        verifyNoMoreInteractions();
    }

    @Test
    public void testUpdateOrganisation() {
        var code = randomString();
        var organisation = new OrganisationUpdate(123, randomString(), true, randomString(), randomString(), randomString(), randomString());
        var updatedOrganisation = randomOrganisation();

        Mockito.when(organisationDao.findOrganisation(code)).thenReturn(updatedOrganisation);
        Mockito.when(organisationDao.update(Mockito.any())).thenReturn(true);

        var result = organisationService.updateOrganisation(code, organisation);

        assertNotNull(result);

        Mockito.verify(organisationDao).findOrganisation(code);
        Mockito.verify(organisationDao).update(Mockito.argThat(x -> {
            assertNull(x.getGroupId());
            assertEquals(code, x.getOrganisationId());
            assertEquals(organisation.poolSize(), x.getPoolSize());
            assertEquals(organisation.smsSenderName(), x.getSmsSenderName());
            assertEquals(organisation.allowCustomUriWithoutDomain(), x.isAllowCustomUriWithoutDomain());
            assertEquals(organisation.smsCallbackUrl(), x.getSmsCallbackUrl());
            assertEquals(organisation.historyApiKey(), x.getHistoryApiKey());
            assertEquals(organisation.deviceWebhookEndpoint(), x.getDeviceWebhookEndpoint());
            assertEquals(organisation.deviceWebhookEndpointKey(), x.getDeviceWebhookEndpointKey());

            return true;
        }));

        verifyNoMoreInteractions();
    }

    @Test
    public void testUpdateOrganisationOrganisationNotFound() {
        var code = randomString();
        var organisation = new OrganisationUpdate(123, randomString(), true, randomString(), randomString(), randomString(), randomString());

        Mockito.when(organisationDao.findOrganisation(code)).thenReturn(null);
        Mockito.when(organisationDao.update(Mockito.any())).thenReturn(false);

        var expectedException = assertThrows(OrganisationNotFoundException.class, () -> organisationService.updateOrganisation(code, organisation));
        assertNotNull(expectedException);
        assertEquals("Organisation with code %s not found.".formatted(code), expectedException.getMessage());

        Mockito.verify(organisationDao).findOrganisation(code);
        Mockito.verify(organisationDao).update(Mockito.argThat(x -> {
            assertNull(x.getGroupId());
            assertEquals(code, x.getOrganisationId());
            assertEquals(organisation.poolSize(), x.getPoolSize());
            assertEquals(organisation.smsSenderName(), x.getSmsSenderName());
            assertEquals(organisation.allowCustomUriWithoutDomain(), x.isAllowCustomUriWithoutDomain());
            assertEquals(organisation.smsCallbackUrl(), x.getSmsCallbackUrl());
            assertEquals(organisation.historyApiKey(), x.getHistoryApiKey());
            assertEquals(organisation.deviceWebhookEndpoint(), x.getDeviceWebhookEndpoint());
            assertEquals(organisation.deviceWebhookEndpointKey(), x.getDeviceWebhookEndpointKey());

            return true;
        }));

        verifyNoMoreInteractions();
    }

    @Test
    public void testUpdateOrganisationFails() {
        var code = randomString();
        var organisation = new OrganisationUpdate(123, randomString(), true, randomString(), randomString(), randomString(), randomString());
        var updatedOrganisation = randomOrganisation();

        Mockito.when(organisationDao.findOrganisation(code)).thenReturn(updatedOrganisation);
        Mockito.when(organisationDao.update(Mockito.any())).thenReturn(false);

        var expectedException = assertThrows(DaoException.class, () -> organisationService.updateOrganisation(code, organisation));
        assertNotNull(expectedException);
        assertEquals("Failed to update organisation with code %s.".formatted(code), expectedException.getMessage());

        Mockito.verify(organisationDao).findOrganisation(code);
        Mockito.verify(organisationDao).update(Mockito.argThat(x -> {
            assertNull(x.getGroupId());
            assertEquals(code, x.getOrganisationId());
            assertEquals(organisation.poolSize(), x.getPoolSize());
            assertEquals(organisation.smsSenderName(), x.getSmsSenderName());
            assertEquals(organisation.allowCustomUriWithoutDomain(), x.isAllowCustomUriWithoutDomain());
            assertEquals(organisation.smsCallbackUrl(), x.getSmsCallbackUrl());
            assertEquals(organisation.historyApiKey(), x.getHistoryApiKey());
            assertEquals(organisation.deviceWebhookEndpoint(), x.getDeviceWebhookEndpoint());
            assertEquals(organisation.deviceWebhookEndpointKey(), x.getDeviceWebhookEndpointKey());

            return true;
        }));

        verifyNoMoreInteractions();
    }

    private OrganisationCreate randomOrganisationCreateExt() {
        return new OrganisationCreate(
                randomString(),
                randomString(),
                randomString(),
                123,
                randomString(),
                true,
                randomString(),
                randomString(),
                randomString(),
                randomString());
    }

    private Organisation randomOrganisation() {
        var organisation = new Organisation();
        organisation.setGroupId(1000L);
        organisation.setOrganisationId(randomString());
        organisation.setOrganisationName(randomString());
        organisation.setSmsSenderName(randomString());
        organisation.setAllowCustomUriWithoutDomain(false);
        organisation.setSmsCallbackUrl(randomString());
        organisation.setPoolSize(12345);
        organisation.setDeviceWebhookEndpoint(randomString());
        organisation.setDeviceWebhookEndpointKey(randomString());
        return organisation;
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }
}

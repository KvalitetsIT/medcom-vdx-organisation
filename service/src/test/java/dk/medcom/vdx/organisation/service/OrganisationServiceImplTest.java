package dk.medcom.vdx.organisation.service;

import dk.medcom.vdx.organisation.dao.GroupsDao;
import dk.medcom.vdx.organisation.dao.OrganisationDao;
import dk.medcom.vdx.organisation.dao.jpa.OrganisationRepository;
import dk.medcom.vdx.organisation.dao.jpa.entity.Organisation;
import dk.medcom.vdx.organisation.service.exception.InvalidDataException;
import dk.medcom.vdx.organisation.service.model.OrganisationCreate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

public class OrganisationServiceImplTest {
    private OrganisationNameService organisationService;
    private OrganisationRepository organisationRepository;
    private OrganisationDao organisationDao;
    private GroupsDao groupsDao;

    @Before
    public void setup() {
        organisationRepository = Mockito.mock(OrganisationRepository.class);
        organisationDao = Mockito.mock(OrganisationDao.class);
        groupsDao = Mockito.mock(GroupsDao.class);

        organisationService = new OrganisationNameService(organisationRepository, organisationDao, groupsDao);
    }

    @Test
    public void testCreateOrganisationParentNotFound() {
        var organisation = new OrganisationCreate("org", "name", "parent");

        var result = assertThrows(InvalidDataException.class, () -> organisationService.createOrganisation(organisation));
        assertNotNull(result);
        assertEquals("Parent organisation parent not found", result.getMessage());

        Mockito.verify(organisationDao, times(1)).findOrganisation(organisation.parentOrganisation());
        Mockito.verify(organisationRepository, times(1)).findByOrganisationId(organisation.organisationCode());

        Mockito.verifyNoMoreInteractions(organisationRepository, organisationDao, groupsDao);
    }

    @Test
    public void testCreateOrganisationAlreadyExists() {
        var organisation = new OrganisationCreate("org", "name", "parent");

        Mockito.when(organisationRepository.findByOrganisationId(organisation.organisationCode())).thenReturn(new Organisation());

        var result = assertThrows(InvalidDataException.class, () -> organisationService.createOrganisation(organisation));
        assertNotNull(result);
        assertEquals("Organisation org already exists.", result.getMessage());

        Mockito.verify(organisationRepository, times(1)).findByOrganisationId(organisation.organisationCode());

        Mockito.verifyNoMoreInteractions(organisationRepository, organisationDao, groupsDao);
    }

    @Test
    public void testCreateOrganisation() {
        var code = "org";
        var name = "name";
        var organisation = new OrganisationCreate(code, name, "parent");

        var parentOrganisation = new dk.medcom.vdx.organisation.dao.entity.Organisation();
        parentOrganisation.setGroupId(1L);
        parentOrganisation.setParentId(null);
        parentOrganisation.setPoolSize(2);
        parentOrganisation.setOrganisationId(organisation.parentOrganisation());
        parentOrganisation.setGroupName("group name");
        parentOrganisation.setOrganisationName("org name");
        parentOrganisation.setSmsSenderName("sms sender");
        parentOrganisation.setSmsCallbackUrl("sms callback");

        var newOrganisation = new Organisation();
        newOrganisation.setId(5L);
        newOrganisation.setName(organisation.name());
        newOrganisation.setGroupId(10L);
        newOrganisation.setSmsSenderName(null);
        newOrganisation.setAllowCustomUriWithoutDomain(false);
        newOrganisation.setSmsCallbackUrl(null);
        newOrganisation.setGroupId(10L);
        newOrganisation.setPoolSize(null);
        newOrganisation.setOrganisationId(organisation.organisationCode());

        Mockito.when(organisationRepository.findByOrganisationId(code)).thenReturn(null, newOrganisation);
        Mockito.when(organisationDao.findOrganisation(organisation.parentOrganisation())).thenReturn(parentOrganisation);
        Mockito.when(groupsDao.insert(Mockito.any())).thenReturn(10L);

        var result = organisationService.createOrganisation(organisation);

        assertNotNull(result);

        Mockito.verify(organisationRepository, times(2)).findByOrganisationId(code);
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

        Mockito.verifyNoMoreInteractions(organisationRepository, groupsDao, organisationDao);
    }
}

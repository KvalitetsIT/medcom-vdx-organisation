package dk.medcom.vdx.organisation.service;

import dk.medcom.vdx.organisation.dao.OrganisationDao;
import dk.medcom.vdx.organisation.dao.OrganisationTemplateDao;
import dk.medcom.vdx.organisation.dao.entity.OrganisationTemplate;
import dk.medcom.vdx.organisation.dao.GroupsDao;
import dk.medcom.vdx.organisation.dao.jpa.OrganisationRepository;
import dk.medcom.vdx.organisation.dao.jpa.entity.Organisation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

public class OrganisationServiceImplTest {
    private OrganisationNameService organisationService;
    private OrganisationRepository organisationRepository;
    private OrganisationTemplateDao organisationTemplateDao;
    private OrganisationDao organisationDao;
    private GroupsDao groupsDao;

    @Before
    public void setup() {
        organisationRepository = Mockito.mock(OrganisationRepository.class);
        organisationTemplateDao = Mockito.mock(OrganisationTemplateDao.class);
        organisationDao = Mockito.mock(OrganisationDao.class);
        groupsDao = Mockito.mock(GroupsDao.class);

        organisationService = new OrganisationNameService(organisationRepository, organisationTemplateDao, organisationDao, groupsDao);
    }

    @Test
    public void testGetOrganisationOrCreateFromTemplateNotFound() {
        var code = "not found";

        var result = organisationService.getOrganisationOrCreateFromTemplate(code);
        assertNotNull(result);
        assertTrue(result.isEmpty());

        Mockito.verify(organisationRepository, times(1)).findByOrganisationId(code);
    }

    @Test
    public void testGetOrganisationOrCreateFromTemplateOrganisationFound() {
        var code = "not found";

        Organisation organisation = new Organisation();
        organisation.setId(1L);
        organisation.setOrganisationId(code);
        organisation.setName("name");
        organisation.setPoolSize(2);
        organisation.setGroupId(1);
        organisation.setSmsSenderName("sender name");
        organisation.setAllowCustomUriWithoutDomain(true);
        organisation.setSmsCallbackUrl("callback");

        Mockito.when(organisationRepository.findByOrganisationId(code)).thenReturn(organisation);

        var result = organisationService.getOrganisationOrCreateFromTemplate(code);
        assertNotNull(result);
        assertTrue(result.isPresent());

        assertEquals(result.get(), organisation);

        Mockito.verify(organisationRepository, times(1)).findByOrganisationId(code);

        Mockito.verifyNoMoreInteractions(organisationRepository, organisationTemplateDao);
    }

    @Test
    public void testGetOrganisationOrCreateFromTemplateTemplateFound() {
        var code = "not found";

        var organisationTemplate = new OrganisationTemplate(1L, code, "name", "parent");
        var parentOrganisation = new dk.medcom.vdx.organisation.dao.entity.Organisation();
        parentOrganisation.setGroupId(1L);
        parentOrganisation.setParentId(null);
        parentOrganisation.setPoolSize(2);
        parentOrganisation.setOrganisationId(organisationTemplate.parentOrganisationCode());
        parentOrganisation.setGroupName("group name");
        parentOrganisation.setOrganisationName("org name");
        parentOrganisation.setSmsSenderName("sms sender");
        parentOrganisation.setSmsCallbackUrl("sms callback");

        var newOrganisation = new Organisation();
        newOrganisation.setId(5L);
        newOrganisation.setName(organisationTemplate.name());
        newOrganisation.setGroupId(10L);
        newOrganisation.setSmsSenderName(null);
        newOrganisation.setAllowCustomUriWithoutDomain(false);
        newOrganisation.setSmsCallbackUrl(null);
        newOrganisation.setGroupId(10L);
        newOrganisation.setPoolSize(null);
        newOrganisation.setOrganisationId(code);

        Mockito.when(organisationTemplateDao.findOrganisationTemplate(code)).thenReturn(Optional.of(organisationTemplate));
        Mockito.when(organisationDao.findOrganisation(organisationTemplate.parentOrganisationCode())).thenReturn(parentOrganisation);
        Mockito.when(groupsDao.insert(Mockito.any())).thenReturn(10L);
        Mockito.when(organisationRepository.findByOrganisationId(code)).thenReturn(null, newOrganisation);

        var result = organisationService.getOrganisationOrCreateFromTemplate(code);
        assertNotNull(result);
        assertTrue(result.isPresent());

        Mockito.verify(organisationRepository, times(2)).findByOrganisationId(code);
        Mockito.verify(organisationTemplateDao, times(1)).findOrganisationTemplate(code);
        Mockito.verify(organisationDao, times(1)).findOrganisation(organisationTemplate.parentOrganisationCode());
        Mockito.verify(organisationDao, times(1)).insert(Mockito.argThat(x -> {
            assertEquals(10L, x.getGroupId());
            assertNull( x.getPoolSize());
            assertEquals(code, x.getOrganisationId());
            assertEquals(organisationTemplate.name(), x.getOrganisationName());
            assertNull(x.getSmsSenderName());
            assertNull(x.getSmsCallbackUrl());

            return true;
        }));
        Mockito.verify(groupsDao, times(1)).insert(Mockito.argThat(x -> {
            assertEquals(organisationTemplate.name(), x.groupName());
            assertEquals(2, x.groupType());
            assertEquals(parentOrganisation.getGroupId(), x.parentId());
            assertEquals("system", x.createdBy());
            assertNull(x.groupId());
            assertEquals(organisationTemplate.name(), x.groupName());
            assertEquals(organisationTemplate.name(), x.groupName());
            assertEquals(organisationTemplate.name(), x.groupName());

            return true;
        }));

        Mockito.verifyNoMoreInteractions(organisationRepository, organisationTemplateDao, groupsDao, organisationDao);
    }
}

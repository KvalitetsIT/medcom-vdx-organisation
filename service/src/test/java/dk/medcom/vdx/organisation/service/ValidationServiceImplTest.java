package dk.medcom.vdx.organisation.service;

import dk.medcom.vdx.organisation.context.UserContextImpl;
import dk.medcom.vdx.organisation.context.UserContextService;
import dk.medcom.vdx.organisation.dao.OrganisationDao;
import dk.medcom.vdx.organisation.dao.entity.OrganisationGroupJoin;
import dk.medcom.vdx.organisation.service.exception.OrganisationNotFoundException;
import dk.medcom.vdx.organisation.service.exception.OrganisationNotInTreeException;
import dk.medcom.vdx.organisation.service.impl.ValidationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidationServiceImplTest {

    private ValidationService validationService;
    private UserContextService userContextService;
    private OrganisationDao organisationDao;

    @BeforeEach
    void setup() {
        userContextService = Mockito.mock(UserContextService.class);
        organisationDao = Mockito.mock(OrganisationDao.class);

        validationService = new ValidationServiceImpl(userContextService, organisationDao);
    }

    @Test
    void testValidateAncestorList() {
        var ancestorList = List.of(randomOrganisationGroupJoin(randomString()), randomOrganisationGroupJoin(randomString()), randomOrganisationGroupJoin("user-org"));
        Mockito.when(userContextService.getUserContext()).thenReturn(new UserContextImpl("user-org", "email", List.of()));

        validationService.validateAncestorList(ancestorList);

        Mockito.verify(userContextService).getUserContext();
        Mockito.verifyNoMoreInteractions(userContextService, organisationDao);
    }

    @Test
    void testValidateAncestorListNotInTree() {
        var ancestorList = List.of(randomOrganisationGroupJoin(randomString()), randomOrganisationGroupJoin(randomString()), randomOrganisationGroupJoin(randomString()));
        Mockito.when(userContextService.getUserContext()).thenReturn(new UserContextImpl("user-org", "email", List.of()));

        var expectedException = assertThrows(OrganisationNotInTreeException.class, () -> validationService.validateAncestorList(ancestorList));
        assertEquals("UserContext organisation is not a parent of the requested organisation.", expectedException.getMessage());

        Mockito.verify(userContextService).getUserContext();
        Mockito.verifyNoMoreInteractions(userContextService, organisationDao);
    }

    @Test
    void testValidateOrganisationWhenOrganisationAndUserOrganisationByCodeIsTheSame() {
        var requestOrganisation = randomString();
        Mockito.when(userContextService.getUserContext()).thenReturn(new UserContextImpl(requestOrganisation, "email", List.of()));

        validationService.validateOrganisationByCode(requestOrganisation);

        Mockito.verify(userContextService).getUserContext();
        Mockito.verifyNoMoreInteractions(userContextService, organisationDao);
    }

    @Test
    void testValidateOrganisationByCode() {
        var requestOrganisation = randomString();
        Mockito.when(userContextService.getUserContext()).thenReturn(new UserContextImpl("user-org", "email", List.of()));

        Mockito.when(organisationDao.findAncestorsOfOrganisation(requestOrganisation)).thenReturn(List.of(randomOrganisationGroupJoin(requestOrganisation), randomOrganisationGroupJoin(randomString()), randomOrganisationGroupJoin("user-org")));

        validationService.validateOrganisationByCode(requestOrganisation);

        Mockito.verify(userContextService).getUserContext();
        Mockito.verify(organisationDao).findAncestorsOfOrganisation(requestOrganisation);
        Mockito.verifyNoMoreInteractions(userContextService, organisationDao);
    }

    @Test
    void testValidateOrganisationWhenOrganisationByCodeNotExist() {
        var requestOrganisation = randomString();
        Mockito.when(userContextService.getUserContext()).thenReturn(new UserContextImpl("user-org", "email", List.of()));

        Mockito.when(organisationDao.findAncestorsOfOrganisation(requestOrganisation)).thenReturn(List.of());

        var expectedException = assertThrows(OrganisationNotFoundException.class, () -> validationService.validateOrganisationByCode(requestOrganisation));
        assertEquals("Organisation with code %s not found.".formatted(requestOrganisation), expectedException.getMessage());

        Mockito.verify(userContextService).getUserContext();
        Mockito.verify(organisationDao).findAncestorsOfOrganisation(requestOrganisation);
        Mockito.verifyNoMoreInteractions(userContextService, organisationDao);
    }

    @Test
    void testValidateOrganisationByCodeNotInTree() {
        var requestOrganisation = randomString();
        Mockito.when(userContextService.getUserContext()).thenReturn(new UserContextImpl("user-org", "email", List.of()));

        Mockito.when(organisationDao.findAncestorsOfOrganisation(requestOrganisation)).thenReturn(List.of(randomOrganisationGroupJoin(requestOrganisation), randomOrganisationGroupJoin(randomString()), randomOrganisationGroupJoin(randomString())));

        var expectedException = assertThrows(OrganisationNotInTreeException.class, () -> validationService.validateOrganisationByCode(requestOrganisation));
        assertEquals("UserContext organisation is not a parent of the requested organisation.", expectedException.getMessage());

        Mockito.verify(userContextService).getUserContext();
        Mockito.verify(organisationDao).findAncestorsOfOrganisation(requestOrganisation);
        Mockito.verifyNoMoreInteractions(userContextService, organisationDao);
    }

    @Test
    void testValidateOrganisationByGroupId() {
        var groupId = 345;
        Mockito.when(userContextService.getUserContext()).thenReturn(new UserContextImpl("user-org", "email", List.of()));

        Mockito.when(organisationDao.findAncestorsOfOrganisation(groupId)).thenReturn(List.of(randomOrganisationGroupJoin(randomString()), randomOrganisationGroupJoin(randomString()), randomOrganisationGroupJoin("user-org")));

        validationService.validateOrganisationByGroupId(groupId);

        Mockito.verify(userContextService).getUserContext();
        Mockito.verify(organisationDao).findAncestorsOfOrganisation(groupId);
        Mockito.verifyNoMoreInteractions(userContextService, organisationDao);
    }

    @Test
    void testValidateOrganisationWhenOrganisationByGroupIdNotExist() {
        var groupId = 234;
        Mockito.when(userContextService.getUserContext()).thenReturn(new UserContextImpl("user-org", "email", List.of()));

        Mockito.when(organisationDao.findAncestorsOfOrganisation(groupId)).thenReturn(List.of());

        var expectedException = assertThrows(OrganisationNotFoundException.class, () -> validationService.validateOrganisationByGroupId(groupId));
        assertEquals("Group with id %s not found.".formatted(groupId), expectedException.getMessage());

        Mockito.verify(userContextService).getUserContext();
        Mockito.verify(organisationDao).findAncestorsOfOrganisation(groupId);
        Mockito.verifyNoMoreInteractions(userContextService, organisationDao);
    }

    @Test
    void testValidateOrganisationByGroupIdNotInTree() {
        var groupId = 123;
        Mockito.when(userContextService.getUserContext()).thenReturn(new UserContextImpl("user-org", "email", List.of()));

        Mockito.when(organisationDao.findAncestorsOfOrganisation(groupId)).thenReturn(List.of(randomOrganisationGroupJoin(randomString()), randomOrganisationGroupJoin(randomString()), randomOrganisationGroupJoin(randomString())));

        var expectedException = assertThrows(OrganisationNotInTreeException.class, () -> validationService.validateOrganisationByGroupId(groupId));
        assertEquals("UserContext organisation is not a parent of the requested organisation.", expectedException.getMessage());

        Mockito.verify(userContextService).getUserContext();
        Mockito.verify(organisationDao).findAncestorsOfOrganisation(groupId);
        Mockito.verifyNoMoreInteractions(userContextService, organisationDao);
    }


    private OrganisationGroupJoin randomOrganisationGroupJoin(String organisationId) {
        return new OrganisationGroupJoin(123L, 321L, 234, randomString(), organisationId, randomString(), randomString(), true, randomString(), randomString(), randomString(), randomString());
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }
}

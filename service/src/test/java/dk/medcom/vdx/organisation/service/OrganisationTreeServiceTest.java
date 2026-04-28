package dk.medcom.vdx.organisation.service;

import dk.medcom.vdx.organisation.dao.OrganisationDao;
import dk.medcom.vdx.organisation.dao.entity.Organisation;
import dk.medcom.vdx.organisation.dao.entity.OrganisationGroupJoin;
import dk.medcom.vdx.organisation.service.exception.InvalidDataException;
import dk.medcom.vdx.organisation.service.exception.OrganisationNotFoundException;
import dk.medcom.vdx.organisation.service.exception.OrganisationNotInTreeException;
import dk.medcom.vdx.organisation.service.impl.OrganisationTreeServiceImpl;
import dk.medcom.vdx.organisation.service.model.OrganisationModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

public class OrganisationTreeServiceTest {
    private OrganisationTreeService organisationTreeService;
    private ValidationService validationService;
    private OrganisationDao organisationDao;

    @BeforeEach
    public void setup() {
        validationService = Mockito.mock(ValidationService.class);
        organisationDao = Mockito.mock(OrganisationDao.class);
        organisationTreeService = new OrganisationTreeServiceImpl(validationService, organisationDao);
    }

    private void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(validationService, organisationDao);
    }

    @Test
    public void testGetOrganisationTree() {
        String input = "child";

        var child = createOrganisation(13, 12, null);
        Mockito.when(organisationDao.findOrganisation("child")).thenReturn(child);

        var childOne = createOrganisation(12, 11, null);
        Mockito.when(organisationDao.findOrganisationByGroupId(child.getParentId())).thenReturn(Optional.of(childOne));

        var parent = createOrganisation(11, 10, 20);
        Mockito.when(organisationDao.findOrganisationByGroupId(childOne.getParentId())).thenReturn(Optional.of(parent));

        var superParent = createOrganisation(10, null, null);
        Mockito.when(organisationDao.findOrganisationByGroupId(parent.getParentId())).thenReturn(Optional.of(superParent));

        var result = organisationTreeService.findOrganisations(input).orElseThrow(RuntimeException::new);

        assertEquals(4, result.size());
        assertOrganisation(result.stream().filter(x -> x.getGroupId() == 13L).findFirst(), 13, 12L, null);
        assertOrganisation(result.stream().filter(x -> x.getGroupId() == 12L).findFirst(), 12, 11L, null);
        assertOrganisation(result.stream().filter(x -> x.getGroupId() == 11L).findFirst(), 11, 10L, 20);
        assertOrganisation(result.stream().filter(x -> x.getGroupId() == 10L).findFirst(), 10, null, null);
    }

    @Test
    public void testGetOrganisationTreeByGroupId() {
        var input = 13;

        var child = createOrganisation(13, 12, null);
        Mockito.when(organisationDao.findOrganisationByGroupId(input)).thenReturn(Optional.of(child));

        var childOne = createOrganisation(12, 11, null);
        Mockito.when(organisationDao.findOrganisationByGroupId(child.getParentId())).thenReturn(Optional.of(childOne));

        var parent = createOrganisation(11, 10, 20);
        Mockito.when(organisationDao.findOrganisationByGroupId(childOne.getParentId())).thenReturn(Optional.of(parent));

        var superParent = createOrganisation(10, null, null);
        Mockito.when(organisationDao.findOrganisationByGroupId(parent.getParentId())).thenReturn(Optional.of(superParent));

        var result = organisationTreeService.getByGroupId(input).orElseThrow(RuntimeException::new);

        assertEquals(4, result.size());
        assertOrganisation(result.stream().filter(x -> x.getGroupId() == 13L).findFirst(), 13, 12L, null);
        assertOrganisation(result.stream().filter(x -> x.getGroupId() == 12L).findFirst(), 12, 11L, null);
        assertOrganisation(result.stream().filter(x -> x.getGroupId() == 11L).findFirst(), 11, 10L, 20);
        assertOrganisation(result.stream().filter(x -> x.getGroupId() == 10L).findFirst(), 10, null, null);
    }


    @Test
    public void testGetOrganisationTreeForApiKey() {
        String apiKey = "api-key";
        String apiKeyType = "history";

        var child = createOrganisation(13, 12, null);
        Mockito.when(organisationDao.findOrganisationByHistoryApiKey(apiKey)).thenReturn(child);

        var childOne = createOrganisation(12, 11, null);
        Mockito.when(organisationDao.findOrganisationByGroupId(child.getParentId())).thenReturn(Optional.of(childOne));

        var parent = createOrganisation(11, 10, 20);
        Mockito.when(organisationDao.findOrganisationByGroupId(childOne.getParentId())).thenReturn(Optional.of(parent));

        var superParent = createOrganisation(10, null, null);
        Mockito.when(organisationDao.findOrganisationByGroupId(parent.getParentId())).thenReturn(Optional.of(superParent));

        var result = organisationTreeService.findOrganisations(apiKeyType, apiKey).orElseThrow(RuntimeException::new);

        assertEquals(4, result.size());
        assertOrganisation(result.stream().filter(x -> x.getGroupId() == 13L).findFirst(), 13, 12L, null);
        assertOrganisation(result.stream().filter(x -> x.getGroupId() == 12L).findFirst(), 12, 11L, null);
        assertOrganisation(result.stream().filter(x -> x.getGroupId() == 11L).findFirst(), 11, 10L, 20);
        assertOrganisation(result.stream().filter(x -> x.getGroupId() == 10L).findFirst(), 10, null, null);
    }

    @Test
    public void testOrganisationNotFound() {
        String input = "not_found";

        Mockito.when(organisationDao.findOrganisation("input")).thenReturn(null);

        var result = organisationTreeService.findOrganisations(input);
        assertNotNull(result);
        assertTrue(result.isEmpty());

        Mockito.verify(organisationDao, times(1)).findOrganisation(input);
    }

    @Test
    public void testOrganisationForApiKeyNotFound() {
        String apiKey = "not_found";
        String apiKeyType = "history";

        Mockito.when(organisationDao.findOrganisationByHistoryApiKey(apiKey)).thenReturn(null);

        var result = organisationTreeService.findOrganisations(apiKeyType, apiKey);
        assertNotNull(result);
        assertTrue(result.isEmpty());

        Mockito.verify(organisationDao, times(1)).findOrganisationByHistoryApiKey(apiKey);
    }

    @Test
    public void testFindChildrenByGroupId() {
        var input = 1;

        var organisationOne = createOrganisation(1, null);
        var organisationTwo = createOrganisation(2, 1);
        var organisationThree = createOrganisation(3, 2);
        var organisationFour = createOrganisation(4, 3);
        var organisationFive = createOrganisation(5, 3);
        var organisationSix = createOrganisation(6, 1);

        Mockito.when(organisationDao.findOrganisationByGroupId(input)).thenReturn(Optional.of(organisationOne));
        Mockito.when(organisationDao.findOrganisationByParentId(organisationOne.getGroupId())).thenReturn(Arrays.asList(organisationTwo, organisationSix));
        Mockito.when(organisationDao.findOrganisationByParentId(organisationTwo.getGroupId())).thenReturn(Collections.singletonList(organisationThree));
        Mockito.when(organisationDao.findOrganisationByParentId(organisationThree.getGroupId())).thenReturn(Arrays.asList(organisationFour, organisationFive));
        Mockito.when(organisationDao.findOrganisationByParentId(organisationFour.getGroupId())).thenReturn(Collections.emptyList());
        Mockito.when(organisationDao.findOrganisationByParentId(organisationFive.getGroupId())).thenReturn(Collections.emptyList());

        var result = organisationTreeService.findChildrenByGroupId(input);

        assertNotNull(result);
        assertEquals(6, result.size());
        assertTrue(result.contains(organisationOne));
        assertTrue(result.contains(organisationTwo));
        assertTrue(result.contains(organisationThree));
        assertTrue(result.contains(organisationFour));
        assertTrue(result.contains(organisationFive));
        assertTrue(result.contains(organisationSix));
    }

    private Organisation createOrganisation(int groupId, Integer parentId) {
        return createOrganisation(groupId, parentId, null);
    }

    @Test
    public void testFindChildrenByGroupIdNotFound() {
        long input = 1L;
        Mockito.when(organisationDao.findOrganisationByGroupId(input)).thenReturn(Optional.empty());

        var result = organisationTreeService.findChildrenByGroupId((int) input);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        Mockito.verify(organisationDao).findOrganisationByGroupId(input);

        Mockito.verifyNoMoreInteractions(organisationDao);
    }

    @Test
    public void testFindChildrenByOrganisationCode() {
        var input = "organisation";

        var organisationOne = createOrganisation(1, null);
        var organisationTwo = createOrganisation(2, 1);
        var organisationThree = createOrganisation(3, 2);
        var organisationFour = createOrganisation(4, 3);
        var organisationFive = createOrganisation(5, 3);
        var organisationSix = createOrganisation(6, 1);

        Mockito.when(organisationDao.findOrganisation(input)).thenReturn(organisationOne);
        Mockito.when(organisationDao.findOrganisationByParentId(organisationOne.getGroupId())).thenReturn(Arrays.asList(organisationTwo, organisationSix));
        Mockito.when(organisationDao.findOrganisationByParentId(organisationTwo.getGroupId())).thenReturn(Collections.singletonList(organisationThree));
        Mockito.when(organisationDao.findOrganisationByParentId(organisationThree.getGroupId())).thenReturn(Arrays.asList(organisationFour, organisationFive));
        Mockito.when(organisationDao.findOrganisationByParentId(organisationFour.getGroupId())).thenReturn(Collections.emptyList());
        Mockito.when(organisationDao.findOrganisationByParentId(organisationFive.getGroupId())).thenReturn(Collections.emptyList());

        var result = organisationTreeService.findChildrenByOrganisationCode(input);

        assertNotNull(result);
        assertEquals(6, result.size());
        assertTrue(result.contains(organisationOne));
        assertTrue(result.contains(organisationTwo));
        assertTrue(result.contains(organisationThree));
        assertTrue(result.contains(organisationFour));
        assertTrue(result.contains(organisationFive));
        assertTrue(result.contains(organisationSix));
    }

    @Test
    public void testFindChildrenByOrganisationCodeNotFound() {
        var input = "organisation";
        Mockito.when(organisationDao.findOrganisation(input)).thenReturn(null);

        var result = organisationTreeService.findChildrenByOrganisationCode(input);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        Mockito.verify(organisationDao).findOrganisation(input);

        Mockito.verifyNoMoreInteractions(organisationDao);
    }

    @Test
    public void testOrganisationForUnknownApiKeyType() {
        String apiKey = "not_found";
        String apiKeyType = "invalid";

        var result = organisationTreeService.findOrganisations(apiKeyType, apiKey);
        assertNotNull(result);
        assertTrue(result.isEmpty());

        Mockito.verifyNoMoreInteractions(organisationDao);
    }

    @Test
    public void testFindAncestorsByCode() {
        var code = randomString();
        var ancestorList = List.of(randomOrganisationGroupJoin(), randomOrganisationGroupJoin());

        Mockito.when(organisationDao.findAncestorsOfOrganisation(code)).thenReturn(ancestorList);

        var result = organisationTreeService.findAncestorsByCode(code);
        assertNotNull(result);
        assertEquals(2, result.size());

        assertOrganisation(ancestorList.getFirst(), result.getFirst());
        assertOrganisation(ancestorList.getLast(), result.getLast());

        Mockito.verify(organisationDao).findAncestorsOfOrganisation(code);
        Mockito.verify(validationService).validateAncestorList(ancestorList);
        verifyNoMoreInteractions();
    }

    @Test
    public void testFindAncestorsByCodeNoOrganisationInDb() {
        var code = randomString();

        Mockito.when(organisationDao.findAncestorsOfOrganisation(code)).thenReturn(List.of());

        var expectedException = assertThrows(OrganisationNotFoundException.class, () -> organisationTreeService.findAncestorsByCode(code));
        assertNotNull(expectedException);
        assertEquals("Organisation with code %s not found.".formatted(code), expectedException.getMessage());

        Mockito.verify(organisationDao).findAncestorsOfOrganisation(code);
        verifyNoMoreInteractions();
    }

    @Test
    public void testFindAncestorsByCodeValidationFails() {
        var code = randomString();
        var ancestorList = List.of(randomOrganisationGroupJoin(), randomOrganisationGroupJoin());

        Mockito.when(organisationDao.findAncestorsOfOrganisation(code)).thenReturn(ancestorList);
        Mockito.doThrow(OrganisationNotInTreeException.class).when(validationService).validateAncestorList(ancestorList);

        var expectedException = assertThrows(OrganisationNotInTreeException.class, () -> organisationTreeService.findAncestorsByCode(code));
        assertNotNull(expectedException);

        Mockito.verify(organisationDao).findAncestorsOfOrganisation(code);
        Mockito.verify(validationService).validateAncestorList(ancestorList);
        verifyNoMoreInteractions();
    }

    @Test
    public void testFindAncestorsByGroupId() {
        var groupId = 123;
        var ancestorList = List.of(randomOrganisationGroupJoin(), randomOrganisationGroupJoin());

        Mockito.when(organisationDao.findAncestorsOfOrganisation(groupId)).thenReturn(ancestorList);

        var result = organisationTreeService.findAncestorsByGroupId(groupId);
        assertNotNull(result);
        assertEquals(2, result.size());

        assertOrganisation(ancestorList.getFirst(), result.getFirst());
        assertOrganisation(ancestorList.getLast(), result.getLast());

        Mockito.verify(organisationDao).findAncestorsOfOrganisation(groupId);
        Mockito.verify(validationService).validateAncestorList(ancestorList);
        verifyNoMoreInteractions();
    }

    @Test
    public void testFindAncestorsByGroupIdNoOrganisationInDb() {
        var groupId = 321;

        Mockito.when(organisationDao.findAncestorsOfOrganisation(groupId)).thenReturn(List.of());

        var expectedException = assertThrows(OrganisationNotFoundException.class, () -> organisationTreeService.findAncestorsByGroupId(groupId));
        assertNotNull(expectedException);
        assertEquals("Organisation with group id 321 not found.", expectedException.getMessage());

        Mockito.verify(organisationDao).findAncestorsOfOrganisation(groupId);
        verifyNoMoreInteractions();
    }

    @Test
    public void testFindAncestorsByGroupIdValidationFails() {
        var groupId = 234;
        var ancestorList = List.of(randomOrganisationGroupJoin(), randomOrganisationGroupJoin());

        Mockito.when(organisationDao.findAncestorsOfOrganisation(groupId)).thenReturn(ancestorList);
        Mockito.doThrow(OrganisationNotInTreeException.class).when(validationService).validateAncestorList(ancestorList);

        var expectedException = assertThrows(OrganisationNotInTreeException.class, () -> organisationTreeService.findAncestorsByGroupId(groupId));
        assertNotNull(expectedException);

        Mockito.verify(organisationDao).findAncestorsOfOrganisation(groupId);
        Mockito.verify(validationService).validateAncestorList(ancestorList);
        verifyNoMoreInteractions();
    }

    @Test
    public void testFindAncestorsByApiKey() {
        var apiKey = randomString();
        var ancestorList = List.of(randomOrganisationGroupJoin(), randomOrganisationGroupJoin());

        Mockito.when(organisationDao.findAncestorsOfOrganisationByHistoryApiKey(apiKey)).thenReturn(ancestorList);

        var result = organisationTreeService.findAncestorsByApiKey("history", apiKey);
        assertNotNull(result);
        assertEquals(2, result.size());

        assertOrganisation(ancestorList.getFirst(), result.getFirst());
        assertOrganisation(ancestorList.getLast(), result.getLast());

        Mockito.verify(organisationDao).findAncestorsOfOrganisationByHistoryApiKey(apiKey);
        Mockito.verify(validationService).validateAncestorList(ancestorList);
        verifyNoMoreInteractions();
    }

    @Test
    public void testFindAncestorsByApiKeyNoOrganisationInDb() {
        var apiKey = randomString();

        Mockito.when(organisationDao.findAncestorsOfOrganisationByHistoryApiKey(apiKey)).thenReturn(List.of());

        var expectedException = assertThrows(OrganisationNotFoundException.class, () -> organisationTreeService.findAncestorsByApiKey("history", apiKey));
        assertNotNull(expectedException);
        assertEquals("Organisation not found in database.", expectedException.getMessage());

        Mockito.verify(organisationDao).findAncestorsOfOrganisationByHistoryApiKey(apiKey);
        verifyNoMoreInteractions();
    }

    @Test
    public void testFindAncestorsByApiKeyUnknownApiKeyType() {
        var apiKey = randomString();

        var expectedException = assertThrows(InvalidDataException.class, () -> organisationTreeService.findAncestorsByApiKey("unknown-key-type", apiKey));
        assertNotNull(expectedException);
        assertEquals("ApiKey is not of valid type.", expectedException.getMessage());

        verifyNoMoreInteractions();
    }

    @Test
    public void testFindAncestorsByApiKeyValidationFails() {
        var apiKey = randomString();
        var ancestorList = List.of(randomOrganisationGroupJoin(), randomOrganisationGroupJoin());

        Mockito.when(organisationDao.findAncestorsOfOrganisationByHistoryApiKey(apiKey)).thenReturn(ancestorList);
        Mockito.doThrow(OrganisationNotInTreeException.class).when(validationService).validateAncestorList(ancestorList);

        var expectedException = assertThrows(OrganisationNotInTreeException.class, () -> organisationTreeService.findAncestorsByApiKey("history", apiKey));
        assertNotNull(expectedException);

        Mockito.verify(organisationDao).findAncestorsOfOrganisationByHistoryApiKey(apiKey);
        Mockito.verify(validationService).validateAncestorList(ancestorList);
        verifyNoMoreInteractions();
    }

    @Test
    public void testFindDescendantsByCode() {
        var code = randomString();
        var descendantList = List.of(randomOrganisationGroupJoin(), randomOrganisationGroupJoin());

        Mockito.when(organisationDao.findDescendantsOfOrganisation(code)).thenReturn(descendantList);

        var result = organisationTreeService.findDescendantsByCode(code);
        assertNotNull(result);
        assertEquals(2, result.size());

        assertOrganisation(descendantList.getFirst(), result.getFirst());
        assertOrganisation(descendantList.getLast(), result.getLast());

        Mockito.verify(validationService).validateOrganisationByCode(code);
        Mockito.verify(organisationDao).findDescendantsOfOrganisation(code);
        verifyNoMoreInteractions();
    }

    @Test
    public void testFindDescendantsByCodeNoOrganisationInDb() {
        var code = randomString();

        Mockito.when(organisationDao.findDescendantsOfOrganisation(code)).thenReturn(List.of());

        var expectedException = assertThrows(OrganisationNotFoundException.class, () -> organisationTreeService.findDescendantsByCode(code));
        assertNotNull(expectedException);
        assertEquals("Organisation with code %s not found.".formatted(code), expectedException.getMessage());

        Mockito.verify(validationService).validateOrganisationByCode(code);
        Mockito.verify(organisationDao).findDescendantsOfOrganisation(code);
        verifyNoMoreInteractions();
    }

    @Test
    public void testFindDescendantsByCodeValidationFails() {
        var code = randomString();

        Mockito.doThrow(OrganisationNotInTreeException.class).when(validationService).validateOrganisationByCode(code);

        var expectedException = assertThrows(OrganisationNotInTreeException.class, () -> organisationTreeService.findDescendantsByCode(code));
        assertNotNull(expectedException);

        Mockito.verify(validationService).validateOrganisationByCode(code);
        verifyNoMoreInteractions();
    }

    @Test
    public void testFindDescendantsByGroupId() {
        var groupId = 123;
        var descendantList = List.of(randomOrganisationGroupJoin(), randomOrganisationGroupJoin());

        Mockito.when(organisationDao.findDescendantsOfOrganisation(groupId)).thenReturn(descendantList);

        var result = organisationTreeService.findDescendantsByGroupId(groupId);
        assertNotNull(result);
        assertEquals(2, result.size());

        assertOrganisation(descendantList.getFirst(), result.getFirst());
        assertOrganisation(descendantList.getLast(), result.getLast());

        Mockito.verify(validationService).validateOrganisationByGroupId(groupId);
        Mockito.verify(organisationDao).findDescendantsOfOrganisation(groupId);
        verifyNoMoreInteractions();
    }

    @Test
    public void testFindDescendantsByGroupIdNoOrganisationInDb() {
        var groupId = 321;

        Mockito.when(organisationDao.findDescendantsOfOrganisation(groupId)).thenReturn(List.of());

        var expectedException = assertThrows(OrganisationNotFoundException.class, () -> organisationTreeService.findDescendantsByGroupId(groupId));
        assertNotNull(expectedException);
        assertEquals("Organisation with group id 321 not found.", expectedException.getMessage());

        Mockito.verify(validationService).validateOrganisationByGroupId(groupId);
        Mockito.verify(organisationDao).findDescendantsOfOrganisation(groupId);
        verifyNoMoreInteractions();
    }

    @Test
    public void testFindDescendantsByGroupIdValidationFails() {
        var groupId = 234;

        Mockito.doThrow(OrganisationNotInTreeException.class).when(validationService).validateOrganisationByGroupId(groupId);

        var expectedException = assertThrows(OrganisationNotInTreeException.class, () -> organisationTreeService.findDescendantsByGroupId(groupId));
        assertNotNull(expectedException);

        Mockito.verify(validationService).validateOrganisationByGroupId(groupId);
        verifyNoMoreInteractions();
    }

    @Test
    public void testFindDescendantsOfOrganisation() {
        var input = randomString();

        var org1 = randomOrganisationGroupJoin();
        var org2 = randomOrganisationGroupJoin();
        var nullOrg = new OrganisationGroupJoin(789L, 876L, null, randomString(), null, null, null, null, null, null, null, null);

        Mockito.when(organisationDao.findDescendantsOfOrganisation(input)).thenReturn(List.of(org1, org2, nullOrg));

        var result = organisationTreeService.findDescendantsOfOrganisation(input);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(x -> x.code() != null && x.code().equals(org1.organisationId())));
        assertTrue(result.stream().anyMatch(x -> x.code() != null && x.code().equals(org2.organisationId())));
        assertFalse(result.stream().anyMatch(x -> x.code() != null && x.code().equals(nullOrg.organisationId())));

        Mockito.verify(validationService).validateOrganisationByCode(input);
        Mockito.verify(organisationDao).findDescendantsOfOrganisation(input);
        verifyNoMoreInteractions();
    }

    @Test
    public void testFindDescendantsOfOrganisationCodesNoOrganisationInDb() {
        var input = randomString();

        Mockito.when(organisationDao.findDescendantsOfOrganisation(input)).thenReturn(List.of());

        var expectedException = assertThrows(OrganisationNotFoundException.class, () -> organisationTreeService.findDescendantsOfOrganisation(input));
        assertNotNull(expectedException);
        assertEquals("Organisation with code %s not found.".formatted(input), expectedException.getMessage());

        Mockito.verify(validationService).validateOrganisationByCode(input);
        Mockito.verify(organisationDao).findDescendantsOfOrganisation(input);
        verifyNoMoreInteractions();
    }

    @Test
    public void testFindDescendantsOfOrganisationValidationFails() {
        var input = randomString();

        Mockito.doThrow(OrganisationNotInTreeException.class).when(validationService).validateOrganisationByCode(input);

        var expectedException = assertThrows(OrganisationNotInTreeException.class, () -> organisationTreeService.findDescendantsOfOrganisation(input));
        assertNotNull(expectedException);

        Mockito.verify(validationService).validateOrganisationByCode(input);
        verifyNoMoreInteractions();
    }

    private void assertOrganisation(Optional<Organisation> expectedOrganisation, int groupId, Long parentId, Integer poolSize) {
        assertTrue(expectedOrganisation.isPresent());

        Organisation org = expectedOrganisation.get();
        assertEquals(groupId, org.getGroupId().longValue());
        assertEquals(parentId, org.getParentId());
        assertEquals(poolSize, org.getPoolSize());
    }

    private void assertOrganisation(OrganisationGroupJoin organisationGroupJoinExpected, OrganisationModel actual) {
        var expected = new OrganisationModel(organisationGroupJoinExpected.groupId(), organisationGroupJoinExpected.parentId(), organisationGroupJoinExpected.poolSize(), organisationGroupJoinExpected.groupName(), organisationGroupJoinExpected.organisationId(), organisationGroupJoinExpected.organisationName(), organisationGroupJoinExpected.smsSenderName(), organisationGroupJoinExpected.allowCustomUriWithoutDomain(), organisationGroupJoinExpected.smsCallbackUrl(), organisationGroupJoinExpected.historyApiKey(), organisationGroupJoinExpected.deviceWebhookEndpoint(), organisationGroupJoinExpected.deviceWebhookEndpointKey());
        assertEquals(expected, actual);
    }

    private Organisation createOrganisation(int groupId, Integer parentId, Integer poolSize) {
        var organisation = new Organisation();
        organisation.setGroupId((long) groupId);
        if(parentId != null) {
            organisation.setParentId(Long.valueOf(parentId));
        }
        if(poolSize != null) {
            organisation.setPoolSize(poolSize);
        }

        return organisation;
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }

    private OrganisationGroupJoin randomOrganisationGroupJoin() {
        return new OrganisationGroupJoin(123L, 321L, 234, randomString(), randomString(), randomString(), randomString(), false, randomString(), randomString(), randomString(), randomString());
    }
}

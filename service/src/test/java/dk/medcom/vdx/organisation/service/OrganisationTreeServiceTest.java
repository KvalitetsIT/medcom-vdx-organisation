package dk.medcom.vdx.organisation.service;

import dk.medcom.vdx.organisation.dao.OrganisationDao;
import dk.medcom.vdx.organisation.dao.entity.Organisation;
import dk.medcom.vdx.organisation.service.impl.OrganisationTreeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

public class OrganisationTreeServiceTest {
    private OrganisationTreeService organisationTreeService;
    private OrganisationDao organisationDao;

    @BeforeEach
    public void setup() {
        organisationDao = Mockito.mock(OrganisationDao.class);
        organisationTreeService = new OrganisationTreeServiceImpl(organisationDao);
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

    private void assertOrganisation(Optional<Organisation> expectedOrganisation, int groupId, Long parentId, Integer poolSize) {
        assertTrue(expectedOrganisation.isPresent());

        Organisation org = expectedOrganisation.get();
        assertEquals(groupId, org.getGroupId().longValue());
        assertEquals(parentId, org.getParentId());
        assertEquals(poolSize, org.getPoolSize());
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
}

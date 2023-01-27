package dk.medcom.vdx.organisation.service;

import dk.medcom.vdx.organisation.dao.OrganisationDao;
import dk.medcom.vdx.organisation.dao.entity.Organisation;
import dk.medcom.vdx.organisation.service.impl.OrganisationTreeServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;

public class OrganisationTreeServiceTest {
    private OrganisationTreeService organisationTreeService;
    private OrganisationDao organisationDao;

    @Before
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
        Mockito.when(organisationDao.findOrganisationByGroupId(child.getParentId())).thenReturn(childOne);

        var parent = createOrganisation(11, 10, 20);
        Mockito.when(organisationDao.findOrganisationByGroupId(childOne.getParentId())).thenReturn(parent);

        var superParent = createOrganisation(10, null, null);
        Mockito.when(organisationDao.findOrganisationByGroupId(parent.getParentId())).thenReturn(superParent);

        var result = organisationTreeService.findOrganisations(input).orElseThrow(RuntimeException::new);

        assertEquals(4, result.size());
        assertOrganisation(result.stream().filter(x -> x.getGroupId() == 13L).findFirst(), 13, 12L, null);
        assertOrganisation(result.stream().filter(x -> x.getGroupId() == 12L).findFirst(), 12, 11L, null);
        assertOrganisation(result.stream().filter(x -> x.getGroupId() == 11L).findFirst(), 11, 10L, 20);
        assertOrganisation(result.stream().filter(x -> x.getGroupId() == 10L).findFirst(), 10, null, null);
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

    @Test
    public void testOrganisationNotFound() {
        String input = "not_found";

        Mockito.when(organisationDao.findOrganisation("input")).thenReturn(null);

        var result = organisationTreeService.findOrganisations(input);
        assertNotNull(result);
        assertTrue(result.isEmpty());

        Mockito.verify(organisationDao, times(1)).findOrganisation(input);
    }
}

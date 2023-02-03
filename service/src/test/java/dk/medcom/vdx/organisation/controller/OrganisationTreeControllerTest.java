package dk.medcom.vdx.organisation.controller;

import dk.medcom.vdx.organisation.controller.exception.ResourceNotFoundException;
import dk.medcom.vdx.organisation.dao.entity.Organisation;
import dk.medcom.vdx.organisation.service.OrganisationTreeService;
import dk.medcom.vdx.organisation.service.impl.OrganisationTreeBuilderImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;

public class OrganisationTreeControllerTest {

    private OrganisationTreeController organisationTreeController;
    private OrganisationTreeService organisationTreeService;

    @Before
    public void setup() {
        organisationTreeService = Mockito.mock(OrganisationTreeService.class);
        organisationTreeController = new OrganisationTreeController(organisationTreeService, new OrganisationTreeBuilderImpl());
    }

    @Test
    public void testGetOrganisationTree() {
        var input = "child";

        var child = createOrganisation("child", 13, 12, null, "child_code");
        var childOne = createOrganisation("childOne", 12, 11, null, null);
        var childTwo = createOrganisation("childtwo", 14, 11, null, null);
        var parent = createOrganisation("parent", 11, 10, 20, "parent_code");
        var superParent = createOrganisation("superParent", 10, null, null, null);

        Mockito.when(organisationTreeService.findOrganisations(input)).thenReturn(Optional.of(Arrays.asList(child, childOne, parent, superParent, childTwo)));

        var resultEntity = organisationTreeController.servicesOrganisationtreeCodeGet(input);
        assertNotNull(resultEntity);

        var result = resultEntity.getBody();

        assertEquals(0, result.getPoolSize().intValue());
        assertEquals(superParent.getOrganisationName(), result.getName());
        assertEquals(superParent.getGroupId().toString(), result.getCode());
        assertEquals(1, result.getChildren().size());

        var treeChild = result.getChildren().get(0);
        assertEquals(parent.getOrganisationName(), treeChild.getName());
        assertEquals(parent.getOrganisationId(), treeChild.getCode());
        assertEquals(20, treeChild.getPoolSize().intValue());
        assertEquals(2, treeChild.getChildren().size());

        var childOneTree = treeChild.getChildren().get(0);
        assertEquals(childOne.getOrganisationName(), childOneTree.getName());
        assertEquals(childOne.getGroupId().toString(), childOneTree.getCode());
        assertEquals(0, childOneTree.getPoolSize().intValue());
        assertEquals(1, childOneTree.getChildren().size());

        var childTwoTree = treeChild.getChildren().get(1);
        assertEquals(childTwo.getOrganisationName(), childTwoTree.getName());
        assertEquals(childTwo.getGroupId().toString(), childTwoTree.getCode());
        assertEquals(0, childTwoTree.getPoolSize().intValue());
        assertEquals(0, childTwoTree.getChildren().size());

        treeChild = childOneTree.getChildren().get(0);
        assertEquals(child.getOrganisationName(), treeChild.getName());
        assertEquals(child.getOrganisationId(), treeChild.getCode());
        assertEquals(0, treeChild.getPoolSize().intValue());
        assertTrue(treeChild.getChildren().isEmpty());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testOrganisationNotFound() {
        var input = "not_found";
        Mockito.when(organisationTreeService.findOrganisations(input)).thenReturn(Optional.empty());
        organisationTreeController.servicesOrganisationtreeCodeGet(input);
    }

    private Organisation createOrganisation(String name, int groupId, Integer parentId, Integer poolSize, String organisationId) {
        var organisation = new Organisation();
        organisation.setGroupId((long) groupId);
        if(parentId != null) {
            organisation.setParentId(Long.valueOf(parentId));
        }
        if(poolSize != null) {
            organisation.setPoolSize(poolSize);
        }
        organisation.setOrganisationName(name);
        organisation.setOrganisationId(organisationId);

        return organisation;
    }
}

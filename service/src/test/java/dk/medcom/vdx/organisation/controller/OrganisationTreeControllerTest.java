package dk.medcom.vdx.organisation.controller;

import dk.medcom.vdx.organisation.controller.exception.BadRequestException;
import dk.medcom.vdx.organisation.controller.exception.ResourceNotFoundException;
import dk.medcom.vdx.organisation.controller.v1.OrganisationTreeController;
import dk.medcom.vdx.organisation.dao.entity.Organisation;
import dk.medcom.vdx.organisation.service.OrganisationTreeService;
import dk.medcom.vdx.organisation.service.impl.OrganisationTreeBuilderImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openapitools.model.OrganisationTreeForApiKey;
import org.springframework.http.HttpStatus;

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
    public void testOrganisationTreeChildrenGetByCode() {
        var child = createOrganisation("child", 13, 12, null, "child_code");
        var childOne = createOrganisation("childOne", 12, 11, null, null);
        var childTwo = createOrganisation("childtwo", 14, 11, null, null);
        var parent = createOrganisation("parent", 11, 10, 20, "parent_code");
        var superParent = createOrganisation("superParent", 10, null, null, "super_parent_code");

        var organisationList = Arrays.asList(superParent, parent, childOne, childTwo, child);

        Mockito.when(organisationTreeService.findChildrenByOrganisationCode("super_parent_code")).thenReturn(organisationList);

        var resultEntity = organisationTreeController.servicesV1OrganisationtreeChildrenGet("super_parent_code", null);
        assertNotNull(resultEntity);

        var result = resultEntity.getBody();
        assertNotNull(result);
        assertEquals(superParent.getGroupId(), result.getGroupId().longValue(), 0);
        assertEquals(superParent.getOrganisationId(), result.getCode());
        assertEquals(1, result.getChildren().size());

        var children = result.getChildren();
        assertEquals(parent.getGroupId(), children.getFirst().getGroupId().longValue(), 0);
        assertEquals(parent.getOrganisationId(), children.getFirst().getCode());
        assertEquals(2, children.getFirst().getChildren().size());

        children = children.getFirst().getChildren();
        assertEquals(childOne.getGroupId(), children.getFirst().getGroupId().longValue(), 0);
        assertEquals(childOne.getGroupId().toString(), children.getFirst().getCode());
        assertEquals(1, children.getFirst().getChildren().size());

        assertEquals(childTwo.getGroupId(), children.getLast().getGroupId().longValue(), 0);
        assertEquals(childTwo.getGroupId().toString(), children.getLast().getCode());
        assertEquals(0, children.getLast().getChildren().size());

        children = children.getFirst().getChildren();
        assertEquals(child.getGroupId(), children.getFirst().getGroupId().longValue(), 0);
        assertEquals(child.getOrganisationId(), children.getFirst().getCode());
        assertEquals(0, children.getFirst().getChildren().size());

        Mockito.verify(organisationTreeService).findChildrenByOrganisationCode("super_parent_code");
        Mockito.verifyNoMoreInteractions(organisationTreeService);
    }

    @Test
    public void testOrganisationTreeChildrenGetByGroupId() {
        var child = createOrganisation("child", 13, 12, null, "child_code");
        var childOne = createOrganisation("childOne", 12, 11, null, null);
        var childTwo = createOrganisation("childtwo", 14, 11, null, null);
        var parent = createOrganisation("parent", 11, 10, 20, "parent_code");
        var superParent = createOrganisation("superParent", 10, null, null, "super_parent_code");

        var organisationList = Arrays.asList(superParent, parent, childOne, childTwo, child);

        Mockito.when(organisationTreeService.findChildrenByGroupId(10)).thenReturn(organisationList);

        var resultEntity = organisationTreeController.servicesV1OrganisationtreeChildrenGet(null, 10);
        assertNotNull(resultEntity);

        var result = resultEntity.getBody();
        assertNotNull(result);
        assertEquals(superParent.getGroupId(), result.getGroupId().longValue(), 0);
        assertEquals(superParent.getOrganisationId(), result.getCode());
        assertEquals(1, result.getChildren().size());

        var children = result.getChildren();
        assertEquals(parent.getGroupId(), children.getFirst().getGroupId().longValue(), 0);
        assertEquals(parent.getOrganisationId(), children.getFirst().getCode());
        assertEquals(2, children.getFirst().getChildren().size());

        children = children.getFirst().getChildren();
        assertEquals(childOne.getGroupId(), children.getFirst().getGroupId().longValue(), 0);
        assertEquals(childOne.getGroupId().toString(), children.getFirst().getCode());
        assertEquals(1, children.getFirst().getChildren().size());

        assertEquals(childTwo.getGroupId(), children.getLast().getGroupId().longValue(), 0);
        assertEquals(childTwo.getGroupId().toString(), children.getLast().getCode());
        assertEquals(0, children.getLast().getChildren().size());

        children = children.getFirst().getChildren();
        assertEquals(child.getGroupId(), children.getFirst().getGroupId().longValue(), 0);
        assertEquals(child.getOrganisationId(), children.getFirst().getCode());
        assertEquals(0, children.getFirst().getChildren().size());

        Mockito.verify(organisationTreeService).findChildrenByGroupId(10);
        Mockito.verifyNoMoreInteractions(organisationTreeService);
    }

    @Test
    public void testOrganisationTreeChildrenGetNoCodeOrGroupId() {
        var exception = assertThrows(BadRequestException.class, () -> organisationTreeController.servicesV1OrganisationtreeChildrenGet(null, null));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    public void testOrganisationTreeChildrenGetBothCodeAndGroupId() {
        var exception = assertThrows(BadRequestException.class, () -> organisationTreeController.servicesV1OrganisationtreeChildrenGet("code", 1));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
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

        var treeChild = result.getChildren().getFirst();
        assertEquals(parent.getOrganisationName(), treeChild.getName());
        assertEquals(parent.getOrganisationId(), treeChild.getCode());
        assertEquals(20, treeChild.getPoolSize().intValue());
        assertEquals(2, treeChild.getChildren().size());

        var childOneTree = treeChild.getChildren().getFirst();
        assertEquals(childOne.getOrganisationName(), childOneTree.getName());
        assertEquals(childOne.getGroupId().toString(), childOneTree.getCode());
        assertEquals(0, childOneTree.getPoolSize().intValue());
        assertEquals(1, childOneTree.getChildren().size());

        var childTwoTree = treeChild.getChildren().get(1);
        assertEquals(childTwo.getOrganisationName(), childTwoTree.getName());
        assertEquals(childTwo.getGroupId().toString(), childTwoTree.getCode());
        assertEquals(0, childTwoTree.getPoolSize().intValue());
        assertEquals(0, childTwoTree.getChildren().size());

        treeChild = childOneTree.getChildren().getFirst();
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

    @Test(expected = ResourceNotFoundException.class)
    public void testOrganisationByApiKeyNotFound() {
        var input = "not_found";
        Mockito.when(organisationTreeService.findOrganisations("history", input)).thenReturn(Optional.empty());
        organisationTreeController.servicesOrganisationtreeCodeGet(input);
    }

    @Test
    public void testGetOrganisationTreeByApiKey() {
        var input = "child";

        var child = createOrganisation("child", 13, 12, null, "child_code");
        var childOne = createOrganisation("childOne", 12, 11, null, null);
        var childTwo = createOrganisation("childtwo", 14, 11, null, null);
        var parent = createOrganisation("parent", 11, 10, 20, "parent_code");
        var superParent = createOrganisation("superParent", 10, null, null, null);

        Mockito.when(organisationTreeService.findOrganisations("history", input)).thenReturn(Optional.of(Arrays.asList(child, childOne, parent, superParent, childTwo)));

        var resultEntity = organisationTreeController.servicesV1OrganisationTreeForApiKeyPost(new OrganisationTreeForApiKey(input, "history"));
        assertNotNull(resultEntity);

        var result = resultEntity.getBody();

        assertEquals(0, result.getPoolSize().intValue());
        assertEquals(superParent.getOrganisationName(), result.getName());
        assertEquals(superParent.getGroupId().toString(), result.getCode());
        assertEquals(1, result.getChildren().size());

        var treeChild = result.getChildren().getFirst();
        assertEquals(parent.getOrganisationName(), treeChild.getName());
        assertEquals(parent.getOrganisationId(), treeChild.getCode());
        assertEquals(20, treeChild.getPoolSize().intValue());
        assertEquals(2, treeChild.getChildren().size());

        var childOneTree = treeChild.getChildren().getFirst();
        assertEquals(childOne.getOrganisationName(), childOneTree.getName());
        assertEquals(childOne.getGroupId().toString(), childOneTree.getCode());
        assertEquals(0, childOneTree.getPoolSize().intValue());
        assertEquals(1, childOneTree.getChildren().size());

        var childTwoTree = treeChild.getChildren().get(1);
        assertEquals(childTwo.getOrganisationName(), childTwoTree.getName());
        assertEquals(childTwo.getGroupId().toString(), childTwoTree.getCode());
        assertEquals(0, childTwoTree.getPoolSize().intValue());
        assertEquals(0, childTwoTree.getChildren().size());

        treeChild = childOneTree.getChildren().getFirst();
        assertEquals(child.getOrganisationName(), treeChild.getName());
        assertEquals(child.getOrganisationId(), treeChild.getCode());
        assertEquals(0, treeChild.getPoolSize().intValue());
        assertTrue(treeChild.getChildren().isEmpty());
    }

    @Test
    public void testOrganisationTreeGetByCode() {
        var child = createOrganisation("child", 13, 12, null, "child_code");
        var childOne = createOrganisation("childOne", 12, 11, null, null);
        var parent = createOrganisation("parent", 11, 10, 20, "parent_code");
        var superParent = createOrganisation("superParent", 10, null, null, null);

        Mockito.when(organisationTreeService.findOrganisations("child_code")).thenReturn(Optional.of(Arrays.asList(child, childOne, parent, superParent)));

        var resultEntity = organisationTreeController.servicesOrganisationtreeGet("child_code", null);
        assertNotNull(resultEntity);

        var result = resultEntity.getBody();

        assertEquals(0, result.getPoolSize().intValue());
        assertEquals(superParent.getOrganisationName(), result.getName());
        assertEquals(superParent.getGroupId().toString(), result.getCode());
        assertEquals(1, result.getChildren().size());

        var treeChild = result.getChildren().getFirst();
        assertEquals(parent.getOrganisationName(), treeChild.getName());
        assertEquals(parent.getOrganisationId(), treeChild.getCode());
        assertEquals(20, treeChild.getPoolSize().intValue());
        assertEquals(1, treeChild.getChildren().size());

        var childOneTree = treeChild.getChildren().getFirst();
        assertEquals(childOne.getOrganisationName(), childOneTree.getName());
        assertEquals(childOne.getGroupId().toString(), childOneTree.getCode());
        assertEquals(0, childOneTree.getPoolSize().intValue());
        assertEquals(1, childOneTree.getChildren().size());

        treeChild = childOneTree.getChildren().getFirst();
        assertEquals(child.getOrganisationName(), treeChild.getName());
        assertEquals(child.getOrganisationId(), treeChild.getCode());
        assertEquals(0, treeChild.getPoolSize().intValue());
        assertTrue(treeChild.getChildren().isEmpty());

        Mockito.verify(organisationTreeService).findOrganisations("child_code");
        Mockito.verifyNoMoreInteractions(organisationTreeService);
    }

    @Test
    public void testOrganisationTreeGetNoCodeOrGroupId() {
        var exception = assertThrows(BadRequestException.class, () -> organisationTreeController.servicesOrganisationtreeGet(null, null));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    public void testOrganisationTreeGetBothCodeAndGroupId() {
        var exception = assertThrows(BadRequestException.class, () -> organisationTreeController.servicesOrganisationtreeGet("code", 1));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    public void testOrganisationTreeGetByGroupId() {
        var child = createOrganisation("child", 13, 12, null, "child_code");
        var childOne = createOrganisation("childOne", 12, 11, null, null);
        var parent = createOrganisation("parent", 11, 10, 20, "parent_code");
        var superParent = createOrganisation("superParent", 10, null, null, null);

        Mockito.when(organisationTreeService.getByGroupId(13)).thenReturn(Optional.of(Arrays.asList(child, childOne, parent, superParent)));

        var resultEntity = organisationTreeController.servicesOrganisationtreeGet(null, 13);
        assertNotNull(resultEntity);

        var result = resultEntity.getBody();

        assertEquals(0, result.getPoolSize().intValue());
        assertEquals(superParent.getOrganisationName(), result.getName());
        assertEquals(superParent.getGroupId().toString(), result.getCode());
        assertEquals(1, result.getChildren().size());

        var treeChild = result.getChildren().getFirst();
        assertEquals(parent.getOrganisationName(), treeChild.getName());
        assertEquals(parent.getOrganisationId(), treeChild.getCode());
        assertEquals(20, treeChild.getPoolSize().intValue());
        assertEquals(1, treeChild.getChildren().size());

        var childOneTree = treeChild.getChildren().getFirst();
        assertEquals(childOne.getOrganisationName(), childOneTree.getName());
        assertEquals(childOne.getGroupId().toString(), childOneTree.getCode());
        assertEquals(0, childOneTree.getPoolSize().intValue());
        assertEquals(1, childOneTree.getChildren().size());

        treeChild = childOneTree.getChildren().getFirst();
        assertEquals(child.getOrganisationName(), treeChild.getName());
        assertEquals(child.getOrganisationId(), treeChild.getCode());
        assertEquals(0, treeChild.getPoolSize().intValue());
        assertTrue(treeChild.getChildren().isEmpty());

        Mockito.verify(organisationTreeService).getByGroupId(13);
        Mockito.verifyNoMoreInteractions(organisationTreeService);
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

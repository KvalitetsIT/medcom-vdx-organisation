package dk.medcom.vdx.organisation.service;

import dk.medcom.vdx.organisation.dao.entity.Organisation;
import dk.medcom.vdx.organisation.service.impl.OrganisationTreeBuilderImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class OrganisationTreeBuilderTest {
    private OrganisationTreeBuilder organisationTreeBuilder;

    @BeforeEach
    public void setup() {
        organisationTreeBuilder = new OrganisationTreeBuilderImpl();
    }

    @Test
    public void testBuildTree() {
        var child = createOrganisation("child", 13, 12, null, "child_code");
        var childOne = createOrganisation("childOne", 12, 11, null, null);
        var childTwo = createOrganisation("childTwo", 14, 11, null, null);
        var parent = createOrganisation("parent", 11, 10, 20, "parent_code", "sms-sender", "callback", UUID.randomUUID().toString(), UUID.randomUUID().toString());
        var superParent = createOrganisation("superParent", 10, null, null, null);

        var result = organisationTreeBuilder.buildOrganisationTree(Arrays.asList(childOne, child, superParent, parent, childTwo));
        assertNotNull(result);

        assertEquals(0, result.getPoolSize().intValue());
        assertEquals(superParent.getOrganisationName(), result.getName());
        assertEquals(superParent.getGroupId().toString(), result.getCode());
        assertNull(result.getSmsCallbackUrl());
        assertNull(result.getSmsSenderName());
        assertNull(result.getDeviceWebhookEndpoint());
        assertNull(result.getDeviceWebhookEndpointKey());
        assertEquals(0, result.getPoolSize().intValue());
        assertEquals(1, result.getChildren().size());

        var treeChild = result.getChildren().getFirst();
        assertEquals(parent.getOrganisationName(), treeChild.getName());
        assertEquals(parent.getOrganisationId(), treeChild.getCode());
        assertEquals("sms-sender", treeChild.getSmsSenderName());
        assertEquals("callback", treeChild.getSmsCallbackUrl());
        assertEquals(parent.getDeviceWebhookEndpoint(), treeChild.getDeviceWebhookEndpoint());
        assertEquals(parent.getDeviceWebhookEndpointKey(), treeChild.getDeviceWebhookEndpointKey());
        assertEquals(20, treeChild.getPoolSize().intValue());
        assertEquals(2, treeChild.getChildren().size());

        var childOneTree = treeChild.getChildren().getFirst();
        assertEquals(childOne.getOrganisationName(), childOneTree.getName());
        assertEquals(childOne.getGroupId().toString(), childOneTree.getCode());
        assertNull(childOneTree.getSmsCallbackUrl());
        assertNull(childOneTree.getSmsSenderName());
        assertNull(childOneTree.getDeviceWebhookEndpoint());
        assertNull(childOneTree.getDeviceWebhookEndpointKey());
        assertEquals(0, childOneTree.getPoolSize().intValue());
        assertEquals(1, childOneTree.getChildren().size());

        var childTwoTree = treeChild.getChildren().get(1);
        assertEquals(childTwo.getOrganisationName(), childTwoTree.getName());
        assertEquals(childTwo.getGroupId().toString(), childTwoTree.getCode());
        assertNull(childTwoTree.getSmsCallbackUrl());
        assertNull(childTwoTree.getSmsSenderName());
        assertNull(childTwoTree.getDeviceWebhookEndpoint());
        assertNull(childTwoTree.getDeviceWebhookEndpointKey());
        assertEquals(0, childTwoTree.getPoolSize().intValue());
        assertTrue(childTwoTree.getChildren().isEmpty());

        treeChild = childOneTree.getChildren().getFirst();
        assertEquals(child.getOrganisationName(), treeChild.getName());
        assertEquals(child.getOrganisationId(), treeChild.getCode());
        assertNull(treeChild.getSmsCallbackUrl());
        assertNull(treeChild.getSmsSenderName());
        assertNull(treeChild.getDeviceWebhookEndpoint());
        assertNull(treeChild.getDeviceWebhookEndpointKey());
        assertEquals(0, treeChild.getPoolSize().intValue());
        assertTrue(treeChild.getChildren().isEmpty());
    }

    @Test
    public void testBuildEmptyTree() {
        var result = organisationTreeBuilder.buildOrganisationTree(Collections.emptyList());
        assertNull(result);
    }

    @Test
    public void testBuildNullTree() {
        var result = organisationTreeBuilder.buildOrganisationTree(null);
        assertNull(result);
    }

    private Organisation createOrganisation(String name, int groupId, Integer parentId, Integer poolSize, String organisationId) {
        return createOrganisation(name, groupId, parentId, poolSize, organisationId, null, null, null, null);
    }

    private Organisation createOrganisation(String name, int groupId, Integer parentId, Integer poolSize, String organisationId, String smsSenderName, String smsCallbackUrl, String deviceWebhookEndpoint, String deviceWebhookEndpointKey) {
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
        organisation.setSmsCallbackUrl(smsCallbackUrl);
        organisation.setSmsSenderName(smsSenderName);
        organisation.setDeviceWebhookEndpoint(deviceWebhookEndpoint);
        organisation.setDeviceWebhookEndpointKey(deviceWebhookEndpointKey);

        return organisation;
    }

}

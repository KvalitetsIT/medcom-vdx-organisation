package dk.medcom.vdx.organisation.integrationtest.v2;

import dk.medcom.vdx.organisation.integrationtest.AbstractIntegrationTest;
import dk.medcom.vdx.organisation.integrationtest.v2.helper.HeaderBuilder;
import org.junit.Before;
import org.junit.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.JSON;
import org.openapitools.client.api.OrganisationTreeV2Api;
import org.openapitools.client.model.OrganisationTreeForApiKey;
import org.openapitools.client.model.Organisationtree;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;

public class OrganisationTreeV2IT extends AbstractIntegrationTest {
    private final String childOrg = "child";

    private OrganisationTreeV2Api organisationTreeV2Api;
    private OrganisationTreeV2Api organisationTreeV2ApiNoHeader;
    private OrganisationTreeV2Api organisationTreeV2ApiInvalidJwt;
    private OrganisationTreeV2Api organisationTreeV2ApiNoRoleAtt;
    private OrganisationTreeV2Api organisationTreeV2ApiNotAdmin;

    @Before
    public void setup() {
        var apiClient = new ApiClient();
        apiClient.setBasePath(getApiBasePath());
        apiClient.addDefaultHeader("Authorization", "Bearer " + HeaderBuilder.getJwtAllRoleAtt(getKeycloakUrl()));
        organisationTreeV2Api = new OrganisationTreeV2Api(apiClient);

        var apiClientNoHeader = new ApiClient();
        apiClientNoHeader.setBasePath(getApiBasePath());
        organisationTreeV2ApiNoHeader = new OrganisationTreeV2Api(apiClientNoHeader);

        var apiClientInvalidJwt = new ApiClient();
        apiClientInvalidJwt.setBasePath(getApiBasePath());
        apiClientInvalidJwt.addDefaultHeader("Authorization", "Bearer " + HeaderBuilder.getInvalidJwt());
        organisationTreeV2ApiInvalidJwt = new OrganisationTreeV2Api(apiClientInvalidJwt);

        var apiClientNoRoleAtt = new ApiClient();
        apiClientNoRoleAtt.setBasePath(getApiBasePath());
        apiClientNoRoleAtt.addDefaultHeader("Authorization", "Bearer " + HeaderBuilder.getJwtNoRoleAtt(getKeycloakUrl()));
        organisationTreeV2ApiNoRoleAtt = new OrganisationTreeV2Api(apiClientNoRoleAtt);

        var apiClientNotAdmin = new ApiClient();
        apiClientNotAdmin.setBasePath(getApiBasePath());
        apiClientNotAdmin.addDefaultHeader("Authorization", "Bearer " + HeaderBuilder.getJwtNotAdmin(getKeycloakUrl()));
        organisationTreeV2ApiNotAdmin = new OrganisationTreeV2Api(apiClientNotAdmin);
    }

// ------ JWT errors -------

    @Test
    public void errorIfNoJwtToken_getOrganisationTreeSlash() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder(new URI(getApiBasePath() + "/services/v2/organisationtree/æ/åø")).
                GET().
                build();

        var client = HttpClient.newHttpClient();
        var responseString = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(401, responseString.statusCode());
    }

    @Test
    public void errorIfInvalidJwtToken_getOrganisationTreeSlash() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder(new URI(getApiBasePath() + "/services/v2/organisationtree/æ/åø")).
                header("Authorization", "Bearer " + HeaderBuilder.getInvalidJwt()).
                GET().
                build();

        var client = HttpClient.newHttpClient();
        var responseString = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(401, responseString.statusCode());
    }

    @Test
    public void errorIfNoRoleAttInToken_getOrganisationTreeSlash() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder(new URI(getApiBasePath() + "/services/v2/organisationtree/æ/åø")).
                header("Authorization", "Bearer " + HeaderBuilder.getJwtNoRoleAtt(getKeycloakUrl())).
                GET().
                build();

        var client = HttpClient.newHttpClient();
        var responseString = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(401, responseString.statusCode());
    }

    @Test
    public void errorIfNotAdmin_getOrganisationTreeSlash() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder(new URI(getApiBasePath() + "/services/v2/organisationtree/æ/åø")).
                header("Authorization", "Bearer " + HeaderBuilder.getJwtNotAdmin(getKeycloakUrl())).
                GET().
                build();

        var client = HttpClient.newHttpClient();
        var responseString = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(403, responseString.statusCode());
    }

    @Test
    public void errorIfNoJwtToken_servicesV2OrganisationTreeForApiKeyPost() {
        var expectedException = assertThrows(ApiException.class, () -> organisationTreeV2ApiNoHeader.servicesV2OrganisationTreeForApiKeyPost(historyApiKey()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfInvalidJwtToken_servicesV2OrganisationTreeForApiKeyPost() {
        var expectedException = assertThrows(ApiException.class, () -> organisationTreeV2ApiInvalidJwt.servicesV2OrganisationTreeForApiKeyPost(historyApiKey()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNoRoleAttInToken_servicesV2OrganisationTreeForApiKeyPost() {
        var expectedException = assertThrows(ApiException.class, () -> organisationTreeV2ApiNoRoleAtt.servicesV2OrganisationTreeForApiKeyPost(historyApiKey()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNotAdmin_servicesV2OrganisationTreeForApiKeyPost() {
        var expectedException = assertThrows(ApiException.class, () -> organisationTreeV2ApiNotAdmin.servicesV2OrganisationTreeForApiKeyPost(historyApiKey()));
        assertEquals(403, expectedException.getCode());
    }

    @Test
    public void errorIfNoJwtToken_servicesV2OrganisationtreeChildrenGet() {
        var expectedException = assertThrows(ApiException.class, () -> organisationTreeV2ApiNoHeader.servicesV2OrganisationtreeChildrenGet(childOrg, 13));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfInvalidJwtToken_servicesV2OrganisationtreeChildrenGet() {
        var expectedException = assertThrows(ApiException.class, () -> organisationTreeV2ApiInvalidJwt.servicesV2OrganisationtreeChildrenGet(childOrg, 13));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNoRoleAttInToken_servicesV2OrganisationtreeChildrenGet() {
        var expectedException = assertThrows(ApiException.class, () -> organisationTreeV2ApiNoRoleAtt.servicesV2OrganisationtreeChildrenGet(childOrg, 13));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNotAdmin_servicesV2OrganisationtreeChildrenGet() {
        var expectedException = assertThrows(ApiException.class, () -> organisationTreeV2ApiNotAdmin.servicesV2OrganisationtreeChildrenGet(childOrg, 13));
        assertEquals(403, expectedException.getCode());
    }

    @Test
    public void errorIfNoJwtToken_servicesV2OrganisationtreeCodeGet() {
        var expectedException = assertThrows(ApiException.class, () -> organisationTreeV2ApiNoHeader.servicesV2OrganisationtreeCodeGet(childOrg));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfInvalidJwtToken_servicesV2OrganisationtreeCodeGet() {
        var expectedException = assertThrows(ApiException.class, () -> organisationTreeV2ApiInvalidJwt.servicesV2OrganisationtreeCodeGet(childOrg));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNoRoleAttInToken_servicesV2OrganisationtreeCodeGet() {
        var expectedException = assertThrows(ApiException.class, () -> organisationTreeV2ApiNoRoleAtt.servicesV2OrganisationtreeCodeGet(childOrg));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNotAdmin_servicesV2OrganisationtreeCodeGet() {
        var expectedException = assertThrows(ApiException.class, () -> organisationTreeV2ApiNotAdmin.servicesV2OrganisationtreeCodeGet(childOrg));
        assertEquals(403, expectedException.getCode());
    }

    @Test
    public void errorIfNoJwtToken_servicesV2OrganisationtreeGet() {
        var expectedException = assertThrows(ApiException.class, () -> organisationTreeV2ApiNoHeader.servicesV2OrganisationtreeGet(childOrg, 13));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfInvalidJwtToken_servicesV2OrganisationtreeGet() {
        var expectedException = assertThrows(ApiException.class, () -> organisationTreeV2ApiInvalidJwt.servicesV2OrganisationtreeGet(childOrg, 13));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNoRoleAttInToken_servicesV2OrganisationtreeGet() {
        var expectedException = assertThrows(ApiException.class, () -> organisationTreeV2ApiNoRoleAtt.servicesV2OrganisationtreeGet(childOrg, 13));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNotAdmin_servicesV2OrganisationtreeGet() {
        var expectedException = assertThrows(ApiException.class, () -> organisationTreeV2ApiNotAdmin.servicesV2OrganisationtreeGet(childOrg, 13));
        assertEquals(403, expectedException.getCode());
    }

// ----------- No JWT errors -----------

    @Test
    public void testGetOrganisationTreeSlash() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder(new URI(getApiBasePath() + "/services/v2/organisationtree/æ/åø")).
                header("Authorization", "Bearer " + HeaderBuilder.getJwtAllRoleAtt(getKeycloakUrl())).
                GET().
                build();

        var client = HttpClient.newHttpClient();

        var responseString = client.send(request, HttpResponse.BodyHandlers.ofString());

        var response = JSON.getGson().fromJson(responseString.body(), Organisationtree.class);

        assertNotNull(response);
        assertEquals("æ/åø", response.getCode());
        assertEquals("This is with a slash", response.getName());
        assertNull(response.getSmsSenderName());
        assertTrue(response.getChildren().isEmpty());
        assertNull(response.getDeviceWebhookEndpoint());
        assertNull(response.getDeviceWebhookEndpointKey());
    }

    @Test
    public void testServicesV2OrganisationTreeForApiKeyPost() throws ApiException {
        var response = organisationTreeV2Api.servicesV2OrganisationTreeForApiKeyPost(historyApiKey());
        assertNotNull(response);

        var childOrganisation = response.getChildren().getFirst().getChildren().getFirst().getChildren().getFirst();
        assertEquals("child", childOrganisation.getCode());
        assertEquals("child org", childOrganisation.getName());
        assertNull(childOrganisation.getSmsSenderName());
        assertNull(childOrganisation.getSmsCallbackUrl());
        assertNull(childOrganisation.getDeviceWebhookEndpoint());
        assertNull(childOrganisation.getDeviceWebhookEndpointKey());

        var parentOrganisation = response.getChildren().getFirst();
        assertEquals("parent", parentOrganisation.getCode());
        assertEquals("parent org", parentOrganisation.getName());
        assertEquals("sms-sender", parentOrganisation.getSmsSenderName());
        assertEquals("callback", parentOrganisation.getSmsCallbackUrl());
        assertEquals("device-webhook-endpoint", parentOrganisation.getDeviceWebhookEndpoint());
        assertEquals("device-webhook-endpoint-key", parentOrganisation.getDeviceWebhookEndpointKey());
    }

    @Test
    public void testServicesV2OrganisationtreeChildrenGetByGroupId() throws ApiException {
        var response = organisationTreeV2Api.servicesV2OrganisationtreeChildrenGet(null, 10);
        assertNotNull(response);

        assertEquals(Optional.of(10).get(), response.getGroupId());
        assertEquals(1, response.getChildren().size());

        var children = response.getChildren();
        assertEquals(Optional.of(11).get(), children.getFirst().getGroupId());
        assertEquals(2, children.getFirst().getChildren().size());

        children = children.getFirst().getChildren();
        assertEquals(Optional.of(12).get(), children.getFirst().getGroupId());
        assertEquals(1, children.getFirst().getChildren().size());

        assertEquals(Optional.of(17).get(), children.get(1).getGroupId());
        assertEquals(0, children.get(1).getChildren().size());

        children = children.getFirst().getChildren();
        assertEquals(Optional.of(13).get(), children.getFirst().getGroupId());
        assertEquals(0, children.getFirst().getChildren().size());
    }

    @Test
    public void testServicesV2OrganisationtreeChildrenGetByGroupIdNotFound() {
        var exception = assertThrows(ApiException.class, () -> organisationTreeV2Api.servicesV2OrganisationtreeChildrenGet(null, 123));
        assertNotNull(exception);
        assertEquals(404, exception.getCode());
    }

    @Test
    public void testServicesV2OrganisationtreeChildrenGetByOrganisationCode() throws ApiException {
        var response = organisationTreeV2Api.servicesV2OrganisationtreeChildrenGet("parent", null);
        assertNotNull(response);

        assertEquals(Optional.of(11).get(), response.getGroupId());
        assertEquals(2, response.getChildren().size());

        var children = response.getChildren();
        assertEquals(Optional.of(12).get(), children.getFirst().getGroupId());
        assertEquals(1, children.getFirst().getChildren().size());

        assertEquals(Optional.of(17).get(), children.get(1).getGroupId());
        assertEquals(0, children.get(1).getChildren().size());

        children = children.getFirst().getChildren();
        assertEquals(Optional.of(13).get(), children.getFirst().getGroupId());
        assertEquals(0, children.getFirst().getChildren().size());
    }

    @Test
    public void testServicesV2OrganisationtreeChildrenGetByOrganisationCodeNotFound() {
        var exception = assertThrows(ApiException.class, () -> organisationTreeV2Api.servicesV2OrganisationtreeChildrenGet(UUID.randomUUID().toString(), null));
        assertNotNull(exception);
        assertEquals(404, exception.getCode());
    }

    @Test
    public void testServicesV2OrganisationtreeCodeGet() throws ApiException {
        var response = organisationTreeV2Api.servicesV2OrganisationtreeCodeGet("child");

        assertNotNull(response);
        assertEquals("super_parent", response.getName());
        assertEquals(0, response.getPoolSize().intValue());
        assertEquals("10", response.getCode());
        assertEquals(1, response.getChildren().size());
        assertNull(response.getSmsSenderName());
        assertNull(response.getSmsCallbackUrl());
        assertNull(response.getDeviceWebhookEndpoint());
        assertNull(response.getDeviceWebhookEndpointKey());

        var child = response.getChildren().getFirst();
        assertEquals("parent", child.getCode());
        assertEquals(20, child.getPoolSize().intValue());
        assertEquals("parent org", child.getName());
        assertEquals(1, child.getChildren().size());
        assertEquals("sms-sender", child.getSmsSenderName());
        assertEquals("callback", child.getSmsCallbackUrl());
        assertEquals("device-webhook-endpoint", child.getDeviceWebhookEndpoint());
        assertEquals("device-webhook-endpoint-key", child.getDeviceWebhookEndpointKey());

        child = child.getChildren().getFirst();
        assertEquals("child_one", child.getName());
        assertEquals(0, child.getPoolSize().intValue());
        assertEquals("12", child.getCode());
        assertEquals(1, child.getChildren().size());
        assertNull(child.getSmsSenderName());
        assertNull(child.getSmsCallbackUrl());
        assertNull(child.getDeviceWebhookEndpoint());
        assertNull(child.getDeviceWebhookEndpointKey());

        child = child.getChildren().getFirst();
        assertEquals("child", child.getCode());
        assertEquals(0, child.getPoolSize().intValue());
        assertEquals("child org", child.getName());
        assertEquals(0, child.getChildren().size());
        assertNull(child.getSmsSenderName());
        assertNull(child.getSmsCallbackUrl());
        assertNull(child.getDeviceWebhookEndpoint());
        assertNull(child.getDeviceWebhookEndpointKey());
    }

    @Test
    public void testServicesV2OrganisationtreeCodeGet_with_group_only() throws ApiException {
        var response = organisationTreeV2Api.servicesV2OrganisationtreeCodeGet("medcom_test_2");

        assertNotNull(response);
        assertEquals("VDX", response.getName());
        assertEquals(4, response.getPoolSize().intValue());
        assertEquals("vdx", response.getCode());
        assertEquals(1, response.getChildren().size());

        var child = response.getChildren().getFirst();
        assertEquals("21", child.getCode());
        assertEquals(0, child.getPoolSize().intValue());
        assertEquals("Offentlige organisationer", child.getName());
        assertEquals(1, child.getChildren().size());

        child = child.getChildren().getFirst();
        assertEquals("medcom1", child.getCode());
        assertEquals(4, child.getPoolSize().intValue());
        assertEquals("Medcom", child.getName());
        assertEquals(1, child.getChildren().size());

        child = child.getChildren().getFirst();
        assertEquals("Test Gruppe 1", child.getName());
        assertEquals(0, child.getPoolSize().intValue());
        assertEquals("42", child.getCode());
        assertEquals(1, child.getChildren().size());

        child = child.getChildren().getFirst();
        assertEquals("medcom_test_2", child.getCode());
        assertEquals(0, child.getPoolSize().intValue());
        assertEquals("MedCom Test 2", child.getName());
        assertEquals(0, child.getChildren().size());
    }

    @Test
    public void testServicesV2OrganisationtreeGetByGroupId() throws ApiException {
        var response = organisationTreeV2Api.servicesV2OrganisationtreeGet(null, 13);
        assertNotNull(response);

        var childOrganisation = response.getChildren().getFirst().getChildren().getFirst().getChildren().getFirst();
        assertEquals("child", childOrganisation.getCode());
        assertEquals("child org", childOrganisation.getName());
        assertNull(childOrganisation.getSmsSenderName());
        assertNull(childOrganisation.getSmsCallbackUrl());
        assertNull(childOrganisation.getDeviceWebhookEndpoint());
        assertNull(childOrganisation.getDeviceWebhookEndpointKey());

        var parentOrganisation = response.getChildren().getFirst();
        assertEquals("parent", parentOrganisation.getCode());
        assertEquals("parent org", parentOrganisation.getName());
        assertEquals("sms-sender", parentOrganisation.getSmsSenderName());
        assertEquals("callback", parentOrganisation.getSmsCallbackUrl());
        assertEquals("device-webhook-endpoint", parentOrganisation.getDeviceWebhookEndpoint());
        assertEquals("device-webhook-endpoint-key", parentOrganisation.getDeviceWebhookEndpointKey());
    }

    @Test
    public void testServicesV2OrganisationtreeGetByGroupIdNotFound() {
        var exception = assertThrows(ApiException.class, () -> organisationTreeV2Api.servicesV2OrganisationtreeGet(null, 123));
        assertNotNull(exception);
        assertEquals(404, exception.getCode());
    }

    @Test
    public void testServicesV2OrganisationtreeGetWithSlashQueryParameter() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder(new URI(getApiBasePath() + "/services/v2/organisationtree?organisationCode=æ/åø")).
                header("Authorization", "Bearer " + HeaderBuilder.getJwtAllRoleAtt(getKeycloakUrl())).
                GET().
                build();

        var client = HttpClient.newHttpClient();

        var responseString = client.send(request, HttpResponse.BodyHandlers.ofString());

        var response = JSON.getGson().fromJson(responseString.body(), Organisationtree.class);

        assertNotNull(response);
        assertEquals("æ/åø", response.getCode());
        assertEquals("This is with a slash", response.getName());
        assertNull(response.getSmsSenderName());
        assertTrue(response.getChildren().isEmpty());
        assertNull(response.getDeviceWebhookEndpoint());
        assertNull(response.getDeviceWebhookEndpointKey());
    }

    private OrganisationTreeForApiKey historyApiKey() {
        return new OrganisationTreeForApiKey()
                .apiKey("8adeac18-f061-4992-818b-8d4461ccfaa7")
                .apiKeyType("history");
    }
}

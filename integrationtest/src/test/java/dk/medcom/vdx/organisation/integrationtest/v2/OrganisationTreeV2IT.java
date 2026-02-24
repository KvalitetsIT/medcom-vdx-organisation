package dk.medcom.vdx.organisation.integrationtest.v2;

import dk.medcom.vdx.organisation.integrationtest.AbstractIntegrationTest;
import dk.medcom.vdx.organisation.integrationtest.v2.helper.HeaderBuilder;
import org.junit.jupiter.api.Test;
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

import static org.junit.jupiter.api.Assertions.*;

class OrganisationTreeV2IT extends AbstractIntegrationTest {
    private final String childOrg = "child";

    private final OrganisationTreeV2Api organisationTreeV2Api;
    private final OrganisationTreeV2Api organisationTreeV2ApiNoHeader;
    private final OrganisationTreeV2Api organisationTreeV2ApiNoRoleAtt;
    private final OrganisationTreeV2Api organisationTreeV2ApiNotAdmin;
    private final OrganisationTreeV2Api organisationTreeV2ApiExpiredJwt;
    private final OrganisationTreeV2Api organisationTreeV2ApiInvalidIssuerJwt;
    private final OrganisationTreeV2Api organisationTreeV2ApiTamperedJwt;
    private final OrganisationTreeV2Api organisationTreeV2ApiMissingSignatureJwt;
    private final OrganisationTreeV2Api organisationTreeV2ApiDifferentSignedJwt;

    OrganisationTreeV2IT() {
        var keycloakUrl = getKeycloakUrl();

        organisationTreeV2Api = createClient(HeaderBuilder.getJwtAllRoleAtt(keycloakUrl));
        organisationTreeV2ApiNoHeader = createClient(null);
        organisationTreeV2ApiNoRoleAtt = createClient(HeaderBuilder.getJwtNoRoleAtt(keycloakUrl));
        organisationTreeV2ApiNotAdmin = createClient(HeaderBuilder.getJwtNotAdmin(keycloakUrl));
        organisationTreeV2ApiExpiredJwt = createClient(HeaderBuilder.getExpiredJwt(keycloakUrl));
        organisationTreeV2ApiInvalidIssuerJwt = createClient(HeaderBuilder.getInvalidIssuerJwt());
        organisationTreeV2ApiTamperedJwt = createClient(HeaderBuilder.getTamperedJwt(keycloakUrl));
        organisationTreeV2ApiMissingSignatureJwt = createClient(HeaderBuilder.getMissingSignatureJwt(keycloakUrl));
        organisationTreeV2ApiDifferentSignedJwt = createClient(HeaderBuilder.getDifferentSignedJwt(keycloakUrl));
    }

    private OrganisationTreeV2Api createClient(String token) {
        var apiClient = new ApiClient();
        apiClient.setBasePath(getApiBasePath());
        if (token != null) {
            apiClient.addDefaultHeader("Authorization", "Bearer " + token);
        }
        return new OrganisationTreeV2Api(apiClient);
    }

// ------ JWT errors -------

    @Test
    void errorIfNoJwtToken_getOrganisationTreeSlash() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder(new URI(getApiBasePath() + "/services/v2/organisationtree/æ/åø")).
                GET().
                build();

        var client = HttpClient.newHttpClient();
        var responseString = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(401, responseString.statusCode());
    }

    @Test
    void errorIfNoRoleAttInToken_getOrganisationTreeSlash() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder(new URI(getApiBasePath() + "/services/v2/organisationtree/æ/åø")).
                header("Authorization", "Bearer " + HeaderBuilder.getJwtNoRoleAtt(getKeycloakUrl())).
                GET().
                build();

        var client = HttpClient.newHttpClient();
        var responseString = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(401, responseString.statusCode());
    }

    @Test
    void errorIfNotAdmin_getOrganisationTreeSlash() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder(new URI(getApiBasePath() + "/services/v2/organisationtree/æ/åø")).
                header("Authorization", "Bearer " + HeaderBuilder.getJwtNotAdmin(getKeycloakUrl())).
                GET().
                build();

        var client = HttpClient.newHttpClient();
        var responseString = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(403, responseString.statusCode());
    }

    @Test
    void errorIfExpiredJwtToken_getOrganisationTreeSlash() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder(new URI(getApiBasePath() + "/services/v2/organisationtree/æ/åø")).
                header("Authorization", "Bearer " + HeaderBuilder.getExpiredJwt(getKeycloakUrl())).
                GET().
                build();

        var client = HttpClient.newHttpClient();
        var responseString = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(401, responseString.statusCode());
    }

    @Test
    void errorIfInvalidIssuerJwtToken_getOrganisationTreeSlash() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder(new URI(getApiBasePath() + "/services/v2/organisationtree/æ/åø")).
                header("Authorization", "Bearer " + HeaderBuilder.getInvalidIssuerJwt()).
                GET().
                build();

        var client = HttpClient.newHttpClient();
        var responseString = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(401, responseString.statusCode());
    }

    @Test
    void errorIfTamperedJwtToken_getOrganisationTreeSlash() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder(new URI(getApiBasePath() + "/services/v2/organisationtree/æ/åø")).
                header("Authorization", "Bearer " + HeaderBuilder.getTamperedJwt(getKeycloakUrl())).
                GET().
                build();

        var client = HttpClient.newHttpClient();
        var responseString = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(401, responseString.statusCode());
    }

    @Test
    void errorIfMissingSignatureJwtToken_getOrganisationTreeSlash() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder(new URI(getApiBasePath() + "/services/v2/organisationtree/æ/åø")).
                header("Authorization", "Bearer " + HeaderBuilder.getMissingSignatureJwt(getKeycloakUrl())).
                GET().
                build();

        var client = HttpClient.newHttpClient();
        var responseString = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(401, responseString.statusCode());
    }

    @Test
    void errorIfDifferentSignedJwtToken_getOrganisationTreeSlash() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder(new URI(getApiBasePath() + "/services/v2/organisationtree/æ/åø")).
                header("Authorization", "Bearer " + HeaderBuilder.getDifferentSignedJwt(getKeycloakUrl())).
                GET().
                build();

        var client = HttpClient.newHttpClient();
        var responseString = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(401, responseString.statusCode());
    }

    @Test
    void errorIfNoJwtToken_servicesV2OrganisationTreeForApiKeyPost() {
        assertThrowsWithStatus(401, () -> organisationTreeV2ApiNoHeader.servicesV2OrganisationTreeForApiKeyPost(historyApiKey()));
    }

    @Test
    void errorIfNoRoleAttInToken_servicesV2OrganisationTreeForApiKeyPost() {
        assertThrowsWithStatus(401, () -> organisationTreeV2ApiNoRoleAtt.servicesV2OrganisationTreeForApiKeyPost(historyApiKey()));
    }

    @Test
    void errorIfNotAdmin_servicesV2OrganisationTreeForApiKeyPost() {
        assertThrowsWithStatus(403, () -> organisationTreeV2ApiNotAdmin.servicesV2OrganisationTreeForApiKeyPost(historyApiKey()));
    }

    @Test
    void errorIfExpiredJwtToken_servicesV2OrganisationTreeForApiKeyPost() {
        assertThrowsWithStatus(401, () -> organisationTreeV2ApiExpiredJwt.servicesV2OrganisationTreeForApiKeyPost(historyApiKey()));
    }

    @Test
    void errorIfInvalidIssuerJwtToken_servicesV2OrganisationTreeForApiKeyPost() {
        assertThrowsWithStatus(401, () -> organisationTreeV2ApiInvalidIssuerJwt.servicesV2OrganisationTreeForApiKeyPost(historyApiKey()));
    }

    @Test
    void errorIfTamperedJwtToken_servicesV2OrganisationTreeForApiKeyPost() {
        assertThrowsWithStatus(401, () -> organisationTreeV2ApiTamperedJwt.servicesV2OrganisationTreeForApiKeyPost(historyApiKey()));
    }

    @Test
    void errorIfMissingSignatureJwtToken_servicesV2OrganisationTreeForApiKeyPost() {
        assertThrowsWithStatus(401, () -> organisationTreeV2ApiMissingSignatureJwt.servicesV2OrganisationTreeForApiKeyPost(historyApiKey()));
    }

    @Test
    void errorIfDifferentSignedJwtToken_servicesV2OrganisationTreeForApiKeyPost() {
        assertThrowsWithStatus(401, () -> organisationTreeV2ApiDifferentSignedJwt.servicesV2OrganisationTreeForApiKeyPost(historyApiKey()));
    }

    @Test
    void errorIfNoJwtToken_servicesV2OrganisationtreeChildrenGet() {
        assertThrowsWithStatus(401, () -> organisationTreeV2ApiNoHeader.servicesV2OrganisationtreeChildrenGet(childOrg, 13));
    }

    @Test
    void errorIfNoRoleAttInToken_servicesV2OrganisationtreeChildrenGet() {
        assertThrowsWithStatus(401, () -> organisationTreeV2ApiNoRoleAtt.servicesV2OrganisationtreeChildrenGet(childOrg, 13));
    }

    @Test
    void errorIfNotAdmin_servicesV2OrganisationtreeChildrenGet() {
        assertThrowsWithStatus(403, () -> organisationTreeV2ApiNotAdmin.servicesV2OrganisationtreeChildrenGet(childOrg, 13));
    }

    @Test
    void errorIfExpiredJwtToken_servicesV2OrganisationtreeChildrenGet() {
        assertThrowsWithStatus(401, () -> organisationTreeV2ApiExpiredJwt.servicesV2OrganisationtreeChildrenGet(childOrg, 13));
    }

    @Test
    void errorIfInvalidIssuerJwtToken_servicesV2OrganisationtreeChildrenGet() {
        assertThrowsWithStatus(401, () -> organisationTreeV2ApiInvalidIssuerJwt.servicesV2OrganisationtreeChildrenGet(childOrg, 13));
    }

    @Test
    void errorIfTamperedJwtToken_servicesV2OrganisationtreeChildrenGet() {
        assertThrowsWithStatus(401, () -> organisationTreeV2ApiTamperedJwt.servicesV2OrganisationtreeChildrenGet(childOrg, 13));
    }

    @Test
    void errorIfMissingSignatureJwtToken_servicesV2OrganisationtreeChildrenGet() {
        assertThrowsWithStatus(401, () -> organisationTreeV2ApiMissingSignatureJwt.servicesV2OrganisationtreeChildrenGet(childOrg, 13));
    }

    @Test
    void errorIfDifferentSignedJwtToken_servicesV2OrganisationtreeChildrenGet() {
        assertThrowsWithStatus(401, () -> organisationTreeV2ApiDifferentSignedJwt.servicesV2OrganisationtreeChildrenGet(childOrg, 13));
    }

    @Test
    void errorIfNoJwtToken_servicesV2OrganisationtreeCodeGet() {
        assertThrowsWithStatus(401, () -> organisationTreeV2ApiNoHeader.servicesV2OrganisationtreeCodeGet(childOrg));
    }

    @Test
    void errorIfNoRoleAttInToken_servicesV2OrganisationtreeCodeGet() {
        assertThrowsWithStatus(401, () -> organisationTreeV2ApiNoRoleAtt.servicesV2OrganisationtreeCodeGet(childOrg));
    }

    @Test
    void errorIfNotAdmin_servicesV2OrganisationtreeCodeGet() {
        assertThrowsWithStatus(403, () -> organisationTreeV2ApiNotAdmin.servicesV2OrganisationtreeCodeGet(childOrg));
    }

    @Test
    void errorIfExpiredJwtToken_servicesV2OrganisationtreeCodeGet() {
        assertThrowsWithStatus(401, () -> organisationTreeV2ApiExpiredJwt.servicesV2OrganisationtreeCodeGet(childOrg));
    }

    @Test
    void errorIfInvalidIssuerJwtToken_servicesV2OrganisationtreeCodeGet() {
        assertThrowsWithStatus(401, () -> organisationTreeV2ApiInvalidIssuerJwt.servicesV2OrganisationtreeCodeGet(childOrg));
    }

    @Test
    void errorIfTamperedJwtToken_servicesV2OrganisationtreeCodeGet() {
        assertThrowsWithStatus(401, () -> organisationTreeV2ApiTamperedJwt.servicesV2OrganisationtreeCodeGet(childOrg));
    }

    @Test
    void errorIfMissingSignatureJwtToken_servicesV2OrganisationtreeCodeGet() {
        assertThrowsWithStatus(401, () -> organisationTreeV2ApiMissingSignatureJwt.servicesV2OrganisationtreeCodeGet(childOrg));
    }

    @Test
    void errorIfDifferentSignedJwtToken_servicesV2OrganisationtreeCodeGet() {
        assertThrowsWithStatus(401, () -> organisationTreeV2ApiDifferentSignedJwt.servicesV2OrganisationtreeCodeGet(childOrg));
    }

    @Test
    void errorIfNoJwtToken_servicesV2OrganisationtreeGet() {
        assertThrowsWithStatus(401, () -> organisationTreeV2ApiNoHeader.servicesV2OrganisationtreeGet(childOrg, 13));
    }

    @Test
    void errorIfNoRoleAttInToken_servicesV2OrganisationtreeGet() {
        assertThrowsWithStatus(401, () -> organisationTreeV2ApiNoRoleAtt.servicesV2OrganisationtreeGet(childOrg, 13));
    }

    @Test
    void errorIfNotAdmin_servicesV2OrganisationtreeGet() {
        assertThrowsWithStatus(403, () -> organisationTreeV2ApiNotAdmin.servicesV2OrganisationtreeGet(childOrg, 13));
    }

    @Test
    void errorIfExpiredJwtToken_servicesV2OrganisationtreeGet() {
        assertThrowsWithStatus(401, () -> organisationTreeV2ApiExpiredJwt.servicesV2OrganisationtreeGet(childOrg, 13));
    }

    @Test
    void errorIfInvalidIssuerJwtToken_servicesV2OrganisationtreeGet() {
        assertThrowsWithStatus(401, () -> organisationTreeV2ApiInvalidIssuerJwt.servicesV2OrganisationtreeGet(childOrg, 13));
    }

    @Test
    void errorIfTamperedJwtToken_servicesV2OrganisationtreeGet() {
        assertThrowsWithStatus(401, () -> organisationTreeV2ApiTamperedJwt.servicesV2OrganisationtreeGet(childOrg, 13));
    }

    @Test
    void errorIfMissingSignatureJwtToken_servicesV2OrganisationtreeGet() {
        assertThrowsWithStatus(401, () -> organisationTreeV2ApiMissingSignatureJwt.servicesV2OrganisationtreeGet(childOrg, 13));
    }

    @Test
    void errorIfDifferentSignedJwtToken_servicesV2OrganisationtreeGet() {
        assertThrowsWithStatus(401, () -> organisationTreeV2ApiDifferentSignedJwt.servicesV2OrganisationtreeGet(childOrg, 13));
    }

// ----------- No JWT errors -----------

    @Test
    void testGetOrganisationTreeSlash() throws URISyntaxException, IOException, InterruptedException {
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
    void testServicesV2OrganisationTreeForApiKeyPost() throws ApiException {
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
    void testServicesV2OrganisationtreeChildrenGetByGroupId() throws ApiException {
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
    void testServicesV2OrganisationtreeChildrenGetByGroupIdNotFound() {
        var exception = assertThrows(ApiException.class, () -> organisationTreeV2Api.servicesV2OrganisationtreeChildrenGet(null, 123));
        assertNotNull(exception);
        assertEquals(404, exception.getCode());
    }

    @Test
    void testServicesV2OrganisationtreeChildrenGetByOrganisationCode() throws ApiException {
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
    void testServicesV2OrganisationtreeChildrenGetByOrganisationCodeNotFound() {
        var exception = assertThrows(ApiException.class, () -> organisationTreeV2Api.servicesV2OrganisationtreeChildrenGet(UUID.randomUUID().toString(), null));
        assertNotNull(exception);
        assertEquals(404, exception.getCode());
    }

    @Test
    void testServicesV2OrganisationtreeCodeGet() throws ApiException {
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
    void testServicesV2OrganisationtreeCodeGet_with_group_only() throws ApiException {
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
    void testServicesV2OrganisationtreeGetByGroupId() throws ApiException {
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
    void testServicesV2OrganisationtreeGetByGroupIdNotFound() {
        var exception = assertThrows(ApiException.class, () -> organisationTreeV2Api.servicesV2OrganisationtreeGet(null, 123));
        assertNotNull(exception);
        assertEquals(404, exception.getCode());
    }

    @Test
    void testServicesV2OrganisationtreeGetWithSlashQueryParameter() throws URISyntaxException, IOException, InterruptedException {
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

    // --------- CORS ----------

    @Test
    void testCorsAllowedServicesV2OrganisationTreeForApiKeyPost() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/services/v2/organisation-tree-for-api-key", getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://allowed:4100")
                .header("Access-Control-Request-Method", "POST")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        var headers = response.headers().map();
        assertTrue(headers.get("Access-Control-Allow-Methods").contains("POST"));
        assertTrue(headers.get("Access-Control-Allow-Origin").contains("http://allowed:4100"));
        assertEquals(200, response.statusCode());
    }

    @Test
    void testCorsDeniedServicesV2OrganisationTreeForApiKeyPost() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/services/v2/organisation-tree-for-api-key", getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://denied:4200")
                .header("Access-Control-Request-Method", "POST")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(403, response.statusCode());
    }

    @Test
    void testCorsAllowedServicesV2OrganisationtreeGet() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/services/v2/organisationtree", getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://allowed:4100")
                .header("Access-Control-Request-Method", "GET")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        var headers = response.headers().map();
        assertTrue(headers.get("Access-Control-Allow-Methods").contains("GET"));
        assertTrue(headers.get("Access-Control-Allow-Origin").contains("http://allowed:4100"));
        assertEquals(200, response.statusCode());
    }

    @Test
    void testCorsDeniedServicesV2OrganisationtreeGet() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/services/v2/organisationtree", getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://denied:4200")
                .header("Access-Control-Request-Method", "GET")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(403, response.statusCode());
    }

    @Test
    void testCorsAllowedServicesV2OrganisationtreeChildrenGet() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/services/v2/organisationtree-children", getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://allowed:4100")
                .header("Access-Control-Request-Method", "GET")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        var headers = response.headers().map();
        assertTrue(headers.get("Access-Control-Allow-Methods").contains("GET"));
        assertTrue(headers.get("Access-Control-Allow-Origin").contains("http://allowed:4100"));
        assertEquals(200, response.statusCode());
    }

    @Test
    void testCorsDeniedServicesV2OrganisationtreeChildrenGet() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/services/v2/organisationtree-children", getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://denied:4200")
                .header("Access-Control-Request-Method", "GET")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(403, response.statusCode());
    }

    @Test
    void testCorsAllowedServicesV2OrganisationtreeCodeGet() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/services/v2/organisationtree/some-org", getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://allowed:4100")
                .header("Access-Control-Request-Method", "GET")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        var headers = response.headers().map();
        assertTrue(headers.get("Access-Control-Allow-Methods").contains("GET"));
        assertTrue(headers.get("Access-Control-Allow-Origin").contains("http://allowed:4100"));
        assertEquals(200, response.statusCode());
    }

    @Test
    void testCorsDeniedServicesV2OrganisationtreeCodeGet() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/services/v2/organisationtree/some-org", getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://denied:4200")
                .header("Access-Control-Request-Method", "GET")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(403, response.statusCode());
    }

    private OrganisationTreeForApiKey historyApiKey() {
        return new OrganisationTreeForApiKey()
                .apiKey("8adeac18-f061-4992-818b-8d4461ccfaa7")
                .apiKeyType("history");
    }
}

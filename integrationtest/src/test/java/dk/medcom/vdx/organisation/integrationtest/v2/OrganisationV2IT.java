package dk.medcom.vdx.organisation.integrationtest.v2;

import dk.medcom.vdx.organisation.integrationtest.AbstractIntegrationTest;
import dk.medcom.vdx.organisation.integrationtest.v2.helper.HeaderBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.JSON;
import org.openapitools.client.api.OrganisationV2Api;
import org.openapitools.client.model.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrganisationV2IT extends AbstractIntegrationTest {
    private final String testOrg = "test-org";
    private final String company1 = "company 1";
    
    private OrganisationV2Api organisationV2Api;
    private OrganisationV2Api organisationV2ApiNoHeader;
    private OrganisationV2Api organisationV2ApiInvalidJwt;
    private OrganisationV2Api organisationV2ApiNoRoleAtt;
    private OrganisationV2Api organisationV2ApiNotAdmin;
    
    @BeforeEach
    void setup() {
        var apiClient = new ApiClient();
        apiClient.setBasePath(getApiBasePath());
        apiClient.addDefaultHeader("Authorization", "Bearer " + HeaderBuilder.getJwtAllRoleAtt(getKeycloakUrl()));
        organisationV2Api = new OrganisationV2Api(apiClient);
        
        var apiClientNoHeader = new ApiClient();
        apiClientNoHeader.setBasePath(getApiBasePath());
        organisationV2ApiNoHeader = new OrganisationV2Api(apiClientNoHeader);

        var apiClientInvalidJwt = new ApiClient();
        apiClientInvalidJwt.setBasePath(getApiBasePath());
        apiClientInvalidJwt.addDefaultHeader("Authorization", "Bearer " + HeaderBuilder.getInvalidJwt());
        organisationV2ApiInvalidJwt = new OrganisationV2Api(apiClientInvalidJwt);

        var apiClientNoRoleAtt = new ApiClient();
        apiClientNoRoleAtt.setBasePath(getApiBasePath());
        apiClientNoRoleAtt.addDefaultHeader("Authorization", "Bearer " + HeaderBuilder.getJwtNoRoleAtt(getKeycloakUrl()));
        organisationV2ApiNoRoleAtt = new OrganisationV2Api(apiClientNoRoleAtt);

        var apiClientNotAdmin = new ApiClient();
        apiClientNotAdmin.setBasePath(getApiBasePath());
        apiClientNotAdmin.addDefaultHeader("Authorization", "Bearer " + HeaderBuilder.getJwtNotAdmin(getKeycloakUrl()));
        organisationV2ApiNotAdmin = new OrganisationV2Api(apiClientNotAdmin);
    }

// ------ JWT errors -------

    @Test
    void errorIfNoJwtToken_getOrganisationSlash() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder(new URI(getApiBasePath() + "/services/v2/organisation/æ/åø")).
                GET().
                build();

        var client = HttpClient.newHttpClient();
        var responseString = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(401, responseString.statusCode());
    }

    @Test
    void errorIfInvalidJwtToken_getOrganisationSlash() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder(new URI(getApiBasePath() + "/services/v2/organisation/æ/åø")).
                header("Authorization", "Bearer " + HeaderBuilder.getInvalidJwt()).
                GET().
                build();

        var client = HttpClient.newHttpClient();
        var responseString = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(401, responseString.statusCode());
    }

    @Test
    void errorIfNoRoleAttInToken_getOrganisationSlash() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder(new URI(getApiBasePath() + "/services/v2/organisation/æ/åø")).
                header("Authorization", "Bearer " + HeaderBuilder.getJwtNoRoleAtt(getKeycloakUrl())).
                GET().
                build();

        var client = HttpClient.newHttpClient();
        var responseString = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(401, responseString.statusCode());
    }

    @Test
    void errorIfNotAdmin_getOrganisationSlash() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder(new URI(getApiBasePath() + "/services/v2/organisation/æ/åø")).
                header("Authorization", "Bearer " + HeaderBuilder.getJwtNotAdmin(getKeycloakUrl())).
                GET().
                build();

        var client = HttpClient.newHttpClient();
        var responseString = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(403, responseString.statusCode());
    }

    @Test
    void errorIfNoJwtToken_servicesV2OrganisationCodeGet() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNoHeader.servicesV2OrganisationCodeGet(testOrg));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfInvalidJwtToken_servicesV2OrganisationCodeGet() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiInvalidJwt.servicesV2OrganisationCodeGet(testOrg));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfNoRoleAttInToken_servicesV2OrganisationCodeGet() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNoRoleAtt.servicesV2OrganisationCodeGet(testOrg));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfNotAdmin_servicesV2OrganisationCodeGet() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNotAdmin.servicesV2OrganisationCodeGet(testOrg));
        assertEquals(403, expectedException.getCode());
    }

    @Test
    void errorIfNoJwtToken_servicesV2OrganisationCodePut() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNoHeader.servicesV2OrganisationCodePut(testOrg, randomOrganisationUpdate()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfInvalidJwtToken_servicesV2OrganisationCodePut() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiInvalidJwt.servicesV2OrganisationCodePut(testOrg, randomOrganisationUpdate()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfNoRoleAttInToken_servicesV2OrganisationCodePut() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNoRoleAtt.servicesV2OrganisationCodePut(testOrg, randomOrganisationUpdate()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfNotAdmin_servicesV2OrganisationCodePut() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNotAdmin.servicesV2OrganisationCodePut(testOrg, randomOrganisationUpdate()));
        assertEquals(403, expectedException.getCode());
    }

    @Test
    void errorIfNoJwtToken_servicesV2OrganisationGet() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNoHeader.servicesV2OrganisationGet(testOrg));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfInvalidJwtToken_servicesV2OrganisationGet() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiInvalidJwt.servicesV2OrganisationGet(testOrg));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfNoRoleAttInToken_servicesV2OrganisationGet() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNoRoleAtt.servicesV2OrganisationGet(testOrg));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfNotAdmin_servicesV2OrganisationGet() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNotAdmin.servicesV2OrganisationGet(testOrg));
        assertEquals(403, expectedException.getCode());
    }

    @Test
    void errorIfNoJwtToken_servicesV2OrganisationParentCodePost() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNoHeader.servicesV2OrganisationParentCodePost(company1, randomOrganisationCreate()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfInvalidJwtToken_servicesV2OrganisationParentCodePost() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiInvalidJwt.servicesV2OrganisationParentCodePost(company1, randomOrganisationCreate()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfNoRoleAttInToken_servicesV2OrganisationParentCodePost() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNoRoleAtt.servicesV2OrganisationParentCodePost(company1, randomOrganisationCreate()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfNotAdmin_servicesV2OrganisationParentCodePost() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNotAdmin.servicesV2OrganisationParentCodePost(company1, randomOrganisationCreate()));
        assertEquals(403, expectedException.getCode());
    }

    @Test
    void errorIfNoJwtToken_servicesV2OrganisationPost() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNoHeader.servicesV2OrganisationPost(company1, randomOrganisationCreate()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfInvalidJwtToken_servicesV2OrganisationPost() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiInvalidJwt.servicesV2OrganisationPost(company1, randomOrganisationCreate()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfNoRoleAttInToken_servicesV2OrganisationPost() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNoRoleAtt.servicesV2OrganisationPost(company1, randomOrganisationCreate()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfNotAdmin_servicesV2OrganisationPost() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNotAdmin.servicesV2OrganisationPost(company1, randomOrganisationCreate()));
        assertEquals(403, expectedException.getCode());
    }

    @Test
    void errorIfNoJwtToken_servicesV2OrganisationUriPost() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNoHeader.servicesV2OrganisationUriPost(List.of("1239@test.dk")));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfInvalidJwtToken_servicesV2OrganisationUriPost() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiInvalidJwt.servicesV2OrganisationUriPost(List.of("1239@test.dk")));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfNoRoleAttInToken_servicesV2OrganisationUriPost() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNoRoleAtt.servicesV2OrganisationUriPost(List.of("1239@test.dk")));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfNotAdmin_servicesV2OrganisationUriPost() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNotAdmin.servicesV2OrganisationUriPost(List.of("1239@test.dk")));
        assertEquals(403, expectedException.getCode());
    }

// -------- No JWT Errors -----------

    @Test
    void testGetOrganisationSlashPath() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder(new URI(getApiBasePath() + "/services/v2/organisation/æ/åø")).
                header("Authorization", "Bearer " + HeaderBuilder.getJwtAllRoleAtt(getKeycloakUrl())).
                GET().
                build();

        var client = HttpClient.newHttpClient();

        var responseString = client.send(request, HttpResponse.BodyHandlers.ofString());

        var response = JSON.getGson().fromJson(responseString.body(), OrganisationResponse.class);

        assertNotNull(response);
        assertEquals("æ/åø", response.getCode());
        assertEquals("This is with a slash", response.getName());
        assertNull(response.getSmsSenderName());
    }

    @Test
    void testServicesV2OrganisationCodeGet() throws ApiException {
        var result = organisationV2Api.servicesV2OrganisationCodeGet(testOrg);

        assertNotNull(result);
        assertEquals(5, result.getGroupId(), 0);
        assertEquals(testOrg, result.getCode());
        assertEquals("company name test-org", result.getName());
        assertEquals(0, result.getPoolSize(), 0);
        assertEquals("MinAfsender", result.getSmsSenderName());
        assertEquals(Boolean.FALSE, result.getAllowCustomUriWithoutDomain());
        assertEquals("some_url", result.getSmsCallbackUrl());
        assertNull(result.getHistoryApiKey());
        assertNull(result.getDeviceWebhookEndpoint());
        assertNull(result.getDeviceWebhookEndpointKey());
    }

    @Test
    void testServicesV2OrganisationCodeGetNoSmsSenderName() throws ApiException {
        var result = organisationV2Api.servicesV2OrganisationCodeGet("kvak");

        assertNotNull(result);
        assertEquals("kvak", result.getCode());
        assertEquals("company name kvak", result.getName());
        assertNull(result.getSmsSenderName());
    }

    @Test
    void testServicesV2OrganisationCodePut() throws ApiException {
        var input = new OrganisationUpdate()
                .poolSize(123)
                .smsCallbackUrl(randomString())
                .allowCustomUriWithoutDomain(true)
                .smsSenderName(randomString().substring(0, 10))
                .historyApiKey(randomString())
                .deviceWebhookEndpoint(randomString())
                .deviceWebhookEndpointKey(randomString());

        var result = organisationV2Api.servicesV2OrganisationCodePut("company 2", input);

        assertNotNull(result);
        assertEquals(2, result.getGroupId(), 0);
        assertEquals("company 2", result.getCode());
        assertEquals("company name 2", result.getName());
        assertEquals(input.getPoolSize(), result.getPoolSize());
        assertEquals(input.getSmsCallbackUrl(), result.getSmsCallbackUrl());
        assertEquals(input.getAllowCustomUriWithoutDomain(), result.getAllowCustomUriWithoutDomain());
        assertEquals(input.getSmsSenderName(), result.getSmsSenderName());
        assertEquals(input.getHistoryApiKey(), result.getHistoryApiKey());
        assertEquals(input.getDeviceWebhookEndpoint(), result.getDeviceWebhookEndpoint());
        assertEquals(input.getDeviceWebhookEndpointKey(), result.getDeviceWebhookEndpointKey());
    }

    @Test
    void testServicesV2OrganisationCodePutNoValuesSet() throws ApiException {
        var input = new OrganisationUpdate();

        var result = organisationV2Api.servicesV2OrganisationCodePut("company 2", input);

        assertNotNull(result);
        assertEquals(2, result.getGroupId(), 0);
        assertEquals("company 2", result.getCode());
        assertEquals("company name 2", result.getName());
        assertEquals(0, result.getPoolSize(), 0);
        assertNull(result.getSmsCallbackUrl());
        assertNotNull(result.getAllowCustomUriWithoutDomain());
        assertFalse(result.getAllowCustomUriWithoutDomain());
        assertNull(result.getSmsSenderName());
        assertNull(result.getHistoryApiKey());
        assertNull(result.getDeviceWebhookEndpoint());
        assertNull(result.getDeviceWebhookEndpointKey());
    }

    @Test
    void testServicesV2OrganisationGet() throws ApiException {
        var result = organisationV2Api.servicesV2OrganisationGet(testOrg);

        assertNotNull(result);
        assertEquals(5, result.getGroupId(), 0);
        assertEquals(testOrg, result.getCode());
        assertEquals("company name test-org", result.getName());
        assertEquals(0, result.getPoolSize(), 0);
        assertEquals("MinAfsender", result.getSmsSenderName());
        assertEquals(Boolean.FALSE, result.getAllowCustomUriWithoutDomain());
        assertEquals("some_url", result.getSmsCallbackUrl());
        assertNull(result.getHistoryApiKey());
        assertNull(result.getDeviceWebhookEndpoint());
        assertNull(result.getDeviceWebhookEndpointKey());
    }

    @Test
    void testServicesV2OrganisationGetWebhookNotNull() throws ApiException {
        var result = organisationV2Api.servicesV2OrganisationGet("parent");

        assertNotNull(result);
        assertEquals("parent", result.getCode());
        assertEquals("parent org", result.getName());
        assertEquals(20, result.getPoolSize(), 0);
        assertEquals("sms-sender", result.getSmsSenderName());
        assertEquals("callback", result.getSmsCallbackUrl());
        assertEquals("device-webhook-endpoint", result.getDeviceWebhookEndpoint());
        assertEquals("device-webhook-endpoint-key", result.getDeviceWebhookEndpointKey());
    }

    @Test
    void testServicesV2OrganisationGetWithSlash() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder(new URI(getApiBasePath() + "/services/v2/organisation?organisationCode=æ/åø")).
                header("Authorization", "Bearer " + HeaderBuilder.getJwtAllRoleAtt(getKeycloakUrl())).
                GET().
                build();

        var client = HttpClient.newHttpClient();

        var responseString = client.send(request, HttpResponse.BodyHandlers.ofString());

        var response = JSON.getGson().fromJson(responseString.body(), OrganisationResponse.class);

        assertNotNull(response);
        assertEquals("æ/åø", response.getCode());
        assertEquals("This is with a slash", response.getName());
        assertNull(response.getSmsSenderName());
    }

    @Test
    void testServicesV2OrganisationParentCodePost() throws ApiException {
        var inputOrganisation = new OrganisationCreate();
        inputOrganisation.setCode(UUID.randomUUID().toString());

        var result = organisationV2Api.servicesV2OrganisationParentCodePost(company1, inputOrganisation);
        assertNotNull(result);

        assertEquals(inputOrganisation.getCode(), result.getCode());
        assertEquals(inputOrganisation.getCode(), result.getName());
        assertNull(result.getSmsSenderName());
        assertNull(result.getSmsCallbackUrl());
        assertEquals(0, result.getPoolSize(), 0);
    }

    @Test
    void testServicesV2OrganisationParentCodePostAlreadyExists() {
        var input = "from_template";

        var inputOrganisation = new OrganisationCreate();
        inputOrganisation.code(testOrg);

        var exception = assertThrows(ApiException.class, () -> organisationV2Api.servicesV2OrganisationParentCodePost(input, inputOrganisation));
        assertNotNull(exception);
        assertEquals(400, exception.getCode());
        var error = JSON.getGson().fromJson(exception.getResponseBody(), BasicError.class);
        assertEquals("Organisation test-org already exists.", error.getError());
    }

    @Test
    void testServicesV2OrganisationParentCodePostParentNotFound() {
        var input = "i_dont_exist";
        var inputOrganisation = new OrganisationCreate();
        inputOrganisation.code("code");

        var exception = assertThrows(ApiException.class, () -> organisationV2Api.servicesV2OrganisationParentCodePost(input, inputOrganisation));
        assertNotNull(exception);
        assertEquals(400, exception.getCode());
        var error = JSON.getGson().fromJson(exception.getResponseBody(), BasicError.class);
        assertEquals("Parent organisation i_dont_exist not found", error.getError());
    }

    @Test
    void testServicesV2OrganisationPostOnlyRequiredValues() throws ApiException {
        var input = "æ/åø";
        var inputOrganisation = new OrganisationCreate();
        inputOrganisation.setCode(UUID.randomUUID().toString());

        var result = organisationV2Api.servicesV2OrganisationPost(input, inputOrganisation);
        assertNotNull(result);

        assertEquals(inputOrganisation.getCode(), result.getCode());
        assertEquals(inputOrganisation.getCode(), result.getName());
        assertNull(result.getSmsSenderName());
        assertNull(result.getSmsCallbackUrl());
        assertEquals(0, result.getPoolSize(), 0);
        assertEquals(Boolean.FALSE, result.getAllowCustomUriWithoutDomain());
        assertNull(result.getHistoryApiKey());
        assertNull(result.getDeviceWebhookEndpoint());
        assertNull(result.getDeviceWebhookEndpointKey());
    }

    @Test
    void testServicesV2OrganisationPostAllValues() throws ApiException {
        var input = "æ/åø";

        var inputOrganisation = new OrganisationCreate()
                .code(randomString())
                .name(randomString())
                .poolSize(321)
                .smsSenderName(randomString().substring(0, 10))
                .allowCustomUriWithoutDomain(true)
                .smsCallbackUrl(randomString())
                .historyApiKey(randomString())
                .deviceWebhookEndpoint(randomString())
                .deviceWebhookEndpointKey(randomString());

        var result = organisationV2Api.servicesV2OrganisationPost(input, inputOrganisation);
        assertNotNull(result);

        assertEquals(inputOrganisation.getCode(), result.getCode());
        assertEquals(inputOrganisation.getName(), result.getName());
        assertEquals(inputOrganisation.getSmsSenderName(), result.getSmsSenderName());
        assertEquals(inputOrganisation.getSmsCallbackUrl(), result.getSmsCallbackUrl());
        assertEquals(inputOrganisation.getPoolSize(), result.getPoolSize());
        assertEquals(inputOrganisation.getAllowCustomUriWithoutDomain(), result.getAllowCustomUriWithoutDomain());
        assertEquals(inputOrganisation.getHistoryApiKey(), result.getHistoryApiKey());
        assertEquals(inputOrganisation.getDeviceWebhookEndpoint(), result.getDeviceWebhookEndpoint());
        assertEquals(inputOrganisation.getDeviceWebhookEndpointKey(), result.getDeviceWebhookEndpointKey());
    }

    @Test
    void testServicesV2OrganisationUriPostWhereStatusPROVISIONED_OK() throws ApiException {
        var uris = List.of("1239@test.dk");

        var result = organisationV2Api.servicesV2OrganisationUriPost(uris);

        assertFalse(result.isEmpty());
        assertEquals("pool-test-org", result.getFirst().getCode());
        assertEquals("company name another-test-org", result.getFirst().getName());
        assertEquals(Long.valueOf(7), result.getFirst().getGroupId());
        assertEquals(uris.getFirst(), result.getFirst().getUri());
    }

    @Test
    void testServicesV2OrganisationUriPostWhereStatusNotPROVISIONED_OK() throws ApiException {
        var uris = List.of("1230@test.dk");

        var result = organisationV2Api.servicesV2OrganisationUriPost(uris);

        assertTrue(result.isEmpty());
    }

// --------- CORS ----------

    @Test
    void testCorsAllowed() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/services/v2/organisation", getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://allowed:4100")
                .header("Access-Control-Request-Method", "POST")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void testCorsDenied() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/services/v2/organisation", getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://denied:4200")
                .header("Access-Control-Request-Method", "POST")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(403, response.statusCode());
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }
    
    private OrganisationCreate randomOrganisationCreate() {
        return new OrganisationCreate()
                .code(randomString())
                .name(randomString())
                .poolSize(321)
                .smsCallbackUrl(randomString())
                .allowCustomUriWithoutDomain(true)
                .smsSenderName(randomString().substring(0, 10))
                .historyApiKey(randomString())
                .deviceWebhookEndpoint(randomString())
                .deviceWebhookEndpointKey(randomString());
    }

    private OrganisationUpdate randomOrganisationUpdate() {
        return new OrganisationUpdate()
                .poolSize(123)
                .smsCallbackUrl(randomString())
                .allowCustomUriWithoutDomain(true)
                .smsSenderName(randomString().substring(0, 10))
                .historyApiKey(randomString())
                .deviceWebhookEndpoint(randomString())
                .deviceWebhookEndpointKey(randomString());
    }
}

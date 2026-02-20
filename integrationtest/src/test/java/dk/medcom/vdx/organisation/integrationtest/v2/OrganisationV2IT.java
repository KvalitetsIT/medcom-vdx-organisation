package dk.medcom.vdx.organisation.integrationtest.v2;

import dk.medcom.vdx.organisation.integrationtest.AbstractIntegrationTest;
import dk.medcom.vdx.organisation.integrationtest.v2.helper.HeaderBuilder;
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
    
    private final OrganisationV2Api organisationV2Api;
    private final OrganisationV2Api organisationV2ApiNoHeader;
    private final OrganisationV2Api organisationV2ApiNoRoleAtt;
    private final OrganisationV2Api organisationV2ApiNotAdmin;
    private final OrganisationV2Api organisationV2ApiExpiredJwt;
    private final OrganisationV2Api organisationV2ApiInvalidIssuerJwt;
    private final OrganisationV2Api organisationV2ApiTamperedJwt;
    private final OrganisationV2Api organisationV2ApiMissingSignatureJwt;
    private final OrganisationV2Api organisationV2ApiDifferentSignedJwt;

    OrganisationV2IT() {
        var keycloakUrl = getKeycloakUrl();

        organisationV2Api = createClient(HeaderBuilder.getJwtAllRoleAtt(keycloakUrl));
        organisationV2ApiNoHeader = createClient(null);
        organisationV2ApiNoRoleAtt = createClient(HeaderBuilder.getJwtNoRoleAtt(keycloakUrl));
        organisationV2ApiNotAdmin = createClient(HeaderBuilder.getJwtNotAdmin(keycloakUrl));
        organisationV2ApiExpiredJwt = createClient(HeaderBuilder.getExpiredJwt(keycloakUrl));
        organisationV2ApiInvalidIssuerJwt = createClient(HeaderBuilder.getInvalidIssuerJwt());
        organisationV2ApiTamperedJwt = createClient(HeaderBuilder.getTamperedJwt(keycloakUrl));
        organisationV2ApiMissingSignatureJwt = createClient(HeaderBuilder.getMissingSignatureJwt(keycloakUrl));
        organisationV2ApiDifferentSignedJwt = createClient(HeaderBuilder.getDifferentSignedJwt(keycloakUrl));
    }

    private OrganisationV2Api createClient(String token) {
        var apiClient = new ApiClient();
        apiClient.setBasePath(getApiBasePath());
        if (token != null) {
            apiClient.addDefaultHeader("Authorization", "Bearer " + token);
        }
        return new OrganisationV2Api(apiClient);
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
    void errorIfExpiredJwtToken_getOrganisationSlash() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder(new URI(getApiBasePath() + "/services/v2/organisation/æ/åø")).
                header("Authorization", "Bearer " + HeaderBuilder.getExpiredJwt(getKeycloakUrl())).
                GET().
                build();

        var client = HttpClient.newHttpClient();
        var responseString = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(401, responseString.statusCode());
    }

    @Test
    void errorIfInvalidIssuerJwtToken_getOrganisationSlash() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder(new URI(getApiBasePath() + "/services/v2/organisation/æ/åø")).
                header("Authorization", "Bearer " + HeaderBuilder.getInvalidIssuerJwt()).
                GET().
                build();

        var client = HttpClient.newHttpClient();
        var responseString = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(401, responseString.statusCode());
    }

    @Test
    void errorIfTamperedJwtToken_getOrganisationSlash() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder(new URI(getApiBasePath() + "/services/v2/organisation/æ/åø")).
                header("Authorization", "Bearer " + HeaderBuilder.getTamperedJwt(getKeycloakUrl())).
                GET().
                build();

        var client = HttpClient.newHttpClient();
        var responseString = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(401, responseString.statusCode());
    }

    @Test
    void errorIfMissingSignatureJwtToken_getOrganisationSlash() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder(new URI(getApiBasePath() + "/services/v2/organisation/æ/åø")).
                header("Authorization", "Bearer " + HeaderBuilder.getMissingSignatureJwt(getKeycloakUrl())).
                GET().
                build();

        var client = HttpClient.newHttpClient();
        var responseString = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(401, responseString.statusCode());
    }

    @Test
    void errorIfDifferentSignedJwtToken_getOrganisationSlash() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder(new URI(getApiBasePath() + "/services/v2/organisation/æ/åø")).
                header("Authorization", "Bearer " + HeaderBuilder.getDifferentSignedJwt(getKeycloakUrl())).
                GET().
                build();

        var client = HttpClient.newHttpClient();
        var responseString = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(401, responseString.statusCode());
    }

    @Test
    void errorIfNoJwtToken_servicesV2OrganisationCodeGet() {
        assertThrowsWithStatus(401, () -> organisationV2ApiNoHeader.servicesV2OrganisationCodeGet(testOrg));
    }

    @Test
    void errorIfNoRoleAttInToken_servicesV2OrganisationCodeGet() {
        assertThrowsWithStatus(401, () -> organisationV2ApiNoRoleAtt.servicesV2OrganisationCodeGet(testOrg));
    }

    @Test
    void errorIfNotAdmin_servicesV2OrganisationCodeGet() {
        assertThrowsWithStatus(403, () -> organisationV2ApiNotAdmin.servicesV2OrganisationCodeGet(testOrg));
    }

    @Test
    void errorIfExpiredJwtToken_servicesV2OrganisationCodeGet() {
        assertThrowsWithStatus(401, () -> organisationV2ApiExpiredJwt.servicesV2OrganisationCodeGet(testOrg));
    }

    @Test
    void errorIfInvalidIssuerJwtToken_servicesV2OrganisationCodeGet() {
        assertThrowsWithStatus(401, () -> organisationV2ApiInvalidIssuerJwt.servicesV2OrganisationCodeGet(testOrg));
    }

    @Test
    void errorIfTamperedJwtToken_servicesV2OrganisationCodeGet() {
        assertThrowsWithStatus(401, () -> organisationV2ApiTamperedJwt.servicesV2OrganisationCodeGet(testOrg));
    }

    @Test
    void errorIfMissingSignatureJwtToken_servicesV2OrganisationCodeGet() {
        assertThrowsWithStatus(401, () -> organisationV2ApiMissingSignatureJwt.servicesV2OrganisationCodeGet(testOrg));
    }

    @Test
    void errorIfDifferentSignedJwtToken_servicesV2OrganisationCodeGet() {
        assertThrowsWithStatus(401, () -> organisationV2ApiDifferentSignedJwt.servicesV2OrganisationCodeGet(testOrg));
    }

    @Test
    void errorIfNoJwtToken_servicesV2OrganisationCodePut() {
        assertThrowsWithStatus(401, () -> organisationV2ApiNoHeader.servicesV2OrganisationCodePut(testOrg, randomOrganisationUpdate()));
    }

    @Test
    void errorIfNoRoleAttInToken_servicesV2OrganisationCodePut() {
        assertThrowsWithStatus(401, () -> organisationV2ApiNoRoleAtt.servicesV2OrganisationCodePut(testOrg, randomOrganisationUpdate()));
    }

    @Test
    void errorIfNotAdmin_servicesV2OrganisationCodePut() {
        assertThrowsWithStatus(403, () -> organisationV2ApiNotAdmin.servicesV2OrganisationCodePut(testOrg, randomOrganisationUpdate()));
    }

    @Test
    void errorIfExpiredJwtToken_servicesV2OrganisationCodePut() {
        assertThrowsWithStatus(401, () -> organisationV2ApiExpiredJwt.servicesV2OrganisationCodePut(testOrg, randomOrganisationUpdate()));
    }

    @Test
    void errorIfInvalidIssuerJwtToken_servicesV2OrganisationCodePut() {
        assertThrowsWithStatus(401, () -> organisationV2ApiInvalidIssuerJwt.servicesV2OrganisationCodePut(testOrg, randomOrganisationUpdate()));
    }

    @Test
    void errorIfTamperedJwtToken_servicesV2OrganisationCodePut() {
        assertThrowsWithStatus(401, () -> organisationV2ApiTamperedJwt.servicesV2OrganisationCodePut(testOrg, randomOrganisationUpdate()));
    }

    @Test
    void errorIfMissingSignatureJwtToken_servicesV2OrganisationCodePut() {
        assertThrowsWithStatus(401, () -> organisationV2ApiMissingSignatureJwt.servicesV2OrganisationCodePut(testOrg, randomOrganisationUpdate()));
    }

    @Test
    void errorIfDifferentSignedJwtToken_servicesV2OrganisationCodePut() {
        assertThrowsWithStatus(401, () -> organisationV2ApiDifferentSignedJwt.servicesV2OrganisationCodePut(testOrg, randomOrganisationUpdate()));
    }

    @Test
    void errorIfNoJwtToken_servicesV2OrganisationGet() {
        assertThrowsWithStatus(401, () -> organisationV2ApiNoHeader.servicesV2OrganisationGet(testOrg));
    }

    @Test
    void errorIfNoRoleAttInToken_servicesV2OrganisationGet() {
        assertThrowsWithStatus(401, () -> organisationV2ApiNoRoleAtt.servicesV2OrganisationGet(testOrg));
    }

    @Test
    void errorIfNotAdmin_servicesV2OrganisationGet() {
        assertThrowsWithStatus(403, () -> organisationV2ApiNotAdmin.servicesV2OrganisationGet(testOrg));
    }

    @Test
    void errorIfExpiredJwtToken_servicesV2OrganisationGet() {
        assertThrowsWithStatus(401, () -> organisationV2ApiExpiredJwt.servicesV2OrganisationGet(testOrg));
    }

    @Test
    void errorIfInvalidIssuerJwtToken_servicesV2OrganisationGet() {
        assertThrowsWithStatus(401, () -> organisationV2ApiInvalidIssuerJwt.servicesV2OrganisationGet(testOrg));
    }

    @Test
    void errorIfTamperedJwtToken_servicesV2OrganisationGet() {
        assertThrowsWithStatus(401, () -> organisationV2ApiTamperedJwt.servicesV2OrganisationGet(testOrg));
    }

    @Test
    void errorIfMissingSignatureJwtToken_servicesV2OrganisationGet() {
        assertThrowsWithStatus(401, () -> organisationV2ApiMissingSignatureJwt.servicesV2OrganisationGet(testOrg));
    }

    @Test
    void errorIfDifferentSignedJwtToken_servicesV2OrganisationGet() {
        assertThrowsWithStatus(401, () -> organisationV2ApiDifferentSignedJwt.servicesV2OrganisationGet(testOrg));
    }

    @Test
    void errorIfNoJwtToken_servicesV2OrganisationParentCodePost() {
        assertThrowsWithStatus(401, () -> organisationV2ApiNoHeader.servicesV2OrganisationParentCodePost(company1, randomOrganisationCreate()));
    }

    @Test
    void errorIfNoRoleAttInToken_servicesV2OrganisationParentCodePost() {
        assertThrowsWithStatus(401, () -> organisationV2ApiNoRoleAtt.servicesV2OrganisationParentCodePost(company1, randomOrganisationCreate()));
    }

    @Test
    void errorIfNotAdmin_servicesV2OrganisationParentCodePost() {
        assertThrowsWithStatus(403, () -> organisationV2ApiNotAdmin.servicesV2OrganisationParentCodePost(company1, randomOrganisationCreate()));
    }

    @Test
    void errorIfExpiredJwtToken_servicesV2OrganisationParentCodePost() {
        assertThrowsWithStatus(401, () -> organisationV2ApiExpiredJwt.servicesV2OrganisationParentCodePost(company1, randomOrganisationCreate()));
    }

    @Test
    void errorIfInvalidIssuerJwtToken_servicesV2OrganisationParentCodePost() {
        assertThrowsWithStatus(401, () -> organisationV2ApiInvalidIssuerJwt.servicesV2OrganisationParentCodePost(company1, randomOrganisationCreate()));
    }

    @Test
    void errorIfTamperedJwtToken_servicesV2OrganisationParentCodePost() {
        assertThrowsWithStatus(401, () -> organisationV2ApiTamperedJwt.servicesV2OrganisationParentCodePost(company1, randomOrganisationCreate()));
    }

    @Test
    void errorIfMissingSignatureJwtToken_servicesV2OrganisationParentCodePost() {
        assertThrowsWithStatus(401, () -> organisationV2ApiMissingSignatureJwt.servicesV2OrganisationParentCodePost(company1, randomOrganisationCreate()));
    }

    @Test
    void errorIfDifferentSignedJwtToken_servicesV2OrganisationParentCodePost() {
        assertThrowsWithStatus(401, () -> organisationV2ApiDifferentSignedJwt.servicesV2OrganisationParentCodePost(company1, randomOrganisationCreate()));
    }

    @Test
    void errorIfNoJwtToken_servicesV2OrganisationPost() {
        assertThrowsWithStatus(401, () -> organisationV2ApiNoHeader.servicesV2OrganisationPost(company1, randomOrganisationCreate()));
    }

    @Test
    void errorIfNoRoleAttInToken_servicesV2OrganisationPost() {
        assertThrowsWithStatus(401, () -> organisationV2ApiNoRoleAtt.servicesV2OrganisationPost(company1, randomOrganisationCreate()));
    }

    @Test
    void errorIfNotAdmin_servicesV2OrganisationPost() {
        assertThrowsWithStatus(403, () -> organisationV2ApiNotAdmin.servicesV2OrganisationPost(company1, randomOrganisationCreate()));
    }

    @Test
    void errorIfExpiredJwtToken_servicesV2OrganisationPost() {
        assertThrowsWithStatus(401, () -> organisationV2ApiExpiredJwt.servicesV2OrganisationPost(company1, randomOrganisationCreate()));
    }

    @Test
    void errorIfInvalidIssuerJwtToken_servicesV2OrganisationPost() {
        assertThrowsWithStatus(401, () -> organisationV2ApiInvalidIssuerJwt.servicesV2OrganisationPost(company1, randomOrganisationCreate()));
    }

    @Test
    void errorIfTamperedJwtToken_servicesV2OrganisationPost() {
        assertThrowsWithStatus(401, () -> organisationV2ApiTamperedJwt.servicesV2OrganisationPost(company1, randomOrganisationCreate()));
    }

    @Test
    void errorIfMissingSignatureJwtToken_servicesV2OrganisationPost() {
        assertThrowsWithStatus(401, () -> organisationV2ApiMissingSignatureJwt.servicesV2OrganisationPost(company1, randomOrganisationCreate()));
    }

    @Test
    void errorIfDifferentSignedJwtToken_servicesV2OrganisationPost() {
        assertThrowsWithStatus(401, () -> organisationV2ApiDifferentSignedJwt.servicesV2OrganisationPost(company1, randomOrganisationCreate()));
    }

    @Test
    void errorIfNoJwtToken_servicesV2OrganisationUriPost() {
        assertThrowsWithStatus(401, () -> organisationV2ApiNoHeader.servicesV2OrganisationUriPost(List.of("1239@test.dk")));
    }

    @Test
    void errorIfNoRoleAttInToken_servicesV2OrganisationUriPost() {
        assertThrowsWithStatus(401, () -> organisationV2ApiNoRoleAtt.servicesV2OrganisationUriPost(List.of("1239@test.dk")));
    }

    @Test
    void errorIfNotAdmin_servicesV2OrganisationUriPost() {
        assertThrowsWithStatus(403, () -> organisationV2ApiNotAdmin.servicesV2OrganisationUriPost(List.of("1239@test.dk")));
    }

    @Test
    void errorIfExpiredJwtToken_servicesV2OrganisationUriPost() {
        assertThrowsWithStatus(401, () -> organisationV2ApiExpiredJwt.servicesV2OrganisationUriPost(List.of("1239@test.dk")));
    }

    @Test
    void errorIfInvalidIssuerJwtToken_servicesV2OrganisationUriPost() {
        assertThrowsWithStatus(401, () -> organisationV2ApiInvalidIssuerJwt.servicesV2OrganisationUriPost(List.of("1239@test.dk")));
    }

    @Test
    void errorIfTamperedJwtToken_servicesV2OrganisationUriPost() {
        assertThrowsWithStatus(401, () -> organisationV2ApiTamperedJwt.servicesV2OrganisationUriPost(List.of("1239@test.dk")));
    }

    @Test
    void errorIfMissingSignatureJwtToken_servicesV2OrganisationUriPost() {
        assertThrowsWithStatus(401, () -> organisationV2ApiMissingSignatureJwt.servicesV2OrganisationUriPost(List.of("1239@test.dk")));
    }

    @Test
    void errorIfDifferentSignedJwtToken_servicesV2OrganisationUriPost() {
        assertThrowsWithStatus(401, () -> organisationV2ApiDifferentSignedJwt.servicesV2OrganisationUriPost(List.of("1239@test.dk")));
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
    void testCorsAllowedServicesV2OrganisationGet() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/services/v2/organisation", getApiBasePath())))
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
    void testCorsDeniedServicesV2OrganisationGet() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/services/v2/organisation", getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://denied:4200")
                .header("Access-Control-Request-Method", "GET")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(403, response.statusCode());
    }

    @Test
    void testCorsAllowedServicesV2OrganisationPost() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/services/v2/organisation", getApiBasePath())))
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
    void testCorsDeniedServicesV2OrganisationPost() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/services/v2/organisation", getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://denied:4200")
                .header("Access-Control-Request-Method", "POST")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(403, response.statusCode());
    }

    @Test
    void testCorsAllowedServicesV2OrganisationCodeGet() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/services/v2/organisation/some-org", getApiBasePath())))
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
    void testCorsDeniedServicesV2OrganisationCodeGet() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/services/v2/organisation/some-org", getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://denied:4200")
                .header("Access-Control-Request-Method", "GET")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(403, response.statusCode());
    }

    @Test
    void testCorsAllowedServicesV2OrganisationCodePut() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/services/v2/organisation/some-org", getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://allowed:4100")
                .header("Access-Control-Request-Method", "PUT")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        var headers = response.headers().map();
        assertTrue(headers.get("Access-Control-Allow-Methods").contains("PUT"));
        assertTrue(headers.get("Access-Control-Allow-Origin").contains("http://allowed:4100"));
        assertEquals(200, response.statusCode());
    }

    @Test
    void testCorsDeniedServicesV2OrganisationCodePut() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/services/v2/organisation/some-org", getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://denied:4200")
                .header("Access-Control-Request-Method", "PUT")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(403, response.statusCode());
    }

    @Test
    void testCorsAllowedServicesV2OrganisationParentCodePost() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/services/v2/organisation/some-org", getApiBasePath())))
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
    void testCorsDeniedServicesV2OrganisationParentCodePost() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/services/v2/organisation/some-org", getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://denied:4200")
                .header("Access-Control-Request-Method", "POST")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(403, response.statusCode());
    }

    @Test
    void testCorsAllowedServicesV2OrganisationUriPost() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/services/v2/organisation/uri", getApiBasePath())))
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
    void testCorsDeniedServicesV2OrganisationUriPost() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/services/v2/organisation/uri", getApiBasePath())))
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

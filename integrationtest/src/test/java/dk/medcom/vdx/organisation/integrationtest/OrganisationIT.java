package dk.medcom.vdx.organisation.integrationtest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.JSON;
import org.openapitools.client.api.OrganisationApi;
import org.openapitools.client.api.OrganisationTreeApi;
import org.openapitools.client.model.BasicError;
import org.openapitools.client.model.Organisation;
import org.openapitools.client.model.OrganisationCreate;
import org.openapitools.client.model.Organisationtree;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class OrganisationIT extends AbstractIntegrationTest {
    private OrganisationApi organisationApi;
    private OrganisationTreeApi organisationTreeApi;

    private static final String SESSION_MEDCOM_ORGANISATION = "ewogICAiVXNlckF0dHJpYnV0ZXMiOnsKICAgICAgImRrOm1lZGNvbTp2aWRlbzpyb2xlIjpbCiAgICAgICAgICJtZWV0aW5nLWFkbWluIgogICAgICBdLAogICAgICAiZGs6bWVkY29tOm9yZ2FuaXNhdGlvbl9pZCI6WwogICAgICAgICAibWVkY29tIgogICAgICBdLAogICAgICAiZGs6bWVkY29tOmVtYWlsIjpbCiAgICAgICAgICJlbWFpbEBkb21haW4uY29tIgogICAgICBdCiAgIH0KfQo=";
    private static final String SESSION_NOT_ADMIN = "ewogICAiVXNlckF0dHJpYnV0ZXMiOnsKICAgICAgImRrOm1lZGNvbTp2aWRlbzpyb2xlIjpbCiAgICAgICAgICJpLWFtLXVua25vd24iCiAgICAgIF0sCiAgICAgICJkazptZWRjb206b3JnYW5pc2F0aW9uX2lkIjpbCiAgICAgICAgICJtZWRjb20iCiAgICAgIF0sCiAgICAgICJkazptZWRjb206ZW1haWwiOlsKICAgICAgICAgImVtYWlsQGRvbWFpbi5jb20iCiAgICAgIF0KICAgfQp9Cgo=";
    private static final String SESSION_MEETING_USER = "ewogICAiVXNlckF0dHJpYnV0ZXMiOnsKICAgICAgImRrOm1lZGNvbTp2aWRlbzpyb2xlIjpbCiAgICAgICAgICJtZWV0aW5nLXVzZXIiCiAgICAgIF0sCiAgICAgICJkazptZWRjb206b3JnYW5pc2F0aW9uX2lkIjpbCiAgICAgICAgICJtZWRjb20iCiAgICAgIF0sCiAgICAgICJkazptZWRjb206ZW1haWwiOlsKICAgICAgICAgImVtYWlsQGRvbWFpbi5jb20iCiAgICAgIF0KICAgfQp9Cgo=";
    private OrganisationApi unauthorizedOrganisationApi;

    @Before
    public void setupApiClient() {
        var apiClient = new ApiClient()
                .setBasePath(getApiBasePath())
                .addDefaultHeader("X-SESSIONDATA", SESSION_MEDCOM_ORGANISATION);
        organisationApi = new OrganisationApi(apiClient);

        organisationTreeApi = new OrganisationTreeApi(apiClient);

        var unauthorizedApiClient = new ApiClient()
                .setBasePath(getApiBasePath())
                .addDefaultHeader("X-SESSIONDATA", SESSION_NOT_ADMIN);

        unauthorizedOrganisationApi = new OrganisationApi(unauthorizedApiClient);

    }

    @Test
    public void testOrganisationByUri_StatusPROVISIONED_OK() throws ApiException {
        // Given
        List<String> uris = new ArrayList<>();
        uris.add("1239@test.dk");

        // When
        var organisations = organisationApi.servicesOrganisationUriPost(uris);

        // Then
        Assert.assertFalse(organisations.isEmpty());
        Assert.assertEquals("pool-test-org", organisations.get(0).getCode());
        Assert.assertEquals("company name another-test-org", organisations.get(0).getName());
        Assert.assertEquals(Long.valueOf(7), organisations.get(0).getGroupId());
        Assert.assertEquals(uris.get(0), organisations.get(0).getUri());
    }

    @Test
    public void testOrganisationByUri_StatusNotPROVISIONED_OK() throws ApiException {
        // Given
        List<String> uris = new ArrayList<>();
        uris.add("1230@test.dk");

        // When
        var organisations = organisationApi.servicesOrganisationUriPost(uris);

        // Then
        Assert.assertTrue(organisations.isEmpty());
    }

    @Test
    public void testReadOrganisation() throws ApiException {
        var response = organisationApi.servicesOrganisationCodeGet("test-org");

        assertNotNull(response);
        assertEquals("test-org", response.getCode());
        assertEquals("company name test-org", response.getName());
        assertEquals("MinAfsender", response.getSmsSenderName());
        assertEquals("some_url", response.getSmsCallbackUrl());
    }

    @Test
    public void testReadOrganisationNoSmsSenderName() throws ApiException {
        var response = organisationApi.servicesOrganisationCodeGet("kvak");

        assertNotNull(response);
        assertEquals("kvak", response.getCode());
        assertEquals("company name kvak", response.getName());
        assertNull(response.getSmsSenderName());
    }

    @Test
    public void testReadOrganisationWithSlash() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder(new URI(getApiBasePath() + "/services/organisation/æ/åø")).
                header("X-SESSIONDATA", SESSION_MEDCOM_ORGANISATION).
                GET().
                build();

        var client = HttpClient.newHttpClient();

        var responseString = client.send(request, HttpResponse.BodyHandlers.ofString());

        var response = JSON.getGson().fromJson(responseString.body(), Organisation.class);

        assertNotNull(response);
        assertEquals("æ/åø", response.getCode());
        assertEquals("This is with a slash", response.getName());
        assertNull(response.getSmsSenderName());
    }

    @Test
    public void testReadOrganisationTree() throws ApiException {
        var response = organisationTreeApi.servicesOrganisationtreeCodeGet("child");
        assertNotNull(response);

        var childOrganisation = response.getChildren().get(0).getChildren().get(0).getChildren().get(0);
        assertEquals("child", childOrganisation.getCode());
        assertEquals("child org", childOrganisation.getName());
        assertNull(childOrganisation.getSmsSenderName());
        assertNull(childOrganisation.getSmsCallbackUrl());

        var parentOrganisation = response.getChildren().get(0);
        assertEquals("parent", parentOrganisation.getCode());
        assertEquals("parent org", parentOrganisation.getName());
        assertEquals("sms-sender", parentOrganisation.getSmsSenderName());
        assertEquals("callback", parentOrganisation.getSmsCallbackUrl());
    }

    @Test
    public void testReadOrganisationTreeWithSlash() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder(new URI(getApiBasePath() + "/services/organisationtree/æ/åø")).
                header("X-SESSIONDATA", SESSION_MEDCOM_ORGANISATION).
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
    }

    @Test
    public void testReadOrganisationTreeWithSlashQueryParameter() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder(new URI(getApiBasePath() + "/services/organisationtree?organisationCode=æ/åø")).
                header("X-SESSIONDATA", SESSION_MEDCOM_ORGANISATION).
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
    }

    @Test
    public void testReadOrganisationWithSlashQueryParameter() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder(new URI(getApiBasePath() + "/services/organisation?organisationCode=æ/åø")).
                header("X-SESSIONDATA", SESSION_MEDCOM_ORGANISATION).
                GET().
                build();

        var client = HttpClient.newHttpClient();

        var responseString = client.send(request, HttpResponse.BodyHandlers.ofString());

        var response = JSON.getGson().fromJson(responseString.body(), Organisation.class);

        assertNotNull(response);
        assertEquals("æ/åø", response.getCode());
        assertEquals("This is with a slash", response.getName());
        assertNull(response.getSmsSenderName());
    }

    @Test
    public void testUnauthorizedWhenUnknownRole() {
        var expectedException = assertThrows(ApiException.class, () -> unauthorizedOrganisationApi.servicesOrganisationCodeGet("some code"));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void testUnauthorizedWhenNotAdmin() {
        var unauthorizedApiClient = new ApiClient()
                .setBasePath(getApiBasePath())
                .addDefaultHeader("X-SESSIONDATA", SESSION_MEETING_USER);

        var api = new OrganisationApi(unauthorizedApiClient);

        var expectedException = assertThrows(ApiException.class, () -> api.servicesOrganisationCodeGet("some code"));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void testCreateOrganisationAlreadyExists() {
        var input = "from_template";

        var inputOrganisation = new OrganisationCreate();
        inputOrganisation.code("test-org");

        var exception = assertThrows(ApiException.class, () -> organisationApi.servicesOrganisationParentCodePost(input, inputOrganisation));
        assertNotNull(exception);
        assertEquals(400, exception.getCode());
        var error = JSON.getGson().fromJson(exception.getResponseBody(), BasicError.class);
        assertEquals("Organisation test-org already exists.", error.getError());
    }

    @Test
    public void testCreateOrganisation() throws ApiException {
        var input = "child";
        var inputOrganisation = new OrganisationCreate();
        inputOrganisation.setCode(UUID.randomUUID().toString());

        var result = organisationApi.servicesOrganisationParentCodePost(input, inputOrganisation);
        assertNotNull(result);

        assertEquals(inputOrganisation.getCode(), result.getCode());
        assertEquals(inputOrganisation.getCode(), result.getName());
        assertNull(result.getSmsSenderName());
        assertNull(result.getSmsCallbackUrl());
        assertEquals(0, result.getPoolSize());
    }

    @Test
    public void testCreateOrganisationParentInQuery() throws ApiException {
        var input = "æ/åø";
        var inputOrganisation = new OrganisationCreate();
        inputOrganisation.setCode(UUID.randomUUID().toString());

        var result = organisationApi.servicesOrganisationPost(input, inputOrganisation);
        assertNotNull(result);

        assertEquals(inputOrganisation.getCode(), result.getCode());
        assertEquals(inputOrganisation.getCode(), result.getName());
        assertNull(result.getSmsSenderName());
        assertNull(result.getSmsCallbackUrl());
        assertEquals(0, result.getPoolSize());
    }

    @Test
    public void testCreateOrganisationParentNotFound() {
        var input = "i_dont_exist";
        var inputOrganisation = new OrganisationCreate();
        inputOrganisation.code("code");

        var exception = assertThrows(ApiException.class, () -> organisationApi.servicesOrganisationParentCodePost(input, inputOrganisation));
        assertNotNull(exception);
        assertEquals(400, exception.getCode());
        var error = JSON.getGson().fromJson(exception.getResponseBody(), BasicError.class);
        assertEquals("Parent organisation i_dont_exist not found", error.getError());
    }

    @Test
    public void testReadOrganisationTree_a() throws ApiException {
        var response = organisationTreeApi.servicesOrganisationtreeCodeGet("child");

        assertNotNull(response);
        Assert.assertEquals("super_parent", response.getName());
        Assert.assertEquals(0, response.getPoolSize().intValue());
        Assert.assertEquals("10", response.getCode());
        Assert.assertEquals(1, response.getChildren().size());

        var child = response.getChildren().get(0);
        Assert.assertEquals("parent", child.getCode());
        Assert.assertEquals(20, child.getPoolSize().intValue());
        Assert.assertEquals("parent org", child.getName());
        Assert.assertEquals(1, child.getChildren().size());

        child = child.getChildren().get(0);
        Assert.assertEquals("child_one", child.getName());
        Assert.assertEquals(0, child.getPoolSize().intValue());
        Assert.assertEquals("12", child.getCode());
        Assert.assertEquals(1, child.getChildren().size());

        child = child.getChildren().get(0);
        Assert.assertEquals("child", child.getCode());
        Assert.assertEquals(0, child.getPoolSize().intValue());
        Assert.assertEquals("child org", child.getName());
        Assert.assertEquals(0, child.getChildren().size());
    }
}

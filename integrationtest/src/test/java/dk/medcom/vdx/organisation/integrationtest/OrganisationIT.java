package dk.medcom.vdx.organisation.integrationtest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.OrganisationApi;
import org.openapitools.client.api.OrganisationTreeApi;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
                .setOffsetDateTimeFormat(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss X"))
                .addDefaultHeader("X-SESSIONDATA", SESSION_MEDCOM_ORGANISATION);
        organisationApi = new OrganisationApi(apiClient);

        organisationTreeApi = new OrganisationTreeApi(apiClient);

        var unauthorizedApiClient = new ApiClient()
                .setBasePath(getApiBasePath())
                .setOffsetDateTimeFormat(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss X"))
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
    @Ignore // missing view view_entities_meetingroom
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
        var response = organisationApi.servicesOrganisationCodeGet("test-org", false);

        assertNotNull(response);
        assertEquals("test-org", response.getCode());
        assertEquals("company name test-org", response.getName());
        assertEquals("MinAfsender", response.getSmsSenderName());
        assertEquals("some_url", response.getSmsCallbackUrl());
    }

    @Test
    public void testReadOrganisationNoSmsSenderName() throws ApiException {
        var response = organisationApi.servicesOrganisationCodeGet("kvak", false);

        assertNotNull(response);
        assertEquals("kvak", response.getCode());
        assertEquals("company name kvak", response.getName());
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
    public void testUnauthorizedWhenUnknownRole() {
        var expectedException = assertThrows(ApiException.class, () -> unauthorizedOrganisationApi.servicesOrganisationCodeGet("some code", false));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void testUnauthorizedWhenNotAdmin() {
        var unauthorizedApiClient = new ApiClient()
                .setBasePath(getApiBasePath())
                .setOffsetDateTimeFormat(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss X"))
                .addDefaultHeader("X-SESSIONDATA", SESSION_MEETING_USER);

        var api = new OrganisationApi(unauthorizedApiClient);

        var expectedException = assertThrows(ApiException.class, () -> api.servicesOrganisationCodeGet("some code", false));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void testGetOrCreateFromTemplateFound() throws ApiException {
        var input = "test-org";

        var result = organisationApi.servicesOrganisationCodeGet(input, true);
        assertNotNull(result);

        assertEquals(input, result.getCode());
        assertEquals("company name test-org", result.getName());
        assertEquals("MinAfsender", result.getSmsSenderName());
        assertEquals("some_url", result.getSmsCallbackUrl());
        assertEquals(0, result.getPoolSize());
    }

    @Test
    public void testGetOrCreateFromTemplateFoundInTemplate() throws ApiException {
        var input = "from_template";

        var result = organisationApi.servicesOrganisationCodeGet(input, true);
        assertNotNull(result);

        assertEquals(input, result.getCode());
        assertEquals("template name", result.getName());
        assertEquals(null, result.getSmsSenderName());
        assertEquals(null, result.getSmsCallbackUrl());
        assertEquals(0, result.getPoolSize());
    }

    @Test
    public void testGetOrCreateFromTemplateNotFound() throws ApiException {
        var input = "i_dont_exist";

        var exception = assertThrows(ApiException.class, () -> organisationApi.servicesOrganisationCodeGet(input, true));
        assertNotNull(exception);
        assertEquals(404, exception.getCode());
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

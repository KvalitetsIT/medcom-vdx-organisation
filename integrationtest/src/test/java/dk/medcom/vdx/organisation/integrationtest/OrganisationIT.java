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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrganisationIT extends AbstractIntegrationTest {
    private OrganisationApi organisationApi;
    private OrganisationTreeApi organisationTreeApi;

    private static final String SESSION_MEDCOM_ORGANISATION = "ewogICAiVXNlckF0dHJpYnV0ZXMiOnsKICAgICAgImRrOm1lZGNvbTp2aWRlbzpyb2xlIjpbCiAgICAgICAgICJtZWV0aW5nLWFkbWluIgogICAgICBdLAogICAgICAiZGs6bWVkY29tOm9yZ2FuaXNhdGlvbl9pZCI6WwogICAgICAgICAibWVkY29tIgogICAgICBdLAogICAgICAiZGs6bWVkY29tOmVtYWlsIjpbCiAgICAgICAgICJlbWFpbEBkb21haW4uY29tIgogICAgICBdCiAgIH0KfQo=";

    @Before
    public void setupApiClient() {
        var apiClient = new ApiClient()
                .setBasePath(getApiBasePath())
                .setOffsetDateTimeFormat(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss X"))
                .addDefaultHeader("X-SESSIONDATA", SESSION_MEDCOM_ORGANISATION);
        organisationApi = new OrganisationApi(apiClient);
        organisationTreeApi = new OrganisationTreeApi(apiClient);
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
}

package dk.medcom.vdx.organisation.integrationtest.v2;

import dk.medcom.vdx.organisation.integrationtest.AbstractIntegrationTest;
import dk.medcom.vdx.organisation.integrationtest.v2.helper.HeaderBuilder;
import org.junit.Before;
import org.junit.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.OrganisationTreeV2Api;
import org.openapitools.client.model.OrganisationTreeForApiKey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class OrganisationTreeV2IT extends AbstractIntegrationTest {
    private final String childOrg = "child";

    private OrganisationTreeV2Api organisationTreeV2ApiNoHeader;
    private OrganisationTreeV2Api organisationTreeV2ApiInvalidJwt;
    private OrganisationTreeV2Api organisationTreeV2ApiNoRoleAtt;
    private OrganisationTreeV2Api organisationTreeV2ApiNotAdmin;

    @Before
    public void setup() {
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
    
    private OrganisationTreeForApiKey historyApiKey() {
        return new OrganisationTreeForApiKey()
                .apiKey("8adeac18-f061-4992-818b-8d4461ccfaa7")
                .apiKeyType("history");
    }
}

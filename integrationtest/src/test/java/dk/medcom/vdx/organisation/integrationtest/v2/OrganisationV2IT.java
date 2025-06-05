package dk.medcom.vdx.organisation.integrationtest.v2;

import dk.medcom.vdx.organisation.integrationtest.AbstractIntegrationTest;
import dk.medcom.vdx.organisation.integrationtest.v2.helper.HeaderBuilder;
import org.junit.Before;
import org.junit.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.OrganisationV2Api;
import org.openapitools.client.model.OrganisationCreate;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class OrganisationV2IT extends AbstractIntegrationTest {
    private final String testOrg = "test-org";
    private final String company1 = "company 1";
    
    private OrganisationV2Api organisationV2ApiNoHeader;
    private OrganisationV2Api organisationV2ApiInvalidJwt;
    private OrganisationV2Api organisationV2ApiNoRoleAtt;
    private OrganisationV2Api organisationV2ApiNotAdmin;
    
    @Before
    public void setup() {
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
    public void errorIfNoJwtToken_servicesV2OrganisationCodeGet() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNoHeader.servicesV2OrganisationCodeGet(testOrg));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfInvalidJwtToken_servicesV2OrganisationCodeGet() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiInvalidJwt.servicesV2OrganisationCodeGet(testOrg));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNoRoleAttInToken_servicesV2OrganisationCodeGet() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNoRoleAtt.servicesV2OrganisationCodeGet(testOrg));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNotAdmin_servicesV2OrganisationCodeGet() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNotAdmin.servicesV2OrganisationCodeGet(testOrg));
        assertEquals(403, expectedException.getCode());
    }

    @Test
    public void errorIfNoJwtToken_servicesV2OrganisationGet() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNoHeader.servicesV2OrganisationGet(testOrg));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfInvalidJwtToken_servicesV2OrganisationGet() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiInvalidJwt.servicesV2OrganisationGet(testOrg));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNoRoleAttInToken_servicesV2OrganisationGet() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNoRoleAtt.servicesV2OrganisationGet(testOrg));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNotAdmin_servicesV2OrganisationGet() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNotAdmin.servicesV2OrganisationGet(testOrg));
        assertEquals(403, expectedException.getCode());
    }

    @Test
    public void errorIfNoJwtToken_servicesV2OrganisationParentCodePost() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNoHeader.servicesV2OrganisationParentCodePost(company1, randomOrganisationCreate()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfInvalidJwtToken_servicesV2OrganisationParentCodePost() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiInvalidJwt.servicesV2OrganisationParentCodePost(company1, randomOrganisationCreate()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNoRoleAttInToken_servicesV2OrganisationParentCodePost() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNoRoleAtt.servicesV2OrganisationParentCodePost(company1, randomOrganisationCreate()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNotAdmin_servicesV2OrganisationParentCodePost() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNotAdmin.servicesV2OrganisationParentCodePost(company1, randomOrganisationCreate()));
        assertEquals(403, expectedException.getCode());
    }

    @Test
    public void errorIfNoJwtToken_servicesV2OrganisationPost() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNoHeader.servicesV2OrganisationPost(company1, randomOrganisationCreate()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfInvalidJwtToken_servicesV2OrganisationPost() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiInvalidJwt.servicesV2OrganisationPost(company1, randomOrganisationCreate()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNoRoleAttInToken_servicesV2OrganisationPost() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNoRoleAtt.servicesV2OrganisationPost(company1, randomOrganisationCreate()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNotAdmin_servicesV2OrganisationPost() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNotAdmin.servicesV2OrganisationPost(company1, randomOrganisationCreate()));
        assertEquals(403, expectedException.getCode());
    }

    @Test
    public void errorIfNoJwtToken_servicesV2OrganisationUriPost() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNoHeader.servicesV2OrganisationUriPost(List.of("1239@test.dk")));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfInvalidJwtToken_servicesV2OrganisationUriPost() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiInvalidJwt.servicesV2OrganisationUriPost(List.of("1239@test.dk")));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNoRoleAttInToken_servicesV2OrganisationUriPost() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNoRoleAtt.servicesV2OrganisationUriPost(List.of("1239@test.dk")));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNotAdmin_servicesV2OrganisationUriPost() {
        var expectedException = assertThrows(ApiException.class, () -> organisationV2ApiNotAdmin.servicesV2OrganisationUriPost(List.of("1239@test.dk")));
        assertEquals(403, expectedException.getCode());
    }
    
    private String randomString() {
        return UUID.randomUUID().toString();
    }
    
    private OrganisationCreate randomOrganisationCreate() {
        return new OrganisationCreate()
                .code(randomString())
                .name(randomString());
    }
}

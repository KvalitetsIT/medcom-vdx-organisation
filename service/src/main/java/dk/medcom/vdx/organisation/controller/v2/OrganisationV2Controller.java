package dk.medcom.vdx.organisation.controller.v2;

import dk.medcom.vdx.organisation.interceptor.Oauth;
import org.openapitools.api.OrganisationV2Api;
import org.openapitools.model.Organisation;
import org.openapitools.model.OrganisationCreate;
import org.openapitools.model.OrganisationUriInner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrganisationV2Controller implements OrganisationV2Api {
    private final static Logger logger = LoggerFactory.getLogger(OrganisationV2Controller.class);
    private final String adminRoleAtt = "hasAuthority('ROLE_ATT_meeting-admin')";

    @Oauth
    @Override
    @PreAuthorize(adminRoleAtt)
    public ResponseEntity<Organisation> servicesV2OrganisationCodeGet(String code) {
        logger.debug("Enter GET organisation from code in path.");
        return null;
    }

    @Oauth
    @Override
    @PreAuthorize(adminRoleAtt)
    public ResponseEntity<Organisation> servicesV2OrganisationGet(String organisationCode) {
        logger.debug("Enter GET organisation from code in query.");
        return null;
    }

    @Oauth
    @Override
    @PreAuthorize(adminRoleAtt)
    public ResponseEntity<Organisation> servicesV2OrganisationParentCodePost(String parentCode, OrganisationCreate organisationCreate) {
        logger.debug("Enter POST organisation where parent code in path.");
        return null;
    }

    @Oauth
    @Override
    @PreAuthorize(adminRoleAtt)
    public ResponseEntity<Organisation> servicesV2OrganisationPost(String parentCode, OrganisationCreate organisationCreate) {
        logger.debug("Enter POST organisation where parent code in query.");
        return null;
    }

    @Oauth
    @Override
    @PreAuthorize(adminRoleAtt)
    public ResponseEntity<List<OrganisationUriInner>> servicesV2OrganisationUriPost(List<String> requestBody) {
        logger.debug("Enter POST organisation uri.");
        return null;
    }
}

package dk.medcom.vdx.organisation.controller.v2;

import dk.medcom.vdx.organisation.interceptor.Oauth;
import org.openapitools.api.OrganisationTreeV2Api;
import org.openapitools.model.OrganisationTreeForApiKey;
import org.openapitools.model.Organisationtree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrganisationTreeV2Controller implements OrganisationTreeV2Api {
    private final static Logger logger = LoggerFactory.getLogger(OrganisationTreeV2Controller.class);
    private final String adminRoleAtt = "hasAuthority('ROLE_ATT_meeting-admin')";

    @Oauth
    @Override
    @PreAuthorize(adminRoleAtt)
    public ResponseEntity<Organisationtree> servicesV2OrganisationTreeForApiKeyPost(OrganisationTreeForApiKey organisationTreeForApiKey) {
        logger.debug("Enter POST organisation tree for api key.");
        return null;
    }

    @Oauth
    @Override
    @PreAuthorize(adminRoleAtt)
    public ResponseEntity<Organisationtree> servicesV2OrganisationtreeChildrenGet(String organisationCode, Integer groupId) {
        logger.debug("Enter GET organisation tree children by query parameter.");
        return null;
    }

    @Oauth
    @Override
    @PreAuthorize(adminRoleAtt)
    public ResponseEntity<Organisationtree> servicesV2OrganisationtreeCodeGet(String code) {
        logger.debug("Enter GET organisation tree from code.");
        return null;
    }

    @Oauth
    @Override
    @PreAuthorize(adminRoleAtt)
    public ResponseEntity<Organisationtree> servicesV2OrganisationtreeGet(String organisationCode, Integer groupId) {
        logger.debug("Enter GET organisation tree by query parameter.");
        return null;
    }
}

package dk.medcom.vdx.organisation.controller.v2;

import dk.medcom.vdx.organisation.controller.exception.*;
import dk.medcom.vdx.organisation.controller.v2.mapper.OrganisationMapper;
import dk.medcom.vdx.organisation.interceptor.Oauth;
import dk.medcom.vdx.organisation.service.OrganisationTreeBuilder;
import dk.medcom.vdx.organisation.service.OrganisationTreeService;
import jakarta.servlet.http.HttpServletRequest;
import org.openapitools.api.OrganisationTreeV2Api;
import org.openapitools.model.OrganisationSimple;
import org.openapitools.model.OrganisationTreeForApiKey;
import org.openapitools.model.Organisationtree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrganisationTreeV2Controller implements OrganisationTreeV2Api {
    private final static Logger logger = LoggerFactory.getLogger(OrganisationTreeV2Controller.class);
    private final String adminRoleAtt = "hasAuthority('ROLE_ATT_meeting-admin')";

    private final OrganisationTreeService organisationTreeService;
    private final OrganisationTreeBuilder organisationTreeBuilder;

    public OrganisationTreeV2Controller(OrganisationTreeService organisationTreeService, OrganisationTreeBuilder organisationTreeBuilder) {
        this.organisationTreeService = organisationTreeService;
        this.organisationTreeBuilder = organisationTreeBuilder;
    }

    @Oauth
    @PreAuthorize(adminRoleAtt)
    @RequestMapping(method = RequestMethod.GET, value = "/services/v2/organisationtree/**", produces = { "application/json" })
    public ResponseEntity<Organisationtree> getOrganisationTreeSlash(HttpServletRequest request) {
        var organisation = request.getServletPath().replaceFirst("/services/v2/organisationtree/", "");
        logger.debug("Reading organisation tree with slash. Translated to {}.", organisation);

        return servicesV2OrganisationtreeCodeGet(organisation);
    }

    @Oauth
    @Override
    @PreAuthorize(adminRoleAtt)
    public ResponseEntity<List<OrganisationSimple>> servicesV2OrganisationCodeDescendantsGet(String code) {
        logger.debug("Enter GET sub organisation codes of organisation: {}", code);

        var codeList = organisationTreeService.findDescendantsOfOrganisation(code);
        return ResponseEntity.ok(codeList.stream().map(OrganisationMapper::internalToExternal).toList());
    }

    @Oauth
    @Override
    @PreAuthorize(adminRoleAtt)
    public ResponseEntity<Organisationtree> servicesV2OrganisationTreeForApiKeyPost(OrganisationTreeForApiKey organisationTreeForApiKey) {
        logger.debug("Enter POST organisation tree for api key.");

        var organisations = organisationTreeService.findAncestorsByApiKey(organisationTreeForApiKey.getApiKeyType(), organisationTreeForApiKey.getApiKey());

        return ResponseEntity.ok(organisationTreeBuilder.buildOrganisationTreeFromModel(organisations));
    }

    @Oauth
    @Override
    @PreAuthorize(adminRoleAtt)
    public ResponseEntity<Organisationtree> servicesV2OrganisationtreeChildrenGet(String organisationCode, Integer groupId) {
        logger.debug("Enter GET organisation tree children by query parameter, organisationCode: {}, groupId: {}", organisationCode, groupId);
        validateExactlyOneQueryParameterSet(organisationCode, groupId);

        if (organisationCode != null) {
            var organisations = organisationTreeService.findDescendantsByCode(organisationCode);

            return ResponseEntity.ok(organisationTreeBuilder.buildOrganisationTreeFromModel(organisations, organisations.getFirst().group().id()));
        } else {
            var organisations = organisationTreeService.findDescendantsByGroupId(groupId);

            return ResponseEntity.ok(organisationTreeBuilder.buildOrganisationTreeFromModel(organisations, groupId.longValue()));
        }
    }

    @Oauth
    @Override
    @PreAuthorize(adminRoleAtt)
    public ResponseEntity<Organisationtree> servicesV2OrganisationtreeCodeGet(String code) {
        logger.debug("Enter GET organisation tree from code: {}.", code);
        var organisations = organisationTreeService.findAncestorsByCode(code);

        return ResponseEntity.ok(organisationTreeBuilder.buildOrganisationTreeFromModel(organisations));
    }

    @Oauth
    @Override
    @PreAuthorize(adminRoleAtt)
    public ResponseEntity<Organisationtree> servicesV2OrganisationtreeGet(String organisationCode, Integer groupId) {
        logger.debug("Enter GET organisation tree by query parameter, organisationCode: {}, groupId: {}", organisationCode, groupId);
        validateExactlyOneQueryParameterSet(organisationCode, groupId);

        if(organisationCode != null) {
            return servicesV2OrganisationtreeCodeGet(organisationCode);
        }

        var organisations = organisationTreeService.findAncestorsByGroupId(groupId);

        return ResponseEntity.ok(organisationTreeBuilder.buildOrganisationTreeFromModel(organisations));
    }

    private void validateExactlyOneQueryParameterSet(String organisationCode, Integer groupId) {
        if(organisationCode != null && groupId != null) {
            logger.warn("organisationCode and groupId is mutually exclusive. Returning 400.");
            throw new BadRequestException("organisationCode and groupId is mutually exclusive.");
        }

        if(organisationCode == null && groupId == null) {
            logger.warn("Either organisationCode or groupId must be set.");
            throw new BadRequestException("Either organisationCode or groupId must be set.");
        }
    }
}

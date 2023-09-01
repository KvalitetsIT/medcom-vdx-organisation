package dk.medcom.vdx.organisation.controller;

import dk.medcom.vdx.organisation.aspect.APISecurityAnnotation;
import dk.medcom.vdx.organisation.context.UserRole;
import dk.medcom.vdx.organisation.controller.exception.BadRequestException;
import dk.medcom.vdx.organisation.controller.exception.ResourceNotFoundException;
import dk.medcom.vdx.organisation.dao.entity.Organisation;
import dk.medcom.vdx.organisation.service.OrganisationTreeBuilder;
import dk.medcom.vdx.organisation.service.OrganisationTreeService;
import jakarta.servlet.http.HttpServletRequest;
import org.openapitools.api.OrganisationTreeApi;
import org.openapitools.model.OrganisationTreeForApiKey;
import org.openapitools.model.Organisationtree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrganisationTreeController implements OrganisationTreeApi  {
    private final static Logger logger = LoggerFactory.getLogger(OrganisationTreeController.class);
    private final OrganisationTreeService organisationTreeService;
    private final OrganisationTreeBuilder organisationTreeBuilder;

    public OrganisationTreeController(OrganisationTreeService organisationTreeService, OrganisationTreeBuilder organisationTreeBuilder) {
        this.organisationTreeService = organisationTreeService;
        this.organisationTreeBuilder = organisationTreeBuilder;
    }

    @APISecurityAnnotation({UserRole.ADMIN})
    @RequestMapping(method = RequestMethod.GET, value = "/services/organisationtree/**", produces = { "application/json" })
    public ResponseEntity<Organisationtree> getOrganisationTreeSlash(HttpServletRequest request) {
        var organisation = request.getServletPath().replaceFirst("/services/organisationtree/", "");
        logger.info("Reading organisation tree with slash. Translated to {}.", organisation);

        return servicesOrganisationtreeCodeGet(organisation);
    }

    @Override
    @APISecurityAnnotation({UserRole.ADMIN})
    public ResponseEntity<Organisationtree> servicesOrganisationtreeChildrenGet(Integer groupId) {
        logger.debug("Enter servicesOrganisationtreeChildrenGet(groupId: {})", groupId);
        try {
            List<Organisation> organisations = organisationTreeService.findChildrenByGroupId(groupId);

            if(organisations.isEmpty()) {
                throw new ResourceNotFoundException("Organisation tree with group id %s not found.".formatted(groupId));
            }

            return ResponseEntity.ok(organisationTreeBuilder.buildOrganisationTree(organisations));
        } finally {
            logger.debug("Done servicesOrganisationtreeChildrenGet(groupID: {})", groupId);
        }
    }

    @Override
    @APISecurityAnnotation({UserRole.ADMIN})
    public ResponseEntity<Organisationtree> servicesOrganisationtreeCodeGet(String code) {
        logger.debug("Enter getOrganisationTree(code: {})", code);
        try {
            List<Organisation> organisations = organisationTreeService.findOrganisations(code).orElseThrow(() -> new ResourceNotFoundException("The code: "+code+" does not identify an organisation"));
            return ResponseEntity.ok(organisationTreeBuilder.buildOrganisationTree(organisations));
        } finally {
            logger.debug("Done getOrganisationTree(code: {})", code);
        }
    }

    @Override
    @APISecurityAnnotation({ UserRole.ADMIN })
    public ResponseEntity<Organisationtree> servicesOrganisationtreeGet(String organisationCode, Integer groupId) {
        logger.info("Reading organisation tree by query parameter: {}.", organisationCode);

        if(organisationCode != null && groupId != null) {
            logger.info("organisationCode and groupId is mutually exclusive. Returning 400.");
            throw new BadRequestException("organisationCode and groupId is mutually exclusive.");
        }

        if(organisationCode == null && groupId == null) {
            logger.info("Either organisationCode or groupId must be set.");
            throw new BadRequestException("Either organisationCode or groupId must be set.");
        }

        if(organisationCode != null) {
            return servicesOrganisationtreeCodeGet(organisationCode);
        }

        var organisations = organisationTreeService.getByGroupId(groupId).orElseThrow(() -> new ResourceNotFoundException("The group Id %s does not exist.".formatted(groupId)));

        return ResponseEntity.ok(organisationTreeBuilder.buildOrganisationTree(organisations));
    }

    @Override
    @APISecurityAnnotation({ UserRole.ADMIN })
    public ResponseEntity<Organisationtree> servicesV1OrganisationTreeForApiKeyPost(OrganisationTreeForApiKey organisationTreeForApiKey) {
        logger.info("Reading organisation tree for api key.");

        var organisations = organisationTreeService.findOrganisations(organisationTreeForApiKey.getApiKeyType(), organisationTreeForApiKey.getApiKey()).orElseThrow(() -> new ResourceNotFoundException("Request does not identify an organisation."));;

        return ResponseEntity.ok(organisationTreeBuilder.buildOrganisationTree(organisations));
    }
}

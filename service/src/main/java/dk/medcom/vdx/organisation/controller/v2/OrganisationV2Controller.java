package dk.medcom.vdx.organisation.controller.v2;

import dk.medcom.vdx.organisation.controller.exception.BadRequestException;
import dk.medcom.vdx.organisation.controller.exception.ResourceNotFoundV2Exception;
import dk.medcom.vdx.organisation.controller.v2.mapper.OrganisationMapper;
import dk.medcom.vdx.organisation.interceptor.Oauth;
import dk.medcom.vdx.organisation.service.OrganisationByUriService;
import dk.medcom.vdx.organisation.service.OrganisationService;
import dk.medcom.vdx.organisation.service.exception.InvalidDataException;
import jakarta.servlet.http.HttpServletRequest;
import org.openapitools.api.OrganisationV2Api;
import org.openapitools.model.Organisation;
import org.openapitools.model.OrganisationCreate;
import org.openapitools.model.OrganisationUriInner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrganisationV2Controller implements OrganisationV2Api {
    private final static Logger logger = LoggerFactory.getLogger(OrganisationV2Controller.class);
    private final String adminRoleAtt = "hasAuthority('ROLE_ATT_meeting-admin')";

    private final OrganisationService organisationService;
    private final OrganisationByUriService organisationByUriService;

    public OrganisationV2Controller(OrganisationService organisationService, OrganisationByUriService organisationByUriService) {
        this.organisationService = organisationService;
        this.organisationByUriService = organisationByUriService;
    }

    @Oauth
    @PreAuthorize(adminRoleAtt)
    @RequestMapping(method = RequestMethod.GET, value = "/services/v2/organisation/**",
            produces = { "application/json" }
    )
    public ResponseEntity<Organisation> getOrganisationSlash(HttpServletRequest request){
        var organisation = request.getServletPath().replaceFirst("/services/v2/organisation/", "");
        logger.debug("Reading organisation with slash. Translated to {}.", organisation);

        return servicesV2OrganisationCodeGet(organisation);
    }

    @Oauth
    @Override
    @PreAuthorize(adminRoleAtt)
    public ResponseEntity<Organisation> servicesV2OrganisationCodeGet(String code) {
        logger.debug("Enter GET organisation from code in path.");
        var organisation = organisationService.getOrganisationById(code).orElseThrow(() -> new ResourceNotFoundV2Exception("Organisation with id: %s not found".formatted(code)));

        return ResponseEntity.ok(OrganisationMapper.internalToExternal(organisation));
    }

    @Oauth
    @Override
    @PreAuthorize(adminRoleAtt)
    public ResponseEntity<Organisation> servicesV2OrganisationGet(String organisationCode) {
        logger.debug("Enter GET organisation from code in query.");
        return servicesV2OrganisationCodeGet(organisationCode);
    }

    @Oauth
    @Override
    @PreAuthorize(adminRoleAtt)
    public ResponseEntity<Organisation> servicesV2OrganisationParentCodePost(String parentCode, OrganisationCreate organisationCreate) {
        logger.debug("Enter POST organisation where parent code in path.");
        try {
            var organisation = organisationService.createOrganisation(OrganisationMapper.externalToInternal(organisationCreate, parentCode));
            return ResponseEntity.ok(OrganisationMapper.internalToExternal(organisation));
        }
        catch(InvalidDataException e) {
            logger.warn("Error during creation of organisation", e);
            throw new BadRequestException(e.getMessage());
        }
    }

    @Oauth
    @Override
    @PreAuthorize(adminRoleAtt)
    public ResponseEntity<Organisation> servicesV2OrganisationPost(String parentCode, OrganisationCreate organisationCreate) {
        logger.debug("Enter POST organisation where parent code in query.");
        return servicesV2OrganisationParentCodePost(parentCode, organisationCreate);
    }

    @Oauth
    @Override
    @PreAuthorize(adminRoleAtt)
    public ResponseEntity<List<OrganisationUriInner>> servicesV2OrganisationUriPost(List<String> requestBody) {
        logger.debug("Enter POST organisation uri.");

        var resource = organisationByUriService.getOrganisationByUriWithDomain(requestBody);
        logger.debug("Exit of POST organisation uri, return count: {}", resource.size());
        return ResponseEntity.ok(resource.stream().toList());
    }
}

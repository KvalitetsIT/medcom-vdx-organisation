package dk.medcom.vdx.organisation.controller;

import dk.medcom.vdx.organisation.aspect.APISecurityAnnotation;
import dk.medcom.vdx.organisation.context.UserRole;
import dk.medcom.vdx.organisation.controller.exception.BadRequestException;
import dk.medcom.vdx.organisation.controller.exception.ResourceNotFoundException;
import dk.medcom.vdx.organisation.service.OrganisationByUriService;
import dk.medcom.vdx.organisation.service.OrganisationService;
import dk.medcom.vdx.organisation.service.exception.InvalidDataException;
import org.openapitools.api.OrganisationApi;
import org.openapitools.model.Organisation;
import org.openapitools.model.OrganisationCreate;
import org.openapitools.model.OrganisationUriInner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
public class OrganisationController implements OrganisationApi {
    private static final Logger logger = LoggerFactory.getLogger(OrganisationController.class);
    private final OrganisationService organisationService;
    private final OrganisationByUriService organisationByUriService;

    public OrganisationController(OrganisationService organisationService, OrganisationByUriService organisationByUriService) {
        this.organisationService = organisationService;
        this.organisationByUriService = organisationByUriService;
    }

    @Override
    @APISecurityAnnotation({ UserRole.ADMIN })
    public ResponseEntity<Organisation> servicesOrganisationCodeGet(String code) {
        logger.debug("Entry of /services/organisation.get code: " + code);

        var optionalOrganisation = organisationService.getOrganisationById(code);

        var organisation = optionalOrganisation.orElseThrow(() -> new ResourceNotFoundException("OrganisationId", code));

        var response = new Organisation();
        response.setName(organisation.getName());
        response.setCode(organisation.getOrganisationId());
        response.setSmsSenderName(organisation.getSmsSenderName());
        response.setSmsCallbackUrl(organisation.getSmsCallbackUrl());
        int poolSize = organisation.getPoolSize() == null ? 0 : organisation.getPoolSize();
        response.setPoolSize(poolSize);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Organisation> servicesOrganisationParentCodePost(String parentCode, OrganisationCreate organisationCreate) {
        logger.debug("Entry of /services/organisation.post code: " + organisationCreate.getCode());

        try {
            var organisation = organisationService.createOrganisation(new dk.medcom.vdx.organisation.service.model.OrganisationCreate(organisationCreate.getCode(), organisationCreate.getName(), parentCode));
            return ResponseEntity.ok(new Organisation()
                    .code(organisation.getOrganisationId())
                    .name(organisation.getName())
                    .poolSize(organisation.getPoolSize() == null ? 0 : organisation.getPoolSize())
                    .smsCallbackUrl(organisation.getSmsCallbackUrl())
                    .smsSenderName(organisation.getSmsSenderName()));
        }
        catch(InvalidDataException e) {
            logger.info("Error during creation of organisation", e);
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    @APISecurityAnnotation({ UserRole.ADMIN })
    public ResponseEntity<List<OrganisationUriInner>> servicesOrganisationUriPost(List<String> requestBody) {
        logger.debug("Entry of /services/organisation/uri.post count: " + requestBody.size());

        Set<OrganisationUriInner> resource = organisationByUriService.getOrganisationByUriWithDomain(requestBody);
        logger.debug("Exit of /services/organisation/uri.post return count: " + resource.size());
        return ResponseEntity.ok(resource.stream().toList());
    }
}

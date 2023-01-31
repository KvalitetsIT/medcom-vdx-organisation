package dk.medcom.vdx.organisation.controller;

import dk.medcom.vdx.organisation.aspect.APISecurityAnnotation;
import dk.medcom.vdx.organisation.context.UserRole;
import dk.medcom.vdx.organisation.controller.exception.ResourceNotFoundException;
import dk.medcom.vdx.organisation.service.OrganisationByUriService;
import dk.medcom.vdx.organisation.service.OrganisationService;
import org.openapitools.api.OrganisationApi;
import org.openapitools.model.Organisation;
import org.openapitools.model.OrganisationUriInner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
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
    public ResponseEntity<Organisation> servicesOrganisationCodeGet(String code, Boolean fromTemplate) {
        logger.debug("Entry of /services/organisation.get code: " + code);

        if(fromTemplate == null) {
            fromTemplate = false;
        }

        Optional<dk.medcom.vdx.organisation.dao.jpa.entity.Organisation> optionalOrganisation;
        if(fromTemplate) {
            optionalOrganisation = organisationService.getOrganisationOrCreateFromTemplate(code);
        }
        else {
            optionalOrganisation = organisationService.getOrganisationById(code);
        }

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
    @APISecurityAnnotation({ UserRole.ADMIN })
    public ResponseEntity<List<OrganisationUriInner>> servicesOrganisationUriPost(List<String> requestBody) {
        logger.debug("Entry of /services/organisation/uri.post count: " + requestBody.size());

        Set<OrganisationUriInner> resource = organisationByUriService.getOrganisationByUriWithDomain(requestBody);
        logger.debug("Exit of /services/organisation/uri.post return count: " + resource.size());
        return ResponseEntity.ok(resource.stream().toList());
    }
}

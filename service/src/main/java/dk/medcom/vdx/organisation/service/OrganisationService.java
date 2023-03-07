package dk.medcom.vdx.organisation.service;

import dk.medcom.vdx.organisation.dao.jpa.entity.Organisation;
import dk.medcom.vdx.organisation.service.model.OrganisationCreate;

import java.util.Optional;

public interface OrganisationService {
    Optional<Organisation> getOrganisationById(String organisationId);

    Organisation createOrganisation(OrganisationCreate code);
}

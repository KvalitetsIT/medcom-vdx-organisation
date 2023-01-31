package dk.medcom.vdx.organisation.service;

import dk.medcom.vdx.organisation.dao.jpa.entity.Organisation;

import java.util.Optional;

public interface OrganisationService {
    Optional<Organisation> getOrganisationById(String organisationId);

    Optional<Organisation> getOrganisationOrCreateFromTemplate(String code);
}

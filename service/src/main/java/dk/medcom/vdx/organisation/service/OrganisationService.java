package dk.medcom.vdx.organisation.service;

import dk.medcom.vdx.organisation.dao.entity.Organisation;
import dk.medcom.vdx.organisation.service.model.OrganisationCreateBasic;
import dk.medcom.vdx.organisation.service.model.OrganisationCreate;
import dk.medcom.vdx.organisation.service.model.OrganisationUpdate;

import java.util.Optional;

public interface OrganisationService {
    Optional<Organisation> getOrganisationById(String organisationId);

    Organisation createOrganisationBasic(OrganisationCreateBasic code);

    Organisation createOrganisation(OrganisationCreate organisationCreate);

    Organisation updateOrganisation(String organisationCode, OrganisationUpdate organisationUpdate);
}

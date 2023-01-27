package dk.medcom.vdx.organisation.service;


import dk.medcom.vdx.organisation.dao.entity.Organisation;

import java.util.List;
import java.util.Optional;

public interface OrganisationTreeService {
    Optional<List<Organisation>> findOrganisations(String code);
}

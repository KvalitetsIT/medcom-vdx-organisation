package dk.medcom.vdx.organisation.service;

import dk.medcom.vdx.organisation.dao.entity.Organisation;
import org.openapitools.model.Organisationtree;

import java.util.List;

public interface OrganisationTreeBuilder {
    Organisationtree buildOrganisationTree(List<Organisation> organisationList);

    Organisationtree buildOrganisationTree(List<Organisation> organisationList, Long root);
}

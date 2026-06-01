package dk.medcom.vdx.organisation.service;

import dk.medcom.vdx.organisation.dao.entity.Organisation;
import dk.medcom.vdx.organisation.service.model.OrganisationModel;
import org.openapitools.model.Organisationtree;

import java.util.List;

public interface OrganisationTreeBuilder {
    Organisationtree buildOrganisationTree(List<Organisation> organisationList);
    Organisationtree buildOrganisationTreeFromModel(List<OrganisationModel> organisationList);

    Organisationtree buildOrganisationTree(List<Organisation> organisationList, Long root);
    Organisationtree buildOrganisationTreeFromModel(List<OrganisationModel> organisationList, Long root);
}

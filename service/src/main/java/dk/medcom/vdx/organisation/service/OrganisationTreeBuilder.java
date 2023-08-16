package dk.medcom.vdx.organisation.service;

import dk.medcom.vdx.organisation.dao.entity.Organisation;
import org.openapitools.model.OrganisationTreeForApiKeyResponse;
import org.openapitools.model.Organisationtree;

import java.util.List;

public interface OrganisationTreeBuilder {
    Organisationtree buildOrganisationTree(List<Organisation> organisationList);
    OrganisationTreeForApiKeyResponse buildOrganisationTreeForApiKey(List<Organisation> organisationList);
}

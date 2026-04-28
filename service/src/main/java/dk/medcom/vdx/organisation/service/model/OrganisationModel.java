package dk.medcom.vdx.organisation.service.model;

import dk.medcom.vdx.organisation.dao.entity.OrganisationGroupJoin;

public record OrganisationModel(Long groupId,
                                Long parentId,
                                Integer poolSize,
                                String groupName,
                                String organisationId,
                                String organisationName,
                                String smsSenderName,
                                Boolean allowCustomUriWithoutDomain,
                                String smsCallbackUrl,
                                String historyApiKey,
                                String deviceWebhookEndpoint,
                                String deviceWebhookEndpointKey) {
    public static OrganisationModel from(OrganisationGroupJoin organisationGroupJoin) {
        return new OrganisationModel(organisationGroupJoin.groupId(),
                organisationGroupJoin.parentId(),
                organisationGroupJoin.poolSize(),
                organisationGroupJoin.groupName(),
                organisationGroupJoin.organisationId(),
                organisationGroupJoin.organisationName(),
                organisationGroupJoin.smsSenderName(),
                organisationGroupJoin.allowCustomUriWithoutDomain(),
                organisationGroupJoin.smsCallbackUrl(),
                organisationGroupJoin.historyApiKey(),
                organisationGroupJoin.deviceWebhookEndpoint(),
                organisationGroupJoin.deviceWebhookEndpointKey());
    }
}

package dk.medcom.vdx.organisation.service.model;

import dk.medcom.vdx.organisation.dao.entity.OrganisationGroupJoin;

public record OrganisationModel(String id,
                                String name,
                                Integer poolSize,
                                Boolean allowCustomUriWithoutDomain,
                                Group group,
                                SmsInfo smsInfo,
                                String historyApiKey,
                                DeviceWebhook deviceWebhook) {
    public static OrganisationModel from(OrganisationGroupJoin organisationGroupJoin) {
        return new OrganisationModel(organisationGroupJoin.organisationId(),
                organisationGroupJoin.organisationName(),
                organisationGroupJoin.poolSize(),
                organisationGroupJoin.allowCustomUriWithoutDomain(),
                new Group(organisationGroupJoin.groupId(), organisationGroupJoin.parentId(), organisationGroupJoin.groupName()),
                new SmsInfo(organisationGroupJoin.smsSenderName(), organisationGroupJoin.smsCallbackUrl()),
                organisationGroupJoin.historyApiKey(),
                new DeviceWebhook(organisationGroupJoin.deviceWebhookEndpoint(), organisationGroupJoin.deviceWebhookEndpointKey())
        );
    }
}

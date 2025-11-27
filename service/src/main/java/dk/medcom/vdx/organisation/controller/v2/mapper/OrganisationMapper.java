package dk.medcom.vdx.organisation.controller.v2.mapper;

import dk.medcom.vdx.organisation.dao.entity.Organisation;
import dk.medcom.vdx.organisation.service.model.OrganisationCreate;
import dk.medcom.vdx.organisation.service.model.OrganisationUpdate;
import org.openapitools.model.OrganisationResponse;

public class OrganisationMapper {

    public static OrganisationResponse internalToExternal(Organisation input) {
        return new OrganisationResponse()
                .groupId(input.getGroupId().intValue())
                .code(input.getOrganisationId())
                .name(input.getOrganisationName())
                .poolSize(input.getPoolSize() == null ? 0 : input.getPoolSize())
                .smsSenderName(input.getSmsSenderName())
                .allowCustomUriWithoutDomain(input.isAllowCustomUriWithoutDomain())
                .smsCallbackUrl(input.getSmsCallbackUrl())
                .historyApiKey(input.getHistoryApiKey())
                .deviceWebhookEndpoint(input.getDeviceWebhookEndpoint())
                .deviceWebhookEndpointKey(input.getDeviceWebhookEndpointKey());
    }

    public static OrganisationCreate externalToInternal(org.openapitools.model.OrganisationCreate input, String parentCode) {
        return new OrganisationCreate(
                parentCode,
                input.getCode(),
                input.getName(),
                input.getPoolSize(),
                input.getSmsSenderName(),
                input.getAllowCustomUriWithoutDomain(),
                input.getSmsCallbackUrl(),
                input.getHistoryApiKey(),
                input.getDeviceWebhookEndpoint(),
                input.getDeviceWebhookEndpointKey());
    }

    public static OrganisationUpdate externalToInternal(org.openapitools.model.OrganisationUpdate input) {
        return new OrganisationUpdate(
                input.getPoolSize(),
                input.getSmsSenderName(),
                input.getAllowCustomUriWithoutDomain(),
                input.getSmsCallbackUrl(),
                input.getHistoryApiKey(),
                input.getDeviceWebhookEndpoint(),
                input.getDeviceWebhookEndpointKey());
    }
}

package dk.medcom.vdx.organisation.service.model;

public record OrganisationUpdate(Integer poolSize,
                                 String smsSenderName,
                                 boolean allowCustomUriWithoutDomain,
                                 String smsCallbackUrl,
                                 String historyApiKey,
                                 String deviceWebhookEndpoint,
                                 String deviceWebhookEndpointKey) {
}

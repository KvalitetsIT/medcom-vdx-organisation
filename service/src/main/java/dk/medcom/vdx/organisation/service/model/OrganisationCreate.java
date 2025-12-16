package dk.medcom.vdx.organisation.service.model;

public record OrganisationCreate(String parentOrganisation,
                                 String organisationCode,
                                 String name,
                                 Integer poolSize,
                                 String smsSenderName,
                                 boolean allowCustomUriWithoutDomain,
                                 String smsCallbackUrl,
                                 String historyApiKey,
                                 String deviceWebhookEndpoint,
                                 String deviceWebhookEndpointKey) {
}

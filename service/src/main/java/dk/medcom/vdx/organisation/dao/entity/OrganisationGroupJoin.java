package dk.medcom.vdx.organisation.dao.entity;

public record OrganisationGroupJoin(Long groupId,
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
}

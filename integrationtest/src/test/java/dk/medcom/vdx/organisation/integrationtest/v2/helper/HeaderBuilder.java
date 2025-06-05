package dk.medcom.vdx.organisation.integrationtest.v2.helper;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class HeaderBuilder {

    public static String getJwtAllRoleAtt(String keycloakUrl) {
        return getJwt(keycloakUrl, "all-role-att", "all-role-att-pass");
    }

    public static String getJwtNoRoleAtt(String keycloakUrl) {
        return getJwt(keycloakUrl, "no-role-att", "no-role-att-pass");
    }

    public static String getJwtNotAdmin(String keycloakUrl) {
        return getJwt(keycloakUrl, "not-admin", "not-admin-pass");
    }

    public static String getInvalidJwt() {
        //JWT from previous run.
        return "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ3ekZ2UWd5OFFha0xBRS1lbnQ4dWdTVEJtdkNDZHN6cFU5NHVsU1pfNG1JIn0.eyJleHAiOjE3NDkxMzA4OTksImlhdCI6MTc0OTEzMDU5OSwianRpIjoiOGQwNzExODgtM2Y5Yi00NjI3LTk5YjgtN2QzOGUwMDUxNGI3IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDozMjgyNC9yZWFsbXMva2V5Y2xvYWt0ZXN0Iiwic3ViIjoiNDIyNWY1N2YtMDI2ZC00MzJhLWFlODAtZGNkMzY3M2Y4YTk2IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYWxsLXJvbGUtYXR0Iiwic2NvcGUiOiIiLCJjbGllbnRIb3N0IjoiMTcyLjE4LjAuMSIsImRrOm1lZGNvbTpvcmdhbmlzYXRpb25faWQiOiJtZWRjb20iLCJkazptZWRjb206dmlkZW86cm9sZSI6WyJtZWV0aW5nLXVzZXIiLCJtZWV0aW5nLWFkbWluIiwibWVldGluZy1wcm92aXNpb25lciIsIm1lZXRpbmctcHJvdmlzaW9uZXItdXNlciIsIm1lZXRpbmctcGxhbm5lciJdLCJkazptZWRjb206ZW1haWwiOiJlbWFpbEBkb21haW4uY29tIiwiY2xpZW50QWRkcmVzcyI6IjE3Mi4xOC4wLjEiLCJjbGllbnRfaWQiOiJhbGwtcm9sZS1hdHQifQ.aWEoLZF-D2PK1JGEQAIpn18GFuGVDv5B_lcqIjMKkXo0TRb7AvdBjpgsrvMnkZIRbEiQlmjsTMMQrU2zQH-TTTOtuI-15W8RATDtP1gsG1nEP1SpwETjU_oprARVkegd36nOcK8H4QuaBHzWoCoteLXETkHDEzkvn6iSwGRqFhSrYMiUwFXUHT4EIuoPvy3nK7WP587sXgGKJogrAdM1xVOM8CrYLqbxQTGy7RfRmpTMcdS40sNOs7tN2wmkM8QL2cwB8W2dLBtzS3ebTrjFIXtJKqqEg4mQpo0DYU6_j9kvSTvbLE5onHcZ40iyQxbPTLmS54yrDzf4QgwzL4cccg";
    }

    private static String getJwt(String keycloakUrl, String clientId, String clientSecret) {
        var restTemplate = new RestTemplate();
        var httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        var map = new LinkedMultiValueMap<>();
        map.put("grant_type", List.of("client_credentials"));
        map.put("client_id", List.of(clientId));
        map.put("client_secret", List.of(clientSecret));

        var request = new HttpEntity<>(map, httpHeaders);

        var token = restTemplate.postForObject(
                keycloakUrl + "/protocol/openid-connect/token",
                request,
                KeyCloakToken.class
        );

        assert token != null;
        return token.accessToken();
    }

    record KeyCloakToken(@JsonProperty("access_token") String accessToken) {
    }
}

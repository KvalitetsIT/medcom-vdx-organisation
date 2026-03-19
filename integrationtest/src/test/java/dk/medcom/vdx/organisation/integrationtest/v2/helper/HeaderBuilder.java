package dk.medcom.vdx.organisation.integrationtest.v2.helper;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class HeaderBuilder {

    public static String getJwtAllRoleAtt(String keycloakUrl) {
        return buildJwt(keycloakUrl, 100, "medcom", randomString(), List.of("meeting-user","meeting-admin","meeting-provisioner","meeting-provisioner-user","meeting-planner"));
    }

    public static String getJwtNoRoleAtt(String keycloakUrl) {
        return buildJwt(keycloakUrl, 100, "medcom", randomString(), List.of());
    }

    public static String getJwtNotAdmin(String keycloakUrl) {
        return buildJwt(keycloakUrl, 100, "medcom", randomString(), List.of("meeting-user","meeting-provisioner","meeting-provisioner-user","meeting-planner"));
    }

    public static String getExpiredJwt(String keycloakUrl) {
        return buildJwt(keycloakUrl, 0, "medcom", randomString(), List.of("meeting-user","meeting-admin","meeting-provisioner","meeting-provisioner-user","meeting-planner"));
    }

    public static String getInvalidIssuerJwt() {
        return buildJwt(randomString(), 100, "medcom", randomString(), List.of("meeting-user","meeting-admin","meeting-provisioner","meeting-provisioner-user","meeting-planner"));
    }

    public static String getTamperedJwt(String keycloakUrl) {
        var validJwt = buildJwt(keycloakUrl, 100, "medcom", randomString(), List.of("meeting-user"));
        var splitToken = Arrays.stream(validJwt.split("\\.")).toList();
        var payload = new String(Base64.getDecoder().decode(splitToken.get(1)));
        var newPayload = payload.replace("meeting-user", "meeting-admin");
        var encodedPayload = Base64.getEncoder().withoutPadding().encodeToString(newPayload.getBytes());

        return splitToken.getFirst() + "." + encodedPayload + "." + splitToken.getLast();
    }

    public static String getMissingSignatureJwt(String keycloakUrl) {
        var validJwt = buildJwt(keycloakUrl, 100, "medcom", randomString(), List.of("meeting-user"));
        var splitToken = Arrays.stream(validJwt.split("\\.")).toList();

        return splitToken.getFirst() + "." + splitToken.get(1);
    }

    public static String getDifferentSignedJwt(String keycloakUrl) {
        var privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCvI3OaaUKC5BSrDc284uysNRslcb6pGbD0UQ8wzy/vOzKV3TB/nDNDIBUxhh6cyFBJ+qTKA485l39b6Ju48vabzyMNC9kjmYzOQ4Azo1g+SOHF5niGZf1znd7qBgQE/FY0ps9AJSxELn9v7Bhf0elUYSanb/pCrJrraYaaZyLW7Fn1z/WTvbJc5tAKg31dr2+wES5OCYVQeD4qMTcLmbJb1mpveuzTVrzuKZG+S5BaC5gUrZuRdRdLuMnRhOCGLT7agOVob2kLfrU+UjZqPkAEYjWxprEPCdJY/gJeNQg9zWHnoJw+rjhAt0JCgibfUfsxw5KrQbmTVmhSnIhenedZAgMBAAECggEAAvQ+Cy+gKafctGI+zeM6+uWeFVnaOC0g17d1TOK1wYXWAtfwrTpZKquZcpTQoomGFyqv3wtufe9w/Gp2Pp6fK74eQ9D8bYtgaAs/QpUMVD1I95xSp0ZZnyVddkTkikv89+riCd2GpgjDFAQfXiBi3I498hWOBalhJzwyrcXfEnvHYlMlGaFKQUSS3EJKRaciFjcZLG3yx7pPE/mVsTEpWUEl/YgGdv0zP9WdfFO7ZPDRhDD0QYwOgj8I/ig8qkRUJQ+oitMbmHg7B2sENDknjymtAZR1NC0CRtQa9VFkDvpoVkogaTavTG0LXA0DFNB88ypvraMXhwmQYkKR66nRYQKBgQDxtwhtsJFLJwsFF5GcooAQbph/sN/83i1fgEH45lvzUBvGnLw7hn6u6gs9vhmuM7gOJTe/UUiZ9EJPc2MZvZ292aA0inFwyym1BnXPiisu395gWTzNTHczmHYAsVGFLQKX+M+X9ZH79tFkhy6bp4XJayhA/n09kd2XnYnsyAiqSQKBgQC5fSqDU7sWWpXhuM0MYRTC5Hwpj5Eem+s7FBRVzLtQmSvjGe9YlGyLEKLgR1GeD9Apa1jO9BU9pJwTJrXNOnbSX7NBsKmr2Slnqh6HvmRdoL7ZUdJdvJ414YB4sK/brYu0cjY7TiAI2cnRDLNytb7bB7pyGW9bfum2e3TzpynUkQKBgCILxN0mZKCwRCYYdsMYRG+MFGDP+gy1ArET8LbtQ4BXBUKJax37SLa8co7kyts1n7QmW97PxSkiNYDZYNkMUz6de6pK4cWgaCR+X3O3I3P7xmfNyfmkzplu3Rgl4bSfbWEYg60EwuY5kq7VN6RnCTogpM/ayKs2c37aisXWOxo5AoGAcAGK6F5pPKhalUDYYL9GoCEEcLxGrysLslL/rIfCHHtBqxMAGocVvCvBjpjCSiXWVwyBRgbRxLVfoZjgcxr4oTQ0tFgPPLwXaoepg+KODJOCawwjcYLyLisLPtXCe26iS8YjxU/5vB525Ys72OqyYuSZy0hcm+1ZB14pUvAALGECgYEAvArplWaHP3XCPjmnBld60ds1Z0VUrOMvvaashYNXxzYxaUEw/cBmm67u0oi9iq4LO2KS5YKlgFIfKCnwx4Fnhg9Dr2CfrZ3U5fizipL9HYBIOzPyCc4ihGYT8WEMHq/oMqrcJ5wFglpQBziMj1y2bJSlvVLAgCte6fvBrNLUDdc=";
        var publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAryNzmmlCguQUqw3NvOLsrDUbJXG+qRmw9FEPMM8v7zsyld0wf5wzQyAVMYYenMhQSfqkygOPOZd/W+ibuPL2m88jDQvZI5mMzkOAM6NYPkjhxeZ4hmX9c53e6gYEBPxWNKbPQCUsRC5/b+wYX9HpVGEmp2/6Qqya62mGmmci1uxZ9c/1k72yXObQCoN9Xa9vsBEuTgmFUHg+KjE3C5myW9Zqb3rs01a87imRvkuQWguYFK2bkXUXS7jJ0YTghi0+2oDlaG9pC361PlI2aj5ABGI1saaxDwnSWP4CXjUIPc1h56CcPq44QLdCQoIm31H7McOSq0G5k1ZoUpyIXp3nWQIDAQAB";
        var keyId = "HTJ0EG6ruBHh_dVsvRnoZRjdoCvpTkZRXvuo0Qqmv3s";

        JwtTokenGenerator generator;
        try {
            generator = new JwtTokenGenerator(loadPublicKey(publicKey), loadPrivateKey(privateKey), keyId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return generator.createToken(100, keycloakUrl, "medcom", List.of("meeting-admin"), randomString());
    }

    private static String buildJwt(String keycloakUrl, long expiresInSeconds, String organisation, String email, List<String> roles) {
        var privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDYmZj9dQJUwI/kH9YlxbAaVVCREJyZKKxAjElt6MW/2YFKHFAqqAf6xeieZcv+q2NfWNKvjQK5ucQxOmUQBaMcyVuiX1CCYErfZjUEmJ/HM+cAYfzemy7m3c8Iq109w4TI5deqNwV8ju1htlAs23xLcEhbh7JGI4TpP1I1r8tI+OajlLf5dBB/NtTzG3BQnRzDiwobS+LMUI8aPvDS/5rHbWwhQwFT0OqGLsptYrLkLQZnmzA4BK5P+OtY335TIAjhuGsEMIbxAScKpd94kXEGDHJ3iPG6+tW6ZY7+F6Jv+PyuOTnga0b/5uNOBUEGcqZqUBGaOxKBTVQb+W8Z8fwlAgMBAAECggEAOijoLSqFkTdRi8pGqMbP1gpRg1pIClIjCoQA1YvayXyAXGOE3SdYWSxaHWotH/lqSfhiPjQpZrBsb2GEMIyq8zhYLB3W0uNDR4A8vTq0MHuNzAsDqMxXiDNH+8Zz13lQBte8lJ6IkR0ZvZKELb9TvTftnfIuWun/rtfLXdIRzkpUsB/yPiSfs8rPUcST6aF97eLhSXJ6JgSqxWVdwFZVyWS5knpXyvLJDnPQm11KCuBI7LdOINH65aE5GMJyCdx3zOz5qrY5PRmyKOJWOQJa9IYflOPAeWzLNFIFef/A13j7i1+qLvzBVrxuB+uDhI4O4FcT2VhGE6ND/n5rsCXd8wKBgQDjt3ClKSx/8HN2Kd0IC+8VJDfmlR2TAWT/DxxXk4zlPKQIXlVgkezt7gZCriIvRo+rTWnRgk/TD95+HXrW0DTrELsor3JiZyoO7HijF/9mkah9d8sE9p4q3rceFWObsUrWOm9lS6mi++n32buyi8z0cXsLGfvkDP1hfZD/LYxqQwKBgQDzgLDjXZThNdk57cgID/9SCOJkmQQfW1QtTSDDdFstPjrtI1QgN/FMMAsob7cG4CSbaNzM2sHnBIs/JyoLmeVpV2PLxBkssAdFlcHjXPnhrgE4shjP/yqbhfxnd6TRuR5PtDHLHZ7RAt0om2A49eKt7eSf9sGjMxGnQGcysSEddwKBgQCxp3Z5U+MEuVnK8SQg1/ahPs1h1inpe2gI2QgahuGgIiwy3y2qlo4Kne1Cbkn7KgiB83Y/3nNaTYIeT+960ZrHXRk2x5HslWGpnPENBHbb1X1mzWOVLrX86h5gmP1KBQpGkfZAR8RDhFdnZvXZeNngRvxgj9gFdeDkAytw+V8XswKBgHXx37xBl9t+ObnVcw50sTcLFp2jgFuv4RYQRc5mjcNcYS577kOs1TQSEVWExyKNby3XnRuc2k8L5GaykasM4BImQjBwy8DZgqwx3bhDUMX/gpfzrBETh4/NVnTHfw4WnJf+sB+yOTsEKBuDJxhwAVw93n+dBi4GjoZ0q4fiRCgBAoGAJIqibU0+t+JDZD77RTfvPTw/+wcmNYgfowLfIIfptQtGGO59Xg3RdKbX7Vd5tPPt6qHgJMUSbPJAJdKeAbfjOgAsrsaetzcFTnD41lYN8LrjVtoL7z+vRhevuTm2oWtxkXB89b2e6KtvaDrtbL4cX8VxAEwOr9UEF6fRaqta5ys=";
        var publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2JmY/XUCVMCP5B/WJcWwGlVQkRCcmSisQIxJbejFv9mBShxQKqgH+sXonmXL/qtjX1jSr40CubnEMTplEAWjHMlbol9QgmBK32Y1BJifxzPnAGH83psu5t3PCKtdPcOEyOXXqjcFfI7tYbZQLNt8S3BIW4eyRiOE6T9SNa/LSPjmo5S3+XQQfzbU8xtwUJ0cw4sKG0vizFCPGj7w0v+ax21sIUMBU9Dqhi7KbWKy5C0GZ5swOASuT/jrWN9+UyAI4bhrBDCG8QEnCqXfeJFxBgxyd4jxuvrVumWO/heib/j8rjk54GtG/+bjTgVBBnKmalARmjsSgU1UG/lvGfH8JQIDAQAB";
        var keyId = "HTJ0EG6ruBHh_dVsvRnoZRjdoCvpTkZRXvuo0Qqmv3s";

        JwtTokenGenerator generator;
        try {
            generator = new JwtTokenGenerator(loadPublicKey(publicKey), loadPrivateKey(privateKey), keyId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return generator.createToken(expiresInSeconds, keycloakUrl, organisation, roles, email);
    }

    private static RSAPublicKey loadPublicKey(String publicKeyPem) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(publicKeyPem);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(new java.security.spec.X509EncodedKeySpec(decoded));
    }

    private static RSAPrivateKey loadPrivateKey(String privateKeyPem) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(privateKeyPem);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    private static String randomString() {
        return UUID.randomUUID().toString();
    }
}

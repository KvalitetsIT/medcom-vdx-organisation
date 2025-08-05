package dk.medcom.vdx.organisation.service.exception;

public class OrganisationNotFoundException extends RuntimeException {
    public OrganisationNotFoundException(String message) {
        super(message);
    }
}

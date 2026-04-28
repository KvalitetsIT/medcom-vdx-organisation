package dk.medcom.vdx.organisation.service.exception;

public class OrganisationNotInTreeException extends RuntimeException {
    public OrganisationNotInTreeException(String message) {
        super(message);
    }
}

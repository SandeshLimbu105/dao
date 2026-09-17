package org.texas.systembdao.exception;

public class ConsentDeniedException extends RuntimeException {

    private final String reason;   // CONSENT_MISSING, CONSENT_EXPIRED, CONSENT_REVOKED

    public ConsentDeniedException(String reason, String message) {
        super(message);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
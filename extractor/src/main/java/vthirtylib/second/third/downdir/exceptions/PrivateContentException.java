package vthirtylib.second.third.downdir.exceptions;

public class PrivateContentException extends ContentNotAvailableException {
    public PrivateContentException(final String message) {
        super(message);
    }

    public PrivateContentException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
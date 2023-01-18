package vthirtylib.second.third.downdir.exceptions;

public class ContentNotAvailableException extends ParsingException {
    public ContentNotAvailableException(final String message) {
        super(message);
    }

    public ContentNotAvailableException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
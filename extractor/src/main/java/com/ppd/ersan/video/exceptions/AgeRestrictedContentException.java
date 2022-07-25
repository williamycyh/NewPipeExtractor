package com.ppd.ersan.video.exceptions;

public class AgeRestrictedContentException extends ContentNotAvailableException {
    public AgeRestrictedContentException(final String message) {
        super(message);
    }

    public AgeRestrictedContentException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

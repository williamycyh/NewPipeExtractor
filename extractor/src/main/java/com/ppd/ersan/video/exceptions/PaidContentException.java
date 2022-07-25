package com.ppd.ersan.video.exceptions;

public class PaidContentException extends ContentNotAvailableException {
    public PaidContentException(final String message) {
        super(message);
    }

    public PaidContentException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

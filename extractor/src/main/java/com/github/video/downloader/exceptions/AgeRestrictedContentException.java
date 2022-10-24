package com.github.video.downloader.exceptions;

public class AgeRestrictedContentException extends ContentNotAvailableException {
    public AgeRestrictedContentException(final String message) {
        super(message);
    }

    public AgeRestrictedContentException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

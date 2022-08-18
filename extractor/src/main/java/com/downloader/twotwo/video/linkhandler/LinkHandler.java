package com.downloader.twotwo.video.linkhandler;

import com.downloader.twotwo.video.utils.Utils;
import com.downloader.twotwo.video.exceptions.ParsingException;

import java.io.Serializable;

public class LinkHandler implements Serializable {
    protected final String originalUrl;
    protected final String url;
    protected final String id;

    public LinkHandler(final String originalUrl, final String url, final String id) {
        this.originalUrl = originalUrl;
        this.url = url;
        this.id = id;
    }

    public LinkHandler(final LinkHandler handler) {
        this(handler.originalUrl, handler.url, handler.id);
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }

    public String getBaseUrl() throws ParsingException {
        return Utils.getBaseUrl(url);
    }
}
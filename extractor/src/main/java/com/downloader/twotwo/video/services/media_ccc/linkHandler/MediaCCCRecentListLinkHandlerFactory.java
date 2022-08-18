package com.downloader.twotwo.video.services.media_ccc.linkHandler;

import com.downloader.twotwo.video.linkhandler.ListLinkHandlerFactory;

import java.util.List;
import java.util.regex.Pattern;

public class MediaCCCRecentListLinkHandlerFactory extends ListLinkHandlerFactory {
    private static final String PATTERN = "^(https?://)?media\\.ccc\\.de/recent/?$";

    @Override
    public String getId(final String url) {
        return "recent";
    }

    @Override
    public boolean onAcceptUrl(final String url) {
        return Pattern.matches(PATTERN, url);
    }

    @Override
    public String getUrl(final String id,
                         final List<String> contentFilter,
                         final String sortFilter) {
        return "https://media.ccc.de/recent";
    }
}
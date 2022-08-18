package com.downloader.twotwo.video.services.soundcloud.linkHandler;

import com.downloader.twotwo.video.utils.Parser;
import com.downloader.twotwo.video.linkhandler.ListLinkHandlerFactory;

import java.util.List;

public class SoundcloudChartsLinkHandlerFactory extends ListLinkHandlerFactory {
    private static final String TOP_URL_PATTERN =
            "^https?://(www\\.|m\\.)?soundcloud.com/charts(/top)?/?([#?].*)?$";
    private static final String URL_PATTERN =
            "^https?://(www\\.|m\\.)?soundcloud.com/charts(/top|/new)?/?([#?].*)?$";

    @Override
    public String getId(final String url) {
        if (Parser.isMatch(TOP_URL_PATTERN, url.toLowerCase())) {
            return "Top 50";
        } else {
            return "New & hot";
        }
    }

    @Override
    public String getUrl(final String id,
                         final List<String> contentFilter,
                         final String sortFilter) {
        if (id.equals("Top 50")) {
            return "https://soundcloud.com/charts/top";
        } else {
            return "https://soundcloud.com/charts/new";
        }
    }

    @Override
    public boolean onAcceptUrl(final String url) {
        return Parser.isMatch(URL_PATTERN, url.toLowerCase());
    }
}
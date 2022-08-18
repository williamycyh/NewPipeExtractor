// Created by Fynn Godau 2019, licensed GNU GPL version 3 or later

package com.downloader.twotwo.video.services.bandcamp.linkHandler;

import com.downloader.twotwo.video.exceptions.ParsingException;
import com.downloader.twotwo.video.linkhandler.ListLinkHandlerFactory;
import com.downloader.twotwo.video.services.bandcamp.extractors.BandcampExtractorHelper;

import java.util.List;

/**
 * Just as with streams, the album ids are essentially useless for us.
 */
public class BandcampPlaylistLinkHandlerFactory extends ListLinkHandlerFactory {
    @Override
    public String getId(final String url) throws ParsingException {
        return getUrl(url);
    }

    @Override
    public String getUrl(final String url,
                         final List<String> contentFilter,
                         final String sortFilter) throws ParsingException {
        return url;
    }

    /**
     * Accepts all bandcamp URLs that contain /album/ behind their domain name.
     */
    @Override
    public boolean onAcceptUrl(final String url) throws ParsingException {

        // Exclude URLs which do not lead to an album
        if (!url.toLowerCase().matches("https?://.+\\..+/album/.+")) {
            return false;
        }

        // Test whether domain is supported
        return BandcampExtractorHelper.isSupportedDomain(url);
    }
}
package com.github.video.downloader.services.peertube.linkHandler;


import com.github.video.downloader.ServiceList;
import com.github.video.downloader.exceptions.ParsingException;
import com.github.video.downloader.linkhandler.ListLinkHandlerFactory;
import com.github.video.downloader.utils.Parser;

import java.util.List;

public final class PeertubePlaylistLinkHandlerFactory extends ListLinkHandlerFactory {

    private static final PeertubePlaylistLinkHandlerFactory INSTANCE
            = new PeertubePlaylistLinkHandlerFactory();
    private static final String ID_PATTERN = "(/videos/watch/playlist/|/w/p/)([^/?&#]*)";

    private PeertubePlaylistLinkHandlerFactory() {
    }

    public static PeertubePlaylistLinkHandlerFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public String getUrl(final String id,
                         final List<String> contentFilters,
                         final String sortFilter) {
        return getUrl(id, contentFilters, sortFilter, ServiceList.PeerTube.getBaseUrl());
    }

    @Override
    public String getUrl(final String id,
                         final List<String> contentFilters,
                         final String sortFilter,
                         final String baseUrl) {
        return baseUrl + "/api/v1/video-playlists/" + id;
    }

    @Override
    public String getId(final String url) throws ParsingException {
        return Parser.matchGroup(ID_PATTERN, url, 2);
    }

    @Override
    public boolean onAcceptUrl(final String url) {
        try {
            getId(url);
            return true;
        } catch (final ParsingException e) {
            return false;
        }
    }
}

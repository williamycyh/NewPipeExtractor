package com.downloader.twotwo.video.services.peertube.linkHandler;

import com.downloader.twotwo.video.ServiceList;
import com.downloader.twotwo.video.exceptions.ParsingException;
import com.downloader.twotwo.video.linkhandler.SearchQueryHandlerFactory;
import com.downloader.twotwo.video.utils.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public final class PeertubeSearchQueryHandlerFactory extends SearchQueryHandlerFactory {

    public static final String VIDEOS = "videos";
    public static final String SEPIA_VIDEOS = "sepia_videos"; // sepia is the global index
    public static final String SEPIA_BASE_URL = "https://sepiasearch.org";
    public static final String SEARCH_ENDPOINT = "/api/v1/search/videos";

    private PeertubeSearchQueryHandlerFactory() {
    }

    public static PeertubeSearchQueryHandlerFactory getInstance() {
        return new PeertubeSearchQueryHandlerFactory();
    }

    @Override
    public String getUrl(final String searchString,
                         final List<String> contentFilters,
                         final String sortFilter) throws ParsingException {
        final String baseUrl;
        if (!contentFilters.isEmpty() && contentFilters.get(0).startsWith("sepia_")) {
            baseUrl = SEPIA_BASE_URL;
        } else {
            baseUrl = ServiceList.PeerTube.getBaseUrl();
        }
        return getUrl(searchString, contentFilters, sortFilter, baseUrl);
    }

    @Override
    public String getUrl(final String searchString,
                         final List<String> contentFilters,
                         final String sortFilter,
                         final String baseUrl) throws ParsingException {
        try {
            return baseUrl + SEARCH_ENDPOINT + "?search=" + URLEncoder.encode(searchString, Utils.UTF_8);
        } catch (final UnsupportedEncodingException e) {
            throw new ParsingException("Could not encode query", e);
        }
    }

    @Override
    public String[] getAvailableContentFilter() {
        return new String[]{
                VIDEOS,
                SEPIA_VIDEOS
        };
    }
}
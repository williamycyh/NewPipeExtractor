package com.github.video.downloader.services.peertube.extractors;

import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.github.video.downloader.InfoItem;
import com.github.video.downloader.ListExtractor;
import com.github.video.downloader.MetaInfo;
import com.github.video.downloader.MultiInfoItemsCollector;
import com.github.video.downloader.Page;
import com.github.video.downloader.StreamingService;
import com.github.video.downloader.downloader.Downloader;
import com.github.video.downloader.downloader.Response;
import com.github.video.downloader.exceptions.ExtractionException;
import com.github.video.downloader.exceptions.ParsingException;
import com.github.video.downloader.linkhandler.SearchQueryHandler;
import com.github.video.downloader.search.SearchExtractor;
import com.github.video.downloader.services.peertube.PeertubeParsingHelper;
import com.github.video.downloader.utils.Utils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import static com.github.video.downloader.services.peertube.PeertubeParsingHelper.collectStreamsFrom;
import static com.github.video.downloader.utils.Utils.isNullOrEmpty;

public class PeertubeSearchExtractor extends SearchExtractor {

    // if we should use PeertubeSepiaStreamInfoItemExtractor
    private final boolean sepia;

    public PeertubeSearchExtractor(final StreamingService service,
                                   final SearchQueryHandler linkHandler) {
        this(service, linkHandler, false);
    }

    public PeertubeSearchExtractor(final StreamingService service,
                                   final SearchQueryHandler linkHandler,
                                   final boolean sepia) {
        super(service, linkHandler);
        this.sepia = sepia;
    }

    @Nonnull
    @Override
    public String getSearchSuggestion() {
        return "";
    }

    @Override
    public boolean isCorrectedSearch() {
        return false;
    }

    @Nonnull
    @Override
    public List<MetaInfo> getMetaInfo() {
        return Collections.emptyList();
    }

    @Override
    public ListExtractor.InfoItemsPage<InfoItem> getInitialPage() throws IOException, ExtractionException {
        return getPage(new Page(getUrl() + "&" + PeertubeParsingHelper.START_KEY + "=0&"
                + PeertubeParsingHelper.COUNT_KEY + "=" + PeertubeParsingHelper.ITEMS_PER_PAGE));
    }

    @Override
    public ListExtractor.InfoItemsPage<InfoItem> getPage(final Page page)
            throws IOException, ExtractionException {
        if (page == null || Utils.isNullOrEmpty(page.getUrl())) {
            throw new IllegalArgumentException("Page doesn't contain an URL");
        }

        final Response response = getDownloader().get(page.getUrl());

        JsonObject json = null;
        if (response != null && !Utils.isBlank(response.responseBody())) {
            try {
                json = JsonParser.object().from(response.responseBody());
            } catch (final Exception e) {
                throw new ParsingException("Could not parse json data for search info", e);
            }
        }

        if (json != null) {
            PeertubeParsingHelper.validate(json);
            final long total = json.getLong("total");

            final MultiInfoItemsCollector collector = new MultiInfoItemsCollector(getServiceId());
            PeertubeParsingHelper.collectStreamsFrom(collector, json, getBaseUrl(), sepia);

            return new ListExtractor.InfoItemsPage<>(collector,
                    PeertubeParsingHelper.getNextPage(page.getUrl(), total));
        } else {
            throw new ExtractionException("Unable to get PeerTube search info");
        }
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader)
            throws IOException, ExtractionException {
    }
}

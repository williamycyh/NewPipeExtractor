package com.ppd.ersan.video.services.peertube.extractors;

import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.ppd.ersan.video.InfoItem;
import com.ppd.ersan.video.ListExtractor;
import com.ppd.ersan.video.MetaInfo;
import com.ppd.ersan.video.MultiInfoItemsCollector;
import com.ppd.ersan.video.Page;
import com.ppd.ersan.video.StreamingService;
import com.ppd.ersan.video.downloader.Downloader;
import com.ppd.ersan.video.downloader.Response;
import com.ppd.ersan.video.exceptions.ExtractionException;
import com.ppd.ersan.video.exceptions.ParsingException;
import com.ppd.ersan.video.linkhandler.SearchQueryHandler;
import com.ppd.ersan.video.search.SearchExtractor;
import com.ppd.ersan.video.services.peertube.PeertubeParsingHelper;
import com.ppd.ersan.video.utils.Utils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import static com.ppd.ersan.video.services.peertube.PeertubeParsingHelper.collectStreamsFrom;
import static com.ppd.ersan.video.utils.Utils.isNullOrEmpty;

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

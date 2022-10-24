package com.github.video.downloader.services.peertube.extractors;

import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.github.video.downloader.Page;
import com.github.video.downloader.StreamingService;
import com.github.video.downloader.downloader.Downloader;
import com.github.video.downloader.downloader.Response;
import com.github.video.downloader.exceptions.ExtractionException;
import com.github.video.downloader.exceptions.ParsingException;
import com.github.video.downloader.kis.KioskExtractor;
import com.github.video.downloader.linkhandler.ListLinkHandler;
import com.github.video.downloader.services.peertube.PeertubeParsingHelper;
import com.github.video.downloader.stream.StreamInfoItem;
import com.github.video.downloader.stream.StreamInfoItemsCollector;
import com.github.video.downloader.utils.Utils;

import java.io.IOException;

import javax.annotation.Nonnull;

import static com.github.video.downloader.services.peertube.PeertubeParsingHelper.collectStreamsFrom;
import static com.github.video.downloader.utils.Utils.isNullOrEmpty;

public class PeertubeTrendingExtractor extends KioskExtractor<StreamInfoItem> {
    public PeertubeTrendingExtractor(final StreamingService streamingService,
                                     final ListLinkHandler linkHandler,
                                     final String kioskId) {
        super(streamingService, linkHandler, kioskId);
    }

    @Override
    public String getName() throws ParsingException {
        return getId();
    }

    @Override
    public InfoItemsPage<StreamInfoItem> getInitialPage() throws IOException, ExtractionException {
        return getPage(new Page(getUrl() + "&" + PeertubeParsingHelper.START_KEY + "=0&"
                + PeertubeParsingHelper.COUNT_KEY + "=" + PeertubeParsingHelper.ITEMS_PER_PAGE));
    }

    @Override
    public InfoItemsPage<StreamInfoItem> getPage(final Page page)
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
                throw new ParsingException("Could not parse json data for kiosk info", e);
            }
        }

        if (json != null) {
            PeertubeParsingHelper.validate(json);
            final long total = json.getLong("total");

            final StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
            PeertubeParsingHelper.collectStreamsFrom(collector, json, getBaseUrl());

            return new InfoItemsPage<>(collector,
                    PeertubeParsingHelper.getNextPage(page.getUrl(), total));
        } else {
            throw new ExtractionException("Unable to get PeerTube kiosk info");
        }
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader)
            throws IOException, ExtractionException {
    }
}

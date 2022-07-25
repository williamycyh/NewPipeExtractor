package com.downloader.twotwo.video.services.peertube.extractors;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.downloader.twotwo.video.ListExtractor;
import com.downloader.twotwo.video.Page;
import com.downloader.twotwo.video.StreamingService;
import com.downloader.twotwo.video.comments.CommentsExtractor;
import com.downloader.twotwo.video.comments.CommentsInfoItem;
import com.downloader.twotwo.video.comments.CommentsInfoItemsCollector;
import com.downloader.twotwo.video.downloader.Downloader;
import com.downloader.twotwo.video.downloader.Response;
import com.downloader.twotwo.video.exceptions.ExtractionException;
import com.downloader.twotwo.video.exceptions.ParsingException;
import com.downloader.twotwo.video.linkhandler.ListLinkHandler;
import com.downloader.twotwo.video.services.peertube.PeertubeParsingHelper;
import com.downloader.twotwo.video.utils.Utils;

import java.io.IOException;

import static com.downloader.twotwo.video.utils.Utils.isNullOrEmpty;

public class PeertubeCommentsExtractor extends CommentsExtractor {
    public PeertubeCommentsExtractor(final StreamingService service,
                                     final ListLinkHandler uiHandler) {
        super(service, uiHandler);
    }

    @Override
    public ListExtractor.InfoItemsPage<CommentsInfoItem> getInitialPage()
            throws IOException, ExtractionException {
        return getPage(new Page(getUrl() + "?" + PeertubeParsingHelper.START_KEY + "=0&"
                + PeertubeParsingHelper.COUNT_KEY + "=" + PeertubeParsingHelper.ITEMS_PER_PAGE));
    }

    private void collectCommentsFrom(final CommentsInfoItemsCollector collector,
                                     final JsonObject json) throws ParsingException {
        final JsonArray contents = json.getArray("data");

        for (final Object c : contents) {
            if (c instanceof JsonObject) {
                final JsonObject item = (JsonObject) c;
                if (!item.getBoolean("isDeleted")) {
                    collector.commit(new PeertubeCommentsInfoItemExtractor(item, this));
                }
            }
        }
    }

    @Override
    public ListExtractor.InfoItemsPage<CommentsInfoItem> getPage(final Page page)
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
                throw new ParsingException("Could not parse json data for comments info", e);
            }
        }

        if (json != null) {
            PeertubeParsingHelper.validate(json);
            final long total = json.getLong("total");

            final CommentsInfoItemsCollector collector
                    = new CommentsInfoItemsCollector(getServiceId());
            collectCommentsFrom(collector, json);

            return new ListExtractor.InfoItemsPage<>(collector,
                    PeertubeParsingHelper.getNextPage(page.getUrl(), total));
        } else {
            throw new ExtractionException("Unable to get PeerTube kiosk info");
        }
    }

    @Override
    public void onFetchPage(final Downloader downloader) {
    }
}

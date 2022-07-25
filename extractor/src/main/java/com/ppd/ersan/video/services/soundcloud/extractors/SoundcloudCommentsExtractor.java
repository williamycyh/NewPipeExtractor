package com.ppd.ersan.video.services.soundcloud.extractors;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import com.ppd.ersan.video.ListExtractor;
import com.ppd.ersan.video.NewPipe;
import com.ppd.ersan.video.Page;
import com.ppd.ersan.video.StreamingService;
import com.ppd.ersan.video.comments.CommentsExtractor;
import com.ppd.ersan.video.comments.CommentsInfoItem;
import com.ppd.ersan.video.comments.CommentsInfoItemsCollector;
import com.ppd.ersan.video.downloader.Downloader;
import com.ppd.ersan.video.linkhandler.ListLinkHandler;
import com.ppd.ersan.video.utils.Utils;

import com.ppd.ersan.video.downloader.Response;
import com.ppd.ersan.video.exceptions.ExtractionException;
import com.ppd.ersan.video.exceptions.ParsingException;

import java.io.IOException;

import javax.annotation.Nonnull;

import static com.ppd.ersan.video.utils.Utils.isNullOrEmpty;

public class SoundcloudCommentsExtractor extends CommentsExtractor {
    public SoundcloudCommentsExtractor(final StreamingService service,
                                       final ListLinkHandler uiHandler) {
        super(service, uiHandler);
    }

    @Nonnull
    @Override
    public ListExtractor.InfoItemsPage<CommentsInfoItem> getInitialPage() throws ExtractionException,
            IOException {
        final Downloader downloader = NewPipe.getDownloader();
        final Response response = downloader.get(getUrl());

        final JsonObject json;
        try {
            json = JsonParser.object().from(response.responseBody());
        } catch (final JsonParserException e) {
            throw new ParsingException("Could not parse json", e);
        }

        final CommentsInfoItemsCollector collector = new CommentsInfoItemsCollector(
                getServiceId());

        collectStreamsFrom(collector, json.getArray("collection"));

        return new ListExtractor.InfoItemsPage<>(collector, new Page(json.getString("next_href")));
    }

    @Override
    public ListExtractor.InfoItemsPage<CommentsInfoItem> getPage(final Page page) throws ExtractionException,
            IOException {
        if (page == null || Utils.isNullOrEmpty(page.getUrl())) {
            throw new IllegalArgumentException("Page doesn't contain an URL");
        }

        final Downloader downloader = NewPipe.getDownloader();
        final Response response = downloader.get(page.getUrl());

        final JsonObject json;
        try {
            json = JsonParser.object().from(response.responseBody());
        } catch (final JsonParserException e) {
            throw new ParsingException("Could not parse json", e);
        }

        final CommentsInfoItemsCollector collector = new CommentsInfoItemsCollector(
                getServiceId());

        collectStreamsFrom(collector, json.getArray("collection"));

        return new ListExtractor.InfoItemsPage<>(collector, new Page(json.getString("next_href")));
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader) { }

    private void collectStreamsFrom(final CommentsInfoItemsCollector collector,
                                    final JsonArray entries) throws ParsingException {
        final String url = getUrl();
        for (final Object comment : entries) {
            collector.commit(new SoundcloudCommentsInfoItemExtractor((JsonObject) comment, url));
        }
    }
}

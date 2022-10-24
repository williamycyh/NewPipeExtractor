package com.github.video.downloader.services.media_ccc.extractors;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.github.video.downloader.ListExtractor;
import com.github.video.downloader.Page;
import com.github.video.downloader.StreamingService;
import com.github.video.downloader.downloader.Downloader;
import com.github.video.downloader.exceptions.ExtractionException;
import com.github.video.downloader.exceptions.ParsingException;
import com.github.video.downloader.kis.KioskExtractor;
import com.github.video.downloader.linkhandler.ListLinkHandler;
import com.github.video.downloader.stream.StreamInfoItem;
import com.github.video.downloader.stream.StreamInfoItemsCollector;

import javax.annotation.Nonnull;
import java.io.IOException;

public class MediaCCCLiveStreamKiosk extends KioskExtractor<StreamInfoItem> {
    private JsonArray doc;

    public MediaCCCLiveStreamKiosk(final StreamingService streamingService,
                                   final ListLinkHandler linkHandler,
                                   final String kioskId) {
        super(streamingService, linkHandler, kioskId);
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader)
            throws IOException, ExtractionException {
        doc = MediaCCCParsingHelper.getLiveStreams(downloader, getExtractorLocalization());
    }

    @Nonnull
    @Override
    public ListExtractor.InfoItemsPage<StreamInfoItem> getInitialPage() throws IOException, ExtractionException {
        final StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
        for (int c = 0; c < doc.size(); c++) {
            final JsonObject conference = doc.getObject(c);
            final JsonArray groups = conference.getArray("groups");
            for (int g = 0; g < groups.size(); g++) {
                final String group = groups.getObject(g).getString("group");
                final JsonArray rooms = groups.getObject(g).getArray("rooms");
                for (int r = 0; r < rooms.size(); r++) {
                    final JsonObject room = rooms.getObject(r);
                    collector.commit(new MediaCCCLiveStreamKioskExtractor(conference, group, room));
                }
            }

        }
        return new ListExtractor.InfoItemsPage<>(collector, null);
    }

    @Override
    public ListExtractor.InfoItemsPage<StreamInfoItem> getPage(final Page page)
            throws IOException, ExtractionException {
        return ListExtractor.InfoItemsPage.emptyPage();
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        return "live";
    }
}

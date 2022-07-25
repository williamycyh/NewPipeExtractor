// Created by Fynn Godau 2019, licensed GNU GPL version 3 or later

package com.downloader.twotwo.video.services.bandcamp.extractors;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import com.downloader.twotwo.video.Page;
import com.downloader.twotwo.video.StreamingService;
import com.downloader.twotwo.video.downloader.Downloader;
import com.downloader.twotwo.video.exceptions.ExtractionException;
import com.downloader.twotwo.video.exceptions.ParsingException;
import com.downloader.twotwo.video.kiosk.KioskExtractor;
import com.downloader.twotwo.video.linkhandler.ListLinkHandler;
import com.downloader.twotwo.video.stream.StreamInfoItem;
import com.downloader.twotwo.video.stream.StreamInfoItemsCollector;

import javax.annotation.Nonnull;
import java.io.IOException;

import static com.downloader.twotwo.video.services.bandcamp.extractors.BandcampExtractorHelper.BASE_API_URL;

public class BandcampRadioExtractor extends KioskExtractor<StreamInfoItem> {

    public static final String KIOSK_RADIO = "Radio";
    public static final String RADIO_API_URL = BASE_API_URL + "/bcweekly/1/list";

    private JsonObject json = null;

    public BandcampRadioExtractor(final StreamingService streamingService,
                                  final ListLinkHandler linkHandler,
                                  final String kioskId) {
        super(streamingService, linkHandler, kioskId);
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader)
            throws IOException, ExtractionException {
        try {
            json = JsonParser.object().from(
                    getDownloader().get(RADIO_API_URL).responseBody());
        } catch (final JsonParserException e) {
            throw new ExtractionException("Could not parse Bandcamp Radio API response", e);
        }
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        return KIOSK_RADIO;
    }

    @Nonnull
    @Override
    public InfoItemsPage<StreamInfoItem> getInitialPage() {
        final StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());

        final JsonArray radioShows = json.getArray("results");

        for (int i = 0; i < radioShows.size(); i++) {
            final JsonObject radioShow = radioShows.getObject(i);
            collector.commit(new BandcampRadioInfoItemExtractor(radioShow));
        }

        return new InfoItemsPage<>(collector, null);
    }

    @Override
    public InfoItemsPage<StreamInfoItem> getPage(final Page page) {
        return null;
    }
}

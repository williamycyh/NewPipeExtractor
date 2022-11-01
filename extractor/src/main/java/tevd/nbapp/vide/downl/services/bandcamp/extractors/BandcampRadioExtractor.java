// Created by Fynn Godau 2019, licensed GNU GPL version 3 or later

package tevd.nbapp.vide.downl.services.bandcamp.extractors;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import tevd.nbapp.vide.downl.Page;
import tevd.nbapp.vide.downl.StreamingService;
import tevd.nbapp.vide.downl.downloader.Downloader;
import tevd.nbapp.vide.downl.exceptions.ExtractionException;
import tevd.nbapp.vide.downl.exceptions.ParsingException;
import tevd.nbapp.vide.downl.kis.KioskExtractor;
import tevd.nbapp.vide.downl.linkhandler.ListLinkHandler;
import tevd.nbapp.vide.downl.stream.StreamInfoItem;
import tevd.nbapp.vide.downl.stream.StreamInfoItemsCollector;

import javax.annotation.Nonnull;
import java.io.IOException;

public class BandcampRadioExtractor extends KioskExtractor<StreamInfoItem> {

    public static final String KIOSK_RADIO = "Radio";
    public static final String RADIO_API_URL = BandcampExtractorHelper.BASE_API_URL + "/bcweekly/1/list";

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

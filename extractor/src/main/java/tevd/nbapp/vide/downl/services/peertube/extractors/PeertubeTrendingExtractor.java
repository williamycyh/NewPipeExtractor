package tevd.nbapp.vide.downl.services.peertube.extractors;

import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import tevd.nbapp.vide.downl.Page;
import tevd.nbapp.vide.downl.StreamingService;
import tevd.nbapp.vide.downl.downloader.Downloader;
import tevd.nbapp.vide.downl.downloader.Response;
import tevd.nbapp.vide.downl.exceptions.ExtractionException;
import tevd.nbapp.vide.downl.exceptions.ParsingException;
import tevd.nbapp.vide.downl.kis.KioskExtractor;
import tevd.nbapp.vide.downl.linkhandler.ListLinkHandler;
import tevd.nbapp.vide.downl.services.peertube.PeertubeParsingHelper;
import tevd.nbapp.vide.downl.stream.StreamInfoItem;
import tevd.nbapp.vide.downl.stream.StreamInfoItemsCollector;
import tevd.nbapp.vide.downl.utils.Utils;

import java.io.IOException;

import javax.annotation.Nonnull;

import static tevd.nbapp.vide.downl.services.peertube.PeertubeParsingHelper.collectStreamsFrom;
import static tevd.nbapp.vide.downl.utils.Utils.isNullOrEmpty;

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

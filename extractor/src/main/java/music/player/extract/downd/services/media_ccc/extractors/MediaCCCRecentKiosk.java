package music.player.extract.downd.services.media_ccc.extractors;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import music.player.extract.downd.Page;
import music.player.extract.downd.StreamingService;
import music.player.extract.downd.downloader.Downloader;
import music.player.extract.downd.exceptions.ExtractionException;
import music.player.extract.downd.exceptions.ParsingException;
import music.player.extract.downd.kis.KioskExtractor;
import music.player.extract.downd.linkhandler.ListLinkHandler;
import music.player.extract.downd.stream.StreamInfoItem;
import music.player.extract.downd.stream.StreamInfoItemsCollector;

import java.io.IOException;
import java.util.Comparator;

import javax.annotation.Nonnull;

public class MediaCCCRecentKiosk extends KioskExtractor<StreamInfoItem> {

    private JsonObject doc;

    public MediaCCCRecentKiosk(final StreamingService streamingService,
                               final ListLinkHandler linkHandler,
                               final String kioskId) {
        super(streamingService, linkHandler, kioskId);
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader)
            throws IOException, ExtractionException {
        final String site = downloader.get("https://api.media.ccc.de/public/events/recent",
                getExtractorLocalization()).responseBody();
        try {
            doc = JsonParser.object().from(site);
        } catch (final JsonParserException jpe) {
            throw new ExtractionException("Could not parse json.", jpe);
        }
    }

    @Nonnull
    @Override
    public InfoItemsPage<StreamInfoItem> getInitialPage() throws IOException, ExtractionException {
        final JsonArray events = doc.getArray("events");

        // Streams in the recent kiosk are not ordered by the release date.
        // Sort them to have the latest stream at the beginning of the list.
        Comparator<StreamInfoItem> comparator = Comparator.comparing(
                streamInfoItem -> streamInfoItem.getUploadDate().offsetDateTime());
        comparator = comparator.reversed();

        final StreamInfoItemsCollector collector =
                new StreamInfoItemsCollector(getServiceId(), comparator);

        events.stream()
                .filter(JsonObject.class::isInstance)
                .map(JsonObject.class::cast)
                .map(MediaCCCRecentKioskExtractor::new)
                // #813 / voc/voctoweb#609 -> returns faulty data -> filter it out
                .filter(extractor -> extractor.getDuration() > 0)
                .forEach(collector::commit);

        return new InfoItemsPage<>(collector, null);
    }

    @Override
    public InfoItemsPage<StreamInfoItem> getPage(final Page page)
            throws IOException, ExtractionException {
        return InfoItemsPage.emptyPage();
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        return "recent";
    }
}

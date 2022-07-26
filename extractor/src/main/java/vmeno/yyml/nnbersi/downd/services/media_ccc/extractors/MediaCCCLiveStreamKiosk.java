package vmeno.yyml.nnbersi.downd.services.media_ccc.extractors;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import vmeno.yyml.nnbersi.downd.ListExtractor;
import vmeno.yyml.nnbersi.downd.Page;
import vmeno.yyml.nnbersi.downd.StreamingService;
import vmeno.yyml.nnbersi.downd.downloader.Downloader;
import vmeno.yyml.nnbersi.downd.exceptions.ExtractionException;
import vmeno.yyml.nnbersi.downd.exceptions.ParsingException;
import vmeno.yyml.nnbersi.downd.kis.KioskExtractor;
import vmeno.yyml.nnbersi.downd.linkhandler.ListLinkHandler;
import vmeno.yyml.nnbersi.downd.stream.StreamInfoItem;
import vmeno.yyml.nnbersi.downd.stream.StreamInfoItemsCollector;

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

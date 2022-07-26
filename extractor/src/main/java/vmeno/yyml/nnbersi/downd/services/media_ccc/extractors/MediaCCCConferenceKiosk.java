package vmeno.yyml.nnbersi.downd.services.media_ccc.extractors;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import vmeno.yyml.nnbersi.downd.ListExtractor;
import vmeno.yyml.nnbersi.downd.Page;
import vmeno.yyml.nnbersi.downd.StreamingService;
import vmeno.yyml.nnbersi.downd.channel.ChannelInfoItem;
import vmeno.yyml.nnbersi.downd.channel.ChannelInfoItemsCollector;
import vmeno.yyml.nnbersi.downd.downloader.Downloader;
import vmeno.yyml.nnbersi.downd.exceptions.ExtractionException;
import vmeno.yyml.nnbersi.downd.exceptions.ParsingException;
import vmeno.yyml.nnbersi.downd.kis.KioskExtractor;
import vmeno.yyml.nnbersi.downd.linkhandler.ListLinkHandler;

import vmeno.yyml.nnbersi.downd.services.media_ccc.extractors.infoItems.MediaCCCConferenceInfoItemExtractor;

import java.io.IOException;

import javax.annotation.Nonnull;

public class MediaCCCConferenceKiosk extends KioskExtractor<ChannelInfoItem> {
    private JsonObject doc;

    public MediaCCCConferenceKiosk(final StreamingService streamingService,
                                   final ListLinkHandler linkHandler,
                                   final String kioskId) {
        super(streamingService, linkHandler, kioskId);
    }

    @Nonnull
    @Override
    public ListExtractor.InfoItemsPage<ChannelInfoItem> getInitialPage() {
        final JsonArray conferences = doc.getArray("conferences");
        final ChannelInfoItemsCollector collector = new ChannelInfoItemsCollector(getServiceId());
        for (int i = 0; i < conferences.size(); i++) {
            collector.commit(new MediaCCCConferenceInfoItemExtractor(conferences.getObject(i)));
        }

        return new ListExtractor.InfoItemsPage<>(collector, null);
    }

    @Override

    public ListExtractor.InfoItemsPage<ChannelInfoItem> getPage(final Page page) {
        return ListExtractor.InfoItemsPage.emptyPage();
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader)
            throws IOException, ExtractionException {
        final String site = downloader.get(getLinkHandler().getUrl(), getExtractorLocalization())
                .responseBody();
        try {
            doc = JsonParser.object().from(site);
        } catch (final JsonParserException jpe) {
            throw new ExtractionException("Could not parse json.", jpe);
        }
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        return doc.getString("Conferences");
    }
}

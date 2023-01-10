package tndown.tndir.simplevd.vdwload.services.media_ccc.extractors;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import tndown.tndir.simplevd.vdwload.ListExtractor;
import tndown.tndir.simplevd.vdwload.Page;
import tndown.tndir.simplevd.vdwload.StreamingService;
import tndown.tndir.simplevd.vdwload.channel.ChannelInfoItem;
import tndown.tndir.simplevd.vdwload.channel.ChannelInfoItemsCollector;
import tndown.tndir.simplevd.vdwload.downloader.Downloader;
import tndown.tndir.simplevd.vdwload.exceptions.ExtractionException;
import tndown.tndir.simplevd.vdwload.exceptions.ParsingException;
import tndown.tndir.simplevd.vdwload.kis.KioskExtractor;
import tndown.tndir.simplevd.vdwload.linkhandler.ListLinkHandler;

import tndown.tndir.simplevd.vdwload.services.media_ccc.extractors.infoItems.MediaCCCConferenceInfoItemExtractor;

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

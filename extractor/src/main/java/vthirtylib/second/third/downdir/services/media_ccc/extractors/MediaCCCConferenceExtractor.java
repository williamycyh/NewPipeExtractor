package vthirtylib.second.third.downdir.services.media_ccc.extractors;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import vthirtylib.second.third.downdir.ListExtractor;
import vthirtylib.second.third.downdir.Page;
import vthirtylib.second.third.downdir.StreamingService;
import vthirtylib.second.third.downdir.channel.ChannelExtractor;
import vthirtylib.second.third.downdir.downloader.Downloader;
import vthirtylib.second.third.downdir.exceptions.ExtractionException;
import vthirtylib.second.third.downdir.exceptions.ParsingException;
import vthirtylib.second.third.downdir.linkhandler.ListLinkHandler;
import vthirtylib.second.third.downdir.stream.StreamInfoItem;
import vthirtylib.second.third.downdir.stream.StreamInfoItemsCollector;
import vthirtylib.second.third.downdir.services.media_ccc.extractors.infoItems.MediaCCCStreamInfoItemExtractor;
import vthirtylib.second.third.downdir.services.media_ccc.linkHandler.MediaCCCConferenceLinkHandlerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;

public class MediaCCCConferenceExtractor extends ChannelExtractor {
    private JsonObject conferenceData;

    public MediaCCCConferenceExtractor(final StreamingService service,
                                       final ListLinkHandler linkHandler) {
        super(service, linkHandler);
    }

    @Override
    public String getAvatarUrl() {
        return conferenceData.getString("logo_url");
    }

    @Override
    public String getBannerUrl() {
        return conferenceData.getString("logo_url");
    }

    @Override
    public String getFeedUrl() {
        return null;
    }

    @Override
    public long getSubscriberCount() {
        return -1;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getParentChannelName() {
        return "";
    }

    @Override
    public String getParentChannelUrl() {
        return "";
    }

    @Override
    public String getParentChannelAvatarUrl() {
        return "";
    }

    @Override
    public boolean isVerified() {
        return false;
    }

    @Nonnull
    @Override
    public ListExtractor.InfoItemsPage<StreamInfoItem> getInitialPage() {
        final StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
        final JsonArray events = conferenceData.getArray("events");
        for (int i = 0; i < events.size(); i++) {
            collector.commit(new MediaCCCStreamInfoItemExtractor(events.getObject(i)));
        }
        return new ListExtractor.InfoItemsPage<>(collector, null);
    }

    @Override
    public ListExtractor.InfoItemsPage<StreamInfoItem> getPage(final Page page) {
        return ListExtractor.InfoItemsPage.emptyPage();
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader)
            throws IOException, ExtractionException {
        final String conferenceUrl
                = MediaCCCConferenceLinkHandlerFactory.CONFERENCE_API_ENDPOINT + getId();
        try {
            conferenceData = JsonParser.object().from(downloader.get(conferenceUrl).responseBody());
        } catch (final JsonParserException jpe) {
            throw new ExtractionException("Could not parse json returnd by url: " + conferenceUrl);
        }
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        return conferenceData.getString("title");
    }
}

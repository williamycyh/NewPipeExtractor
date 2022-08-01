package music.player.extract.downd.services.media_ccc.extractors;

import static music.player.extract.downd.services.media_ccc.linkHandler.MediaCCCSearchQueryHandlerFactory.ALL;
import static music.player.extract.downd.services.media_ccc.linkHandler.MediaCCCSearchQueryHandlerFactory.CONFERENCES;
import static music.player.extract.downd.services.media_ccc.linkHandler.MediaCCCSearchQueryHandlerFactory.EVENTS;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import music.player.extract.downd.InfoItem;
import music.player.extract.downd.ListExtractor;
import music.player.extract.downd.MetaInfo;
import music.player.extract.downd.MultiInfoItemsCollector;
import music.player.extract.downd.Page;
import music.player.extract.downd.StreamingService;
import music.player.extract.downd.channel.ChannelInfoItem;
import music.player.extract.downd.channel.ChannelInfoItemExtractor;
import music.player.extract.downd.downloader.Downloader;
import music.player.extract.downd.exceptions.ExtractionException;
import music.player.extract.downd.linkhandler.SearchQueryHandler;
import music.player.extract.downd.search.SearchExtractor;

import music.player.extract.downd.services.media_ccc.extractors.infoItems.MediaCCCStreamInfoItemExtractor;
import music.player.extract.downd.services.media_ccc.linkHandler.MediaCCCConferencesListLinkHandlerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

public class MediaCCCSearchExtractor extends SearchExtractor {
    private JsonObject doc;
    private MediaCCCConferenceKiosk conferenceKiosk;

    public MediaCCCSearchExtractor(final StreamingService service,
                                   final SearchQueryHandler linkHandler) {
        super(service, linkHandler);
        try {
            conferenceKiosk = new MediaCCCConferenceKiosk(service,
                    new MediaCCCConferencesListLinkHandlerFactory().fromId("conferences"),
                    "conferences");
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @Nonnull
    @Override
    public String getSearchSuggestion() {
        return "";
    }

    @Override
    public boolean isCorrectedSearch() {
        return false;
    }

    @Nonnull
    @Override
    public List<MetaInfo> getMetaInfo() {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public ListExtractor.InfoItemsPage<InfoItem> getInitialPage() {
        final MultiInfoItemsCollector searchItems = new MultiInfoItemsCollector(getServiceId());

        if (getLinkHandler().getContentFilters().contains(CONFERENCES)
                || getLinkHandler().getContentFilters().contains(ALL)
                || getLinkHandler().getContentFilters().isEmpty()) {
            searchConferences(getSearchString(),
                    conferenceKiosk.getInitialPage().getItems(),
                    searchItems);
        }

        if (getLinkHandler().getContentFilters().contains(EVENTS)
                || getLinkHandler().getContentFilters().contains(ALL)
                || getLinkHandler().getContentFilters().isEmpty()) {
            final JsonArray events = doc.getArray("events");
            for (int i = 0; i < events.size(); i++) {
                // Ensure only uploaded talks are shown in the search results.
                // If the release date is null, the talk has not been held or uploaded yet
                // and no streams are going to be available anyway.
                if (events.getObject(i).getString("release_date") != null) {
                    searchItems.commit(new MediaCCCStreamInfoItemExtractor(
                            events.getObject(i)));
                }
            }
        }
        return new ListExtractor.InfoItemsPage<>(searchItems, null);
    }

    @Override
    public ListExtractor.InfoItemsPage<InfoItem> getPage(final Page page) {
        return ListExtractor.InfoItemsPage.emptyPage();
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader)
            throws IOException, ExtractionException {
        if (getLinkHandler().getContentFilters().contains(EVENTS)
                || getLinkHandler().getContentFilters().contains(ALL)
                || getLinkHandler().getContentFilters().isEmpty()) {
            final String site;
            final String url = getUrl();
            site = downloader.get(url, getExtractorLocalization()).responseBody();
            try {
                doc = JsonParser.object().from(site);
            } catch (final JsonParserException jpe) {
                throw new ExtractionException("Could not parse JSON.", jpe);
            }
        }
        if (getLinkHandler().getContentFilters().contains(CONFERENCES)
                || getLinkHandler().getContentFilters().contains(ALL)
                || getLinkHandler().getContentFilters().isEmpty()) {
            conferenceKiosk.fetchPage();
        }
    }

    private void searchConferences(final String searchString,
                                   final List<ChannelInfoItem> channelItems,
                                   final MultiInfoItemsCollector collector) {
        for (final ChannelInfoItem item : channelItems) {
            if (item.getName().toUpperCase().contains(
                    searchString.toUpperCase())) {
                collector.commit(new ChannelInfoItemExtractor() {
                    @Override
                    public String getDescription() {
                        return item.getDescription();
                    }

                    @Override
                    public long getSubscriberCount() {
                        return item.getSubscriberCount();
                    }

                    @Override
                    public long getStreamCount() {
                        return item.getStreamCount();
                    }

                    @Override
                    public boolean isVerified() {
                        return false;
                    }

                    @Override
                    public String getName() {
                        return item.getName();
                    }

                    @Override
                    public String getUrl() {
                        return item.getUrl();
                    }

                    @Override
                    public String getThumbnailUrl() {
                        return item.getThumbnailUrl();
                    }
                });
            }
        }
    }
}

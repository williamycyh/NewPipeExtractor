package music.player.extract.downd.services.media_ccc;

import music.player.extract.downd.StreamingService;
import music.player.extract.downd.channel.ChannelExtractor;
import music.player.extract.downd.comments.CommentsExtractor;
import music.player.extract.downd.exceptions.ExtractionException;
import music.player.extract.downd.kis.KioskList;
import music.player.extract.downd.linkhandler.LinkHandler;
import music.player.extract.downd.linkhandler.LinkHandlerFactory;
import music.player.extract.downd.linkhandler.ListLinkHandler;
import music.player.extract.downd.linkhandler.ListLinkHandlerFactory;
import music.player.extract.downd.linkhandler.SearchQueryHandler;
import music.player.extract.downd.linkhandler.SearchQueryHandlerFactory;
import music.player.extract.downd.playlist.PlaylistExtractor;
import music.player.extract.downd.search.SearchExtractor;
import music.player.extract.downd.services.media_ccc.extractors.MediaCCCConferenceExtractor;
import music.player.extract.downd.services.media_ccc.extractors.MediaCCCConferenceKiosk;
import music.player.extract.downd.services.media_ccc.extractors.MediaCCCLiveStreamKiosk;
import music.player.extract.downd.services.media_ccc.extractors.MediaCCCParsingHelper;
import music.player.extract.downd.services.media_ccc.extractors.MediaCCCRecentKiosk;
import music.player.extract.downd.services.media_ccc.extractors.MediaCCCSearchExtractor;
import music.player.extract.downd.stream.StreamExtractor;
import music.player.extract.downd.subscription.SubscriptionExtractor;
import music.player.extract.downd.suggestion.SuggestionExtractor;

import static java.util.Arrays.asList;

import music.player.extract.downd.services.media_ccc.extractors.MediaCCCLiveStreamExtractor;
import music.player.extract.downd.services.media_ccc.extractors.MediaCCCStreamExtractor;
import music.player.extract.downd.services.media_ccc.linkHandler.MediaCCCConferenceLinkHandlerFactory;
import music.player.extract.downd.services.media_ccc.linkHandler.MediaCCCConferencesListLinkHandlerFactory;
import music.player.extract.downd.services.media_ccc.linkHandler.MediaCCCLiveListLinkHandlerFactory;
import music.player.extract.downd.services.media_ccc.linkHandler.MediaCCCRecentListLinkHandlerFactory;
import music.player.extract.downd.services.media_ccc.linkHandler.MediaCCCSearchQueryHandlerFactory;
import music.player.extract.downd.services.media_ccc.linkHandler.MediaCCCStreamLinkHandlerFactory;

public class MediaCCCService extends StreamingService {
    public MediaCCCService(final int id) {
        super(id, "media.ccc.de", asList(ServiceInfo.MediaCapability.AUDIO, ServiceInfo.MediaCapability.VIDEO));
    }

    @Override
    public SearchExtractor getSearchExtractor(final SearchQueryHandler query) {
        return new MediaCCCSearchExtractor(this, query);
    }

    @Override
    public LinkHandlerFactory getStreamLHFactory() {
        return new MediaCCCStreamLinkHandlerFactory();
    }

    @Override
    public ListLinkHandlerFactory getChannelLHFactory() {
        return new MediaCCCConferenceLinkHandlerFactory();
    }

    @Override
    public ListLinkHandlerFactory getPlaylistLHFactory() {
        return null;
    }

    @Override
    public SearchQueryHandlerFactory getSearchQHFactory() {
        return new MediaCCCSearchQueryHandlerFactory();
    }

    @Override
    public StreamExtractor getStreamExtractor(final LinkHandler linkHandler) {
        if (MediaCCCParsingHelper.isLiveStreamId(linkHandler.getId())) {
            return new MediaCCCLiveStreamExtractor(this, linkHandler);
        }
        return new MediaCCCStreamExtractor(this, linkHandler);
    }

    @Override
    public ChannelExtractor getChannelExtractor(final ListLinkHandler linkHandler) {
        return new MediaCCCConferenceExtractor(this, linkHandler);
    }

    @Override
    public PlaylistExtractor getPlaylistExtractor(final ListLinkHandler linkHandler) {
        return null;
    }

    @Override
    public SuggestionExtractor getSuggestionExtractor() {
        return null;
    }

    @Override
    public KioskList getKioskList() throws ExtractionException {
        final KioskList list = new KioskList(this);

        // add kiosks here e.g.:
        try {
            list.addKioskEntry(
                    (streamingService, url, kioskId) -> new MediaCCCConferenceKiosk(
                            MediaCCCService.this,
                            new MediaCCCConferencesListLinkHandlerFactory().fromUrl(url),
                            kioskId
                    ),
                    new MediaCCCConferencesListLinkHandlerFactory(),
                    "conferences"
            );

            list.addKioskEntry(
                    (streamingService, url, kioskId) -> new MediaCCCRecentKiosk(
                            MediaCCCService.this,
                            new MediaCCCRecentListLinkHandlerFactory().fromUrl(url),
                            kioskId
                    ),
                    new MediaCCCRecentListLinkHandlerFactory(),
                    "recent"
            );

            list.addKioskEntry(
                    (streamingService, url, kioskId) -> new MediaCCCLiveStreamKiosk(
                            MediaCCCService.this,
                            new MediaCCCLiveListLinkHandlerFactory().fromUrl(url),
                            kioskId
                    ),
                    new MediaCCCLiveListLinkHandlerFactory(),
                    "live"
            );

            list.setDefaultKiosk("recent");
        } catch (final Exception e) {
            throw new ExtractionException(e);
        }

        return list;
    }

    @Override
    public SubscriptionExtractor getSubscriptionExtractor() {
        return null;
    }

    @Override
    public ListLinkHandlerFactory getCommentsLHFactory() {
        return null;
    }

    @Override
    public CommentsExtractor getCommentsExtractor(final ListLinkHandler linkHandler) {
        return null;
    }

    @Override
    public String getBaseUrl() {
        return "https://media.ccc.de";
    }

}

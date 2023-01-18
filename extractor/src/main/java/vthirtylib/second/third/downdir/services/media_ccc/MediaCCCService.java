package vthirtylib.second.third.downdir.services.media_ccc;

import vthirtylib.second.third.downdir.StreamingService;
import vthirtylib.second.third.downdir.channel.ChannelExtractor;
import vthirtylib.second.third.downdir.comments.CommentsExtractor;
import vthirtylib.second.third.downdir.exceptions.ExtractionException;
import vthirtylib.second.third.downdir.kis.KioskList;
import vthirtylib.second.third.downdir.linkhandler.LinkHandler;
import vthirtylib.second.third.downdir.linkhandler.LinkHandlerFactory;
import vthirtylib.second.third.downdir.linkhandler.ListLinkHandler;
import vthirtylib.second.third.downdir.linkhandler.ListLinkHandlerFactory;
import vthirtylib.second.third.downdir.linkhandler.SearchQueryHandler;
import vthirtylib.second.third.downdir.linkhandler.SearchQueryHandlerFactory;
import vthirtylib.second.third.downdir.playlist.PlaylistExtractor;
import vthirtylib.second.third.downdir.search.SearchExtractor;
import vthirtylib.second.third.downdir.services.media_ccc.extractors.MediaCCCConferenceExtractor;
import vthirtylib.second.third.downdir.services.media_ccc.extractors.MediaCCCConferenceKiosk;
import vthirtylib.second.third.downdir.services.media_ccc.extractors.MediaCCCLiveStreamKiosk;
import vthirtylib.second.third.downdir.services.media_ccc.extractors.MediaCCCParsingHelper;
import vthirtylib.second.third.downdir.services.media_ccc.extractors.MediaCCCRecentKiosk;
import vthirtylib.second.third.downdir.services.media_ccc.extractors.MediaCCCSearchExtractor;
import vthirtylib.second.third.downdir.stream.StreamExtractor;
import vthirtylib.second.third.downdir.subscription.SubscriptionExtractor;
import vthirtylib.second.third.downdir.suggestion.SuggestionExtractor;

import static java.util.Arrays.asList;

import vthirtylib.second.third.downdir.services.media_ccc.extractors.MediaCCCLiveStreamExtractor;
import vthirtylib.second.third.downdir.services.media_ccc.extractors.MediaCCCStreamExtractor;
import vthirtylib.second.third.downdir.services.media_ccc.linkHandler.MediaCCCConferenceLinkHandlerFactory;
import vthirtylib.second.third.downdir.services.media_ccc.linkHandler.MediaCCCConferencesListLinkHandlerFactory;
import vthirtylib.second.third.downdir.services.media_ccc.linkHandler.MediaCCCLiveListLinkHandlerFactory;
import vthirtylib.second.third.downdir.services.media_ccc.linkHandler.MediaCCCRecentListLinkHandlerFactory;
import vthirtylib.second.third.downdir.services.media_ccc.linkHandler.MediaCCCSearchQueryHandlerFactory;
import vthirtylib.second.third.downdir.services.media_ccc.linkHandler.MediaCCCStreamLinkHandlerFactory;

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

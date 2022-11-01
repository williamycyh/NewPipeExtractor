package tevd.nbapp.vide.downl.services.media_ccc;

import tevd.nbapp.vide.downl.StreamingService;
import tevd.nbapp.vide.downl.channel.ChannelExtractor;
import tevd.nbapp.vide.downl.comments.CommentsExtractor;
import tevd.nbapp.vide.downl.exceptions.ExtractionException;
import tevd.nbapp.vide.downl.kis.KioskList;
import tevd.nbapp.vide.downl.linkhandler.LinkHandler;
import tevd.nbapp.vide.downl.linkhandler.LinkHandlerFactory;
import tevd.nbapp.vide.downl.linkhandler.ListLinkHandler;
import tevd.nbapp.vide.downl.linkhandler.ListLinkHandlerFactory;
import tevd.nbapp.vide.downl.linkhandler.SearchQueryHandler;
import tevd.nbapp.vide.downl.linkhandler.SearchQueryHandlerFactory;
import tevd.nbapp.vide.downl.playlist.PlaylistExtractor;
import tevd.nbapp.vide.downl.search.SearchExtractor;
import tevd.nbapp.vide.downl.services.media_ccc.extractors.MediaCCCConferenceExtractor;
import tevd.nbapp.vide.downl.services.media_ccc.extractors.MediaCCCConferenceKiosk;
import tevd.nbapp.vide.downl.services.media_ccc.extractors.MediaCCCLiveStreamKiosk;
import tevd.nbapp.vide.downl.services.media_ccc.extractors.MediaCCCParsingHelper;
import tevd.nbapp.vide.downl.services.media_ccc.extractors.MediaCCCRecentKiosk;
import tevd.nbapp.vide.downl.services.media_ccc.extractors.MediaCCCSearchExtractor;
import tevd.nbapp.vide.downl.stream.StreamExtractor;
import tevd.nbapp.vide.downl.subscription.SubscriptionExtractor;
import tevd.nbapp.vide.downl.suggestion.SuggestionExtractor;

import static java.util.Arrays.asList;

import tevd.nbapp.vide.downl.services.media_ccc.extractors.MediaCCCLiveStreamExtractor;
import tevd.nbapp.vide.downl.services.media_ccc.extractors.MediaCCCStreamExtractor;
import tevd.nbapp.vide.downl.services.media_ccc.linkHandler.MediaCCCConferenceLinkHandlerFactory;
import tevd.nbapp.vide.downl.services.media_ccc.linkHandler.MediaCCCConferencesListLinkHandlerFactory;
import tevd.nbapp.vide.downl.services.media_ccc.linkHandler.MediaCCCLiveListLinkHandlerFactory;
import tevd.nbapp.vide.downl.services.media_ccc.linkHandler.MediaCCCRecentListLinkHandlerFactory;
import tevd.nbapp.vide.downl.services.media_ccc.linkHandler.MediaCCCSearchQueryHandlerFactory;
import tevd.nbapp.vide.downl.services.media_ccc.linkHandler.MediaCCCStreamLinkHandlerFactory;

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

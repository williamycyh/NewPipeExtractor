package tevd.nbapp.vide.downl.services.peertube;

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
import tevd.nbapp.vide.downl.services.peertube.linkHandler.PeertubeCommentsLinkHandlerFactory;
import tevd.nbapp.vide.downl.stream.StreamExtractor;
import tevd.nbapp.vide.downl.subscription.SubscriptionExtractor;
import tevd.nbapp.vide.downl.suggestion.SuggestionExtractor;

import static java.util.Arrays.asList;

import tevd.nbapp.vide.downl.services.peertube.extractors.PeertubeAccountExtractor;
import tevd.nbapp.vide.downl.services.peertube.extractors.PeertubeChannelExtractor;
import tevd.nbapp.vide.downl.services.peertube.extractors.PeertubeCommentsExtractor;
import tevd.nbapp.vide.downl.services.peertube.extractors.PeertubePlaylistExtractor;
import tevd.nbapp.vide.downl.services.peertube.extractors.PeertubeSearchExtractor;
import tevd.nbapp.vide.downl.services.peertube.extractors.PeertubeStreamExtractor;
import tevd.nbapp.vide.downl.services.peertube.extractors.PeertubeSuggestionExtractor;
import tevd.nbapp.vide.downl.services.peertube.extractors.PeertubeTrendingExtractor;
import tevd.nbapp.vide.downl.services.peertube.linkHandler.PeertubeChannelLinkHandlerFactory;
import tevd.nbapp.vide.downl.services.peertube.linkHandler.PeertubePlaylistLinkHandlerFactory;
import tevd.nbapp.vide.downl.services.peertube.linkHandler.PeertubeSearchQueryHandlerFactory;
import tevd.nbapp.vide.downl.services.peertube.linkHandler.PeertubeStreamLinkHandlerFactory;
import tevd.nbapp.vide.downl.services.peertube.linkHandler.PeertubeTrendingLinkHandlerFactory;

import java.util.List;

public class PeertubeService extends StreamingService {

    private PeertubeInstance instance;

    public PeertubeService(final int id) {
        this(id, PeertubeInstance.DEFAULT_INSTANCE);
    }

    public PeertubeService(final int id, final PeertubeInstance instance) {
        super(id, "PeerTube", asList(ServiceInfo.MediaCapability.VIDEO, ServiceInfo.MediaCapability.COMMENTS));
        this.instance = instance;
    }

    @Override
    public LinkHandlerFactory getStreamLHFactory() {
        return PeertubeStreamLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getChannelLHFactory() {
        return PeertubeChannelLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getPlaylistLHFactory() {
        return PeertubePlaylistLinkHandlerFactory.getInstance();
    }

    @Override
    public SearchQueryHandlerFactory getSearchQHFactory() {
        return PeertubeSearchQueryHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getCommentsLHFactory() {
        return PeertubeCommentsLinkHandlerFactory.getInstance();
    }

    @Override
    public SearchExtractor getSearchExtractor(final SearchQueryHandler queryHandler) {
        final List<String> contentFilters = queryHandler.getContentFilters();
        return new PeertubeSearchExtractor(this, queryHandler,
                !contentFilters.isEmpty() && contentFilters.get(0).startsWith("sepia_"));
    }

    @Override
    public SuggestionExtractor getSuggestionExtractor() {
        return new PeertubeSuggestionExtractor(this);
    }

    @Override
    public SubscriptionExtractor getSubscriptionExtractor() {
        return null;
    }

    @Override
    public ChannelExtractor getChannelExtractor(final ListLinkHandler linkHandler)
            throws ExtractionException {

        if (linkHandler.getUrl().contains("/video-channels/")) {
            return new PeertubeChannelExtractor(this, linkHandler);
        } else {
            return new PeertubeAccountExtractor(this, linkHandler);
        }
    }

    @Override
    public PlaylistExtractor getPlaylistExtractor(final ListLinkHandler linkHandler)
            throws ExtractionException {
        return new PeertubePlaylistExtractor(this, linkHandler);
    }

    @Override
    public StreamExtractor getStreamExtractor(final LinkHandler linkHandler)
            throws ExtractionException {
        return new PeertubeStreamExtractor(this, linkHandler);
    }

    @Override
    public CommentsExtractor getCommentsExtractor(final ListLinkHandler linkHandler)
            throws ExtractionException {
        return new PeertubeCommentsExtractor(this, linkHandler);
    }

    @Override
    public String getBaseUrl() {
        return instance.getUrl();
    }

    public PeertubeInstance getInstance() {
        return this.instance;
    }

    public void setInstance(final PeertubeInstance instance) {
        this.instance = instance;
    }

    @Override
    public KioskList getKioskList() throws ExtractionException {
        final KioskList.KioskExtractorFactory kioskFactory = (streamingService, url, id) ->
                new PeertubeTrendingExtractor(
                        PeertubeService.this,
                        new PeertubeTrendingLinkHandlerFactory().fromId(id),
                        id
                );

        final KioskList list = new KioskList(this);

        // add kiosks here e.g.:
        final PeertubeTrendingLinkHandlerFactory h = new PeertubeTrendingLinkHandlerFactory();
        try {
            list.addKioskEntry(kioskFactory, h, PeertubeTrendingLinkHandlerFactory.KIOSK_TRENDING);
            list.addKioskEntry(kioskFactory, h,
                    PeertubeTrendingLinkHandlerFactory.KIOSK_MOST_LIKED);
            list.addKioskEntry(kioskFactory, h, PeertubeTrendingLinkHandlerFactory.KIOSK_RECENT);
            list.addKioskEntry(kioskFactory, h, PeertubeTrendingLinkHandlerFactory.KIOSK_LOCAL);
            list.setDefaultKiosk(PeertubeTrendingLinkHandlerFactory.KIOSK_TRENDING);
        } catch (final Exception e) {
            throw new ExtractionException(e);
        }

        return list;
    }


}

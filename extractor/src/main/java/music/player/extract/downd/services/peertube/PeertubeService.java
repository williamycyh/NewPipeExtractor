package music.player.extract.downd.services.peertube;

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
import music.player.extract.downd.services.peertube.linkHandler.PeertubeCommentsLinkHandlerFactory;
import music.player.extract.downd.stream.StreamExtractor;
import music.player.extract.downd.subscription.SubscriptionExtractor;
import music.player.extract.downd.suggestion.SuggestionExtractor;

import static java.util.Arrays.asList;

import music.player.extract.downd.services.peertube.extractors.PeertubeAccountExtractor;
import music.player.extract.downd.services.peertube.extractors.PeertubeChannelExtractor;
import music.player.extract.downd.services.peertube.extractors.PeertubeCommentsExtractor;
import music.player.extract.downd.services.peertube.extractors.PeertubePlaylistExtractor;
import music.player.extract.downd.services.peertube.extractors.PeertubeSearchExtractor;
import music.player.extract.downd.services.peertube.extractors.PeertubeStreamExtractor;
import music.player.extract.downd.services.peertube.extractors.PeertubeSuggestionExtractor;
import music.player.extract.downd.services.peertube.extractors.PeertubeTrendingExtractor;
import music.player.extract.downd.services.peertube.linkHandler.PeertubeChannelLinkHandlerFactory;
import music.player.extract.downd.services.peertube.linkHandler.PeertubePlaylistLinkHandlerFactory;
import music.player.extract.downd.services.peertube.linkHandler.PeertubeSearchQueryHandlerFactory;
import music.player.extract.downd.services.peertube.linkHandler.PeertubeStreamLinkHandlerFactory;
import music.player.extract.downd.services.peertube.linkHandler.PeertubeTrendingLinkHandlerFactory;

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

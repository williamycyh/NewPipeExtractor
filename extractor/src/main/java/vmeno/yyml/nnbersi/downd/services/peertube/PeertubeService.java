package vmeno.yyml.nnbersi.downd.services.peertube;

import vmeno.yyml.nnbersi.downd.StreamingService;
import vmeno.yyml.nnbersi.downd.channel.ChannelExtractor;
import vmeno.yyml.nnbersi.downd.comments.CommentsExtractor;
import vmeno.yyml.nnbersi.downd.exceptions.ExtractionException;
import vmeno.yyml.nnbersi.downd.kis.KioskList;
import vmeno.yyml.nnbersi.downd.linkhandler.LinkHandler;
import vmeno.yyml.nnbersi.downd.linkhandler.LinkHandlerFactory;
import vmeno.yyml.nnbersi.downd.linkhandler.ListLinkHandler;
import vmeno.yyml.nnbersi.downd.linkhandler.ListLinkHandlerFactory;
import vmeno.yyml.nnbersi.downd.linkhandler.SearchQueryHandler;
import vmeno.yyml.nnbersi.downd.linkhandler.SearchQueryHandlerFactory;
import vmeno.yyml.nnbersi.downd.playlist.PlaylistExtractor;
import vmeno.yyml.nnbersi.downd.search.SearchExtractor;
import vmeno.yyml.nnbersi.downd.services.peertube.linkHandler.PeertubeCommentsLinkHandlerFactory;
import vmeno.yyml.nnbersi.downd.stream.StreamExtractor;
import vmeno.yyml.nnbersi.downd.subscription.SubscriptionExtractor;
import vmeno.yyml.nnbersi.downd.suggestion.SuggestionExtractor;

import static java.util.Arrays.asList;

import vmeno.yyml.nnbersi.downd.services.peertube.extractors.PeertubeAccountExtractor;
import vmeno.yyml.nnbersi.downd.services.peertube.extractors.PeertubeChannelExtractor;
import vmeno.yyml.nnbersi.downd.services.peertube.extractors.PeertubeCommentsExtractor;
import vmeno.yyml.nnbersi.downd.services.peertube.extractors.PeertubePlaylistExtractor;
import vmeno.yyml.nnbersi.downd.services.peertube.extractors.PeertubeSearchExtractor;
import vmeno.yyml.nnbersi.downd.services.peertube.extractors.PeertubeStreamExtractor;
import vmeno.yyml.nnbersi.downd.services.peertube.extractors.PeertubeSuggestionExtractor;
import vmeno.yyml.nnbersi.downd.services.peertube.extractors.PeertubeTrendingExtractor;
import vmeno.yyml.nnbersi.downd.services.peertube.linkHandler.PeertubeChannelLinkHandlerFactory;
import vmeno.yyml.nnbersi.downd.services.peertube.linkHandler.PeertubePlaylistLinkHandlerFactory;
import vmeno.yyml.nnbersi.downd.services.peertube.linkHandler.PeertubeSearchQueryHandlerFactory;
import vmeno.yyml.nnbersi.downd.services.peertube.linkHandler.PeertubeStreamLinkHandlerFactory;
import vmeno.yyml.nnbersi.downd.services.peertube.linkHandler.PeertubeTrendingLinkHandlerFactory;

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

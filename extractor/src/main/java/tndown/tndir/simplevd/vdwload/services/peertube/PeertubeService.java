package tndown.tndir.simplevd.vdwload.services.peertube;

import tndown.tndir.simplevd.vdwload.StreamingService;
import tndown.tndir.simplevd.vdwload.channel.ChannelExtractor;
import tndown.tndir.simplevd.vdwload.comments.CommentsExtractor;
import tndown.tndir.simplevd.vdwload.exceptions.ExtractionException;
import tndown.tndir.simplevd.vdwload.kis.KioskList;
import tndown.tndir.simplevd.vdwload.linkhandler.LinkHandler;
import tndown.tndir.simplevd.vdwload.linkhandler.LinkHandlerFactory;
import tndown.tndir.simplevd.vdwload.linkhandler.ListLinkHandler;
import tndown.tndir.simplevd.vdwload.linkhandler.ListLinkHandlerFactory;
import tndown.tndir.simplevd.vdwload.linkhandler.SearchQueryHandler;
import tndown.tndir.simplevd.vdwload.linkhandler.SearchQueryHandlerFactory;
import tndown.tndir.simplevd.vdwload.playlist.PlaylistExtractor;
import tndown.tndir.simplevd.vdwload.search.SearchExtractor;
import tndown.tndir.simplevd.vdwload.services.peertube.linkHandler.PeertubeCommentsLinkHandlerFactory;
import tndown.tndir.simplevd.vdwload.stream.StreamExtractor;
import tndown.tndir.simplevd.vdwload.subscription.SubscriptionExtractor;
import tndown.tndir.simplevd.vdwload.suggestion.SuggestionExtractor;

import static java.util.Arrays.asList;

import tndown.tndir.simplevd.vdwload.services.peertube.extractors.PeertubeAccountExtractor;
import tndown.tndir.simplevd.vdwload.services.peertube.extractors.PeertubeChannelExtractor;
import tndown.tndir.simplevd.vdwload.services.peertube.extractors.PeertubeCommentsExtractor;
import tndown.tndir.simplevd.vdwload.services.peertube.extractors.PeertubePlaylistExtractor;
import tndown.tndir.simplevd.vdwload.services.peertube.extractors.PeertubeSearchExtractor;
import tndown.tndir.simplevd.vdwload.services.peertube.extractors.PeertubeStreamExtractor;
import tndown.tndir.simplevd.vdwload.services.peertube.extractors.PeertubeSuggestionExtractor;
import tndown.tndir.simplevd.vdwload.services.peertube.extractors.PeertubeTrendingExtractor;
import tndown.tndir.simplevd.vdwload.services.peertube.linkHandler.PeertubeChannelLinkHandlerFactory;
import tndown.tndir.simplevd.vdwload.services.peertube.linkHandler.PeertubePlaylistLinkHandlerFactory;
import tndown.tndir.simplevd.vdwload.services.peertube.linkHandler.PeertubeSearchQueryHandlerFactory;
import tndown.tndir.simplevd.vdwload.services.peertube.linkHandler.PeertubeStreamLinkHandlerFactory;
import tndown.tndir.simplevd.vdwload.services.peertube.linkHandler.PeertubeTrendingLinkHandlerFactory;

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

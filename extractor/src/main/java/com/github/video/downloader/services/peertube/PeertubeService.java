package com.github.video.downloader.services.peertube;

import com.github.video.downloader.StreamingService;
import com.github.video.downloader.channel.ChannelExtractor;
import com.github.video.downloader.comments.CommentsExtractor;
import com.github.video.downloader.exceptions.ExtractionException;
import com.github.video.downloader.kis.KioskList;
import com.github.video.downloader.linkhandler.LinkHandler;
import com.github.video.downloader.linkhandler.LinkHandlerFactory;
import com.github.video.downloader.linkhandler.ListLinkHandler;
import com.github.video.downloader.linkhandler.ListLinkHandlerFactory;
import com.github.video.downloader.linkhandler.SearchQueryHandler;
import com.github.video.downloader.linkhandler.SearchQueryHandlerFactory;
import com.github.video.downloader.playlist.PlaylistExtractor;
import com.github.video.downloader.search.SearchExtractor;
import com.github.video.downloader.services.peertube.linkHandler.PeertubeCommentsLinkHandlerFactory;
import com.github.video.downloader.stream.StreamExtractor;
import com.github.video.downloader.subscription.SubscriptionExtractor;
import com.github.video.downloader.suggestion.SuggestionExtractor;

import static java.util.Arrays.asList;

import com.github.video.downloader.services.peertube.extractors.PeertubeAccountExtractor;
import com.github.video.downloader.services.peertube.extractors.PeertubeChannelExtractor;
import com.github.video.downloader.services.peertube.extractors.PeertubeCommentsExtractor;
import com.github.video.downloader.services.peertube.extractors.PeertubePlaylistExtractor;
import com.github.video.downloader.services.peertube.extractors.PeertubeSearchExtractor;
import com.github.video.downloader.services.peertube.extractors.PeertubeStreamExtractor;
import com.github.video.downloader.services.peertube.extractors.PeertubeSuggestionExtractor;
import com.github.video.downloader.services.peertube.extractors.PeertubeTrendingExtractor;
import com.github.video.downloader.services.peertube.linkHandler.PeertubeChannelLinkHandlerFactory;
import com.github.video.downloader.services.peertube.linkHandler.PeertubePlaylistLinkHandlerFactory;
import com.github.video.downloader.services.peertube.linkHandler.PeertubeSearchQueryHandlerFactory;
import com.github.video.downloader.services.peertube.linkHandler.PeertubeStreamLinkHandlerFactory;
import com.github.video.downloader.services.peertube.linkHandler.PeertubeTrendingLinkHandlerFactory;

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

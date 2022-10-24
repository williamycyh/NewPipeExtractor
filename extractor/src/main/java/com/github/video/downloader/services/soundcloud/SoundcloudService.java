package com.github.video.downloader.services.soundcloud;

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
import com.github.video.downloader.localization.ContentCountry;
import com.github.video.downloader.playlist.PlaylistExtractor;
import com.github.video.downloader.search.SearchExtractor;
import com.github.video.downloader.services.soundcloud.extractors.SoundcloudChannelExtractor;
import com.github.video.downloader.services.soundcloud.extractors.SoundcloudChartsExtractor;
import com.github.video.downloader.services.soundcloud.extractors.SoundcloudCommentsExtractor;
import com.github.video.downloader.services.soundcloud.extractors.SoundcloudSearchExtractor;
import com.github.video.downloader.services.soundcloud.extractors.SoundcloudStreamExtractor;
import com.github.video.downloader.services.soundcloud.extractors.SoundcloudSuggestionExtractor;
import com.github.video.downloader.services.soundcloud.linkHandler.SoundcloudChannelLinkHandlerFactory;
import com.github.video.downloader.services.soundcloud.linkHandler.SoundcloudChartsLinkHandlerFactory;
import com.github.video.downloader.services.soundcloud.linkHandler.SoundcloudCommentsLinkHandlerFactory;
import com.github.video.downloader.services.soundcloud.linkHandler.SoundcloudPlaylistLinkHandlerFactory;
import com.github.video.downloader.services.soundcloud.linkHandler.SoundcloudSearchQueryHandlerFactory;
import com.github.video.downloader.services.soundcloud.linkHandler.SoundcloudStreamLinkHandlerFactory;
import com.github.video.downloader.stream.StreamExtractor;
import com.github.video.downloader.subscription.SubscriptionExtractor;

import static java.util.Arrays.asList;

import com.github.video.downloader.services.soundcloud.extractors.SoundcloudPlaylistExtractor;
import com.github.video.downloader.services.soundcloud.extractors.SoundcloudSubscriptionExtractor;

import java.util.List;

public class SoundcloudService extends StreamingService {

    public SoundcloudService(final int id) {
        super(id, "SoundCloud", asList(ServiceInfo.MediaCapability.AUDIO, ServiceInfo.MediaCapability.COMMENTS));
    }

    @Override
    public String getBaseUrl() {
        return "https://soundcloud.com";
    }

    @Override
    public SearchQueryHandlerFactory getSearchQHFactory() {
        return new SoundcloudSearchQueryHandlerFactory();
    }

    @Override
    public LinkHandlerFactory getStreamLHFactory() {
        return SoundcloudStreamLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getChannelLHFactory() {
        return SoundcloudChannelLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getPlaylistLHFactory() {
        return SoundcloudPlaylistLinkHandlerFactory.getInstance();
    }

    @Override
    public List<ContentCountry> getSupportedCountries() {
        // Country selector here: https://soundcloud.com/charts/top?genre=all-music
        return ContentCountry.listFrom(
                "AU", "CA", "DE", "FR", "GB", "IE", "NL", "NZ", "US"
        );
    }

    @Override
    public StreamExtractor getStreamExtractor(final LinkHandler linkHandler) {
        return new SoundcloudStreamExtractor(this, linkHandler);
    }

    @Override
    public ChannelExtractor getChannelExtractor(final ListLinkHandler linkHandler) {
        return new SoundcloudChannelExtractor(this, linkHandler);
    }

    @Override
    public PlaylistExtractor getPlaylistExtractor(final ListLinkHandler linkHandler) {
        return new SoundcloudPlaylistExtractor(this, linkHandler);
    }

    @Override
    public SearchExtractor getSearchExtractor(final SearchQueryHandler queryHandler) {
        return new SoundcloudSearchExtractor(this, queryHandler);
    }

    @Override
    public SoundcloudSuggestionExtractor getSuggestionExtractor() {
        return new SoundcloudSuggestionExtractor(this);
    }

    @Override
    public KioskList getKioskList() throws ExtractionException {
        final KioskList.KioskExtractorFactory chartsFactory = (streamingService, url, id) ->
                new SoundcloudChartsExtractor(SoundcloudService.this,
                        new SoundcloudChartsLinkHandlerFactory().fromUrl(url), id);

        final KioskList list = new KioskList(this);

        // add kiosks here e.g.:
        final SoundcloudChartsLinkHandlerFactory h = new SoundcloudChartsLinkHandlerFactory();
        try {
            list.addKioskEntry(chartsFactory, h, "Top 50");
            list.addKioskEntry(chartsFactory, h, "New & hot");
            list.setDefaultKiosk("New & hot");
        } catch (final Exception e) {
            throw new ExtractionException(e);
        }

        return list;
    }

    @Override
    public SubscriptionExtractor getSubscriptionExtractor() {
        return new SoundcloudSubscriptionExtractor(this);
    }

    @Override
    public ListLinkHandlerFactory getCommentsLHFactory() {
        return SoundcloudCommentsLinkHandlerFactory.getInstance();
    }

    @Override
    public CommentsExtractor getCommentsExtractor(final ListLinkHandler linkHandler)
            throws ExtractionException {
        return new SoundcloudCommentsExtractor(this, linkHandler);
    }
}

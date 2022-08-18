package com.downloader.twotwo.video.services.soundcloud;

import com.downloader.twotwo.video.StreamingService;
import com.downloader.twotwo.video.channel.ChannelExtractor;
import com.downloader.twotwo.video.comments.CommentsExtractor;
import com.downloader.twotwo.video.exceptions.ExtractionException;
import com.downloader.twotwo.video.kiosk.KioskList;
import com.downloader.twotwo.video.linkhandler.LinkHandler;
import com.downloader.twotwo.video.linkhandler.LinkHandlerFactory;
import com.downloader.twotwo.video.linkhandler.ListLinkHandler;
import com.downloader.twotwo.video.linkhandler.ListLinkHandlerFactory;
import com.downloader.twotwo.video.linkhandler.SearchQueryHandler;
import com.downloader.twotwo.video.linkhandler.SearchQueryHandlerFactory;
import com.downloader.twotwo.video.localization.ContentCountry;
import com.downloader.twotwo.video.playlist.PlaylistExtractor;
import com.downloader.twotwo.video.search.SearchExtractor;
import com.downloader.twotwo.video.services.soundcloud.extractors.SoundcloudChannelExtractor;
import com.downloader.twotwo.video.services.soundcloud.extractors.SoundcloudChartsExtractor;
import com.downloader.twotwo.video.services.soundcloud.extractors.SoundcloudCommentsExtractor;
import com.downloader.twotwo.video.services.soundcloud.extractors.SoundcloudSearchExtractor;
import com.downloader.twotwo.video.services.soundcloud.extractors.SoundcloudStreamExtractor;
import com.downloader.twotwo.video.services.soundcloud.extractors.SoundcloudSuggestionExtractor;
import com.downloader.twotwo.video.services.soundcloud.linkHandler.SoundcloudChannelLinkHandlerFactory;
import com.downloader.twotwo.video.services.soundcloud.linkHandler.SoundcloudChartsLinkHandlerFactory;
import com.downloader.twotwo.video.services.soundcloud.linkHandler.SoundcloudCommentsLinkHandlerFactory;
import com.downloader.twotwo.video.services.soundcloud.linkHandler.SoundcloudPlaylistLinkHandlerFactory;
import com.downloader.twotwo.video.services.soundcloud.linkHandler.SoundcloudSearchQueryHandlerFactory;
import com.downloader.twotwo.video.services.soundcloud.linkHandler.SoundcloudStreamLinkHandlerFactory;
import com.downloader.twotwo.video.stream.StreamExtractor;
import com.downloader.twotwo.video.subscription.SubscriptionExtractor;

import static java.util.Arrays.asList;

import com.downloader.twotwo.video.services.soundcloud.extractors.SoundcloudPlaylistExtractor;
import com.downloader.twotwo.video.services.soundcloud.extractors.SoundcloudSubscriptionExtractor;

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
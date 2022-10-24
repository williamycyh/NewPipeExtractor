// Created by Fynn Godau 2019, licensed GNU GPL version 3 or later

package com.github.video.downloader.services.bandcamp;

import static com.github.video.downloader.StreamingService.ServiceInfo.MediaCapability.AUDIO;
import static com.github.video.downloader.StreamingService.ServiceInfo.MediaCapability.COMMENTS;
import static com.github.video.downloader.services.bandcamp.extractors.BandcampExtractorHelper.BASE_URL;
import static com.github.video.downloader.services.bandcamp.extractors.BandcampFeaturedExtractor.FEATURED_API_URL;
import static com.github.video.downloader.services.bandcamp.extractors.BandcampFeaturedExtractor.KIOSK_FEATURED;
import static com.github.video.downloader.services.bandcamp.extractors.BandcampRadioExtractor.KIOSK_RADIO;
import static com.github.video.downloader.services.bandcamp.extractors.BandcampRadioExtractor.RADIO_API_URL;

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
import com.github.video.downloader.services.bandcamp.extractors.BandcampChannelExtractor;
import com.github.video.downloader.services.bandcamp.extractors.BandcampCommentsExtractor;
import com.github.video.downloader.services.bandcamp.extractors.BandcampExtractorHelper;
import com.github.video.downloader.services.bandcamp.extractors.BandcampFeaturedExtractor;
import com.github.video.downloader.services.bandcamp.extractors.BandcampPlaylistExtractor;
import com.github.video.downloader.services.bandcamp.extractors.BandcampRadioExtractor;
import com.github.video.downloader.services.bandcamp.extractors.BandcampRadioStreamExtractor;
import com.github.video.downloader.services.bandcamp.extractors.BandcampSearchExtractor;
import com.github.video.downloader.services.bandcamp.extractors.BandcampStreamExtractor;
import com.github.video.downloader.services.bandcamp.extractors.BandcampSuggestionExtractor;
import com.github.video.downloader.services.bandcamp.linkHandler.BandcampChannelLinkHandlerFactory;
import com.github.video.downloader.services.bandcamp.linkHandler.BandcampCommentsLinkHandlerFactory;
import com.github.video.downloader.services.bandcamp.linkHandler.BandcampFeaturedLinkHandlerFactory;
import com.github.video.downloader.services.bandcamp.linkHandler.BandcampPlaylistLinkHandlerFactory;
import com.github.video.downloader.services.bandcamp.linkHandler.BandcampSearchQueryHandlerFactory;
import com.github.video.downloader.services.bandcamp.linkHandler.BandcampStreamLinkHandlerFactory;
import com.github.video.downloader.stream.StreamExtractor;
import com.github.video.downloader.subscription.SubscriptionExtractor;
import com.github.video.downloader.suggestion.SuggestionExtractor;

import java.util.Arrays;

public class BandcampService extends StreamingService {

    public BandcampService(final int id) {
        super(id, "Bandcamp", Arrays.asList(AUDIO, COMMENTS));
    }

    @Override
    public String getBaseUrl() {
        return BASE_URL;
    }

    @Override
    public LinkHandlerFactory getStreamLHFactory() {
        return new BandcampStreamLinkHandlerFactory();
    }

    @Override
    public ListLinkHandlerFactory getChannelLHFactory() {
        return new BandcampChannelLinkHandlerFactory();
    }

    @Override
    public ListLinkHandlerFactory getPlaylistLHFactory() {
        return new BandcampPlaylistLinkHandlerFactory();
    }

    @Override
    public SearchQueryHandlerFactory getSearchQHFactory() {
        return new BandcampSearchQueryHandlerFactory();
    }

    @Override
    public ListLinkHandlerFactory getCommentsLHFactory() {
        return new BandcampCommentsLinkHandlerFactory();
    }

    @Override
    public SearchExtractor getSearchExtractor(final SearchQueryHandler queryHandler) {
        return new BandcampSearchExtractor(this, queryHandler);
    }

    @Override
    public SuggestionExtractor getSuggestionExtractor() {
        return new BandcampSuggestionExtractor(this);
    }

    @Override
    public SubscriptionExtractor getSubscriptionExtractor() {
        return null;
    }

    @Override
    public KioskList getKioskList() throws ExtractionException {

        final KioskList kioskList = new KioskList(this);

        try {
            kioskList.addKioskEntry(
                    (streamingService, url, kioskId) -> new BandcampFeaturedExtractor(
                            BandcampService.this,
                            new BandcampFeaturedLinkHandlerFactory().fromUrl(FEATURED_API_URL),
                            kioskId
                    ),
                    new BandcampFeaturedLinkHandlerFactory(),
                    KIOSK_FEATURED
            );

            kioskList.addKioskEntry(
                    (streamingService, url, kioskId) -> new BandcampRadioExtractor(
                            BandcampService.this,
                            new BandcampFeaturedLinkHandlerFactory().fromUrl(RADIO_API_URL),
                            kioskId
                    ),
                    new BandcampFeaturedLinkHandlerFactory(),
                    KIOSK_RADIO
            );

            kioskList.setDefaultKiosk(KIOSK_FEATURED);

        } catch (final Exception e) {
            throw new ExtractionException(e);
        }

        return kioskList;
    }

    @Override
    public ChannelExtractor getChannelExtractor(final ListLinkHandler linkHandler) {
        return new BandcampChannelExtractor(this, linkHandler);
    }

    @Override
    public PlaylistExtractor getPlaylistExtractor(final ListLinkHandler linkHandler) {
        return new BandcampPlaylistExtractor(this, linkHandler);
    }

    @Override
    public StreamExtractor getStreamExtractor(final LinkHandler linkHandler) {
        if (BandcampExtractorHelper.isRadioUrl(linkHandler.getUrl())) {
            return new BandcampRadioStreamExtractor(this, linkHandler);
        }
        return new BandcampStreamExtractor(this, linkHandler);
    }

    @Override
    public CommentsExtractor getCommentsExtractor(final ListLinkHandler linkHandler) {
        return new BandcampCommentsExtractor(this, linkHandler);
    }
}

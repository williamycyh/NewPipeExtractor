// Created by Fynn Godau 2019, licensed GNU GPL version 3 or later

package com.downloader.twotwo.video.services.bandcamp;

import static com.downloader.twotwo.video.StreamingService.ServiceInfo.MediaCapability.AUDIO;
import static com.downloader.twotwo.video.StreamingService.ServiceInfo.MediaCapability.COMMENTS;
import static com.downloader.twotwo.video.services.bandcamp.extractors.BandcampExtractorHelper.BASE_URL;
import static com.downloader.twotwo.video.services.bandcamp.extractors.BandcampFeaturedExtractor.FEATURED_API_URL;
import static com.downloader.twotwo.video.services.bandcamp.extractors.BandcampFeaturedExtractor.KIOSK_FEATURED;
import static com.downloader.twotwo.video.services.bandcamp.extractors.BandcampRadioExtractor.KIOSK_RADIO;
import static com.downloader.twotwo.video.services.bandcamp.extractors.BandcampRadioExtractor.RADIO_API_URL;

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
import com.downloader.twotwo.video.playlist.PlaylistExtractor;
import com.downloader.twotwo.video.search.SearchExtractor;
import com.downloader.twotwo.video.services.bandcamp.extractors.BandcampChannelExtractor;
import com.downloader.twotwo.video.services.bandcamp.extractors.BandcampCommentsExtractor;
import com.downloader.twotwo.video.services.bandcamp.extractors.BandcampExtractorHelper;
import com.downloader.twotwo.video.services.bandcamp.extractors.BandcampFeaturedExtractor;
import com.downloader.twotwo.video.services.bandcamp.extractors.BandcampPlaylistExtractor;
import com.downloader.twotwo.video.services.bandcamp.extractors.BandcampRadioExtractor;
import com.downloader.twotwo.video.services.bandcamp.extractors.BandcampRadioStreamExtractor;
import com.downloader.twotwo.video.services.bandcamp.extractors.BandcampSearchExtractor;
import com.downloader.twotwo.video.services.bandcamp.extractors.BandcampStreamExtractor;
import com.downloader.twotwo.video.services.bandcamp.extractors.BandcampSuggestionExtractor;
import com.downloader.twotwo.video.services.bandcamp.linkHandler.BandcampChannelLinkHandlerFactory;
import com.downloader.twotwo.video.services.bandcamp.linkHandler.BandcampCommentsLinkHandlerFactory;
import com.downloader.twotwo.video.services.bandcamp.linkHandler.BandcampFeaturedLinkHandlerFactory;
import com.downloader.twotwo.video.services.bandcamp.linkHandler.BandcampPlaylistLinkHandlerFactory;
import com.downloader.twotwo.video.services.bandcamp.linkHandler.BandcampSearchQueryHandlerFactory;
import com.downloader.twotwo.video.services.bandcamp.linkHandler.BandcampStreamLinkHandlerFactory;
import com.downloader.twotwo.video.stream.StreamExtractor;
import com.downloader.twotwo.video.subscription.SubscriptionExtractor;
import com.downloader.twotwo.video.suggestion.SuggestionExtractor;

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

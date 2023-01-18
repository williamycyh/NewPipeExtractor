// Created by Fynn Godau 2019, licensed GNU GPL version 3 or later

package vthirtylib.second.third.downdir.services.bandcamp;

import vthirtylib.second.third.downdir.StreamingService;
import vthirtylib.second.third.downdir.channel.ChannelExtractor;
import vthirtylib.second.third.downdir.comments.CommentsExtractor;
import vthirtylib.second.third.downdir.exceptions.ExtractionException;
import vthirtylib.second.third.downdir.kis.KioskList;
import vthirtylib.second.third.downdir.linkhandler.LinkHandler;
import vthirtylib.second.third.downdir.linkhandler.LinkHandlerFactory;
import vthirtylib.second.third.downdir.linkhandler.ListLinkHandler;
import vthirtylib.second.third.downdir.linkhandler.ListLinkHandlerFactory;
import vthirtylib.second.third.downdir.linkhandler.SearchQueryHandler;
import vthirtylib.second.third.downdir.linkhandler.SearchQueryHandlerFactory;
import vthirtylib.second.third.downdir.playlist.PlaylistExtractor;
import vthirtylib.second.third.downdir.search.SearchExtractor;
import vthirtylib.second.third.downdir.services.bandcamp.extractors.BandcampChannelExtractor;
import vthirtylib.second.third.downdir.services.bandcamp.extractors.BandcampCommentsExtractor;
import vthirtylib.second.third.downdir.services.bandcamp.extractors.BandcampExtractorHelper;
import vthirtylib.second.third.downdir.services.bandcamp.extractors.BandcampFeaturedExtractor;
import vthirtylib.second.third.downdir.services.bandcamp.extractors.BandcampPlaylistExtractor;
import vthirtylib.second.third.downdir.services.bandcamp.extractors.BandcampRadioExtractor;
import vthirtylib.second.third.downdir.services.bandcamp.extractors.BandcampRadioStreamExtractor;
import vthirtylib.second.third.downdir.services.bandcamp.extractors.BandcampSearchExtractor;
import vthirtylib.second.third.downdir.services.bandcamp.extractors.BandcampStreamExtractor;
import vthirtylib.second.third.downdir.services.bandcamp.extractors.BandcampSuggestionExtractor;
import vthirtylib.second.third.downdir.services.bandcamp.linkHandler.BandcampChannelLinkHandlerFactory;
import vthirtylib.second.third.downdir.services.bandcamp.linkHandler.BandcampCommentsLinkHandlerFactory;
import vthirtylib.second.third.downdir.services.bandcamp.linkHandler.BandcampFeaturedLinkHandlerFactory;
import vthirtylib.second.third.downdir.services.bandcamp.linkHandler.BandcampPlaylistLinkHandlerFactory;
import vthirtylib.second.third.downdir.services.bandcamp.linkHandler.BandcampSearchQueryHandlerFactory;
import vthirtylib.second.third.downdir.services.bandcamp.linkHandler.BandcampStreamLinkHandlerFactory;
import vthirtylib.second.third.downdir.stream.StreamExtractor;
import vthirtylib.second.third.downdir.subscription.SubscriptionExtractor;
import vthirtylib.second.third.downdir.suggestion.SuggestionExtractor;

import java.util.Arrays;

public class BandcampService extends StreamingService {

    public BandcampService(final int id) {
        super(id, "Bandcamp", Arrays.asList(ServiceInfo.MediaCapability.AUDIO, ServiceInfo.MediaCapability.COMMENTS));
    }

    @Override
    public String getBaseUrl() {
        return BandcampExtractorHelper.BASE_URL;
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
                            new BandcampFeaturedLinkHandlerFactory().fromUrl(BandcampFeaturedExtractor.FEATURED_API_URL),
                            kioskId
                    ),
                    new BandcampFeaturedLinkHandlerFactory(),
                    BandcampFeaturedExtractor.KIOSK_FEATURED
            );

            kioskList.addKioskEntry(
                    (streamingService, url, kioskId) -> new BandcampRadioExtractor(
                            BandcampService.this,
                            new BandcampFeaturedLinkHandlerFactory().fromUrl(BandcampRadioExtractor.RADIO_API_URL),
                            kioskId
                    ),
                    new BandcampFeaturedLinkHandlerFactory(),
                    BandcampRadioExtractor.KIOSK_RADIO
            );

            kioskList.setDefaultKiosk(BandcampFeaturedExtractor.KIOSK_FEATURED);

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

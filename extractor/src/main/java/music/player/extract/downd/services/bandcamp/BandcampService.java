// Created by Fynn Godau 2019, licensed GNU GPL version 3 or later

package music.player.extract.downd.services.bandcamp;

import static music.player.extract.downd.StreamingService.ServiceInfo.MediaCapability.AUDIO;
import static music.player.extract.downd.StreamingService.ServiceInfo.MediaCapability.COMMENTS;
import static music.player.extract.downd.services.bandcamp.extractors.BandcampExtractorHelper.BASE_URL;
import static music.player.extract.downd.services.bandcamp.extractors.BandcampFeaturedExtractor.FEATURED_API_URL;
import static music.player.extract.downd.services.bandcamp.extractors.BandcampFeaturedExtractor.KIOSK_FEATURED;
import static music.player.extract.downd.services.bandcamp.extractors.BandcampRadioExtractor.KIOSK_RADIO;
import static music.player.extract.downd.services.bandcamp.extractors.BandcampRadioExtractor.RADIO_API_URL;

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
import music.player.extract.downd.services.bandcamp.extractors.BandcampChannelExtractor;
import music.player.extract.downd.services.bandcamp.extractors.BandcampCommentsExtractor;
import music.player.extract.downd.services.bandcamp.extractors.BandcampExtractorHelper;
import music.player.extract.downd.services.bandcamp.extractors.BandcampFeaturedExtractor;
import music.player.extract.downd.services.bandcamp.extractors.BandcampPlaylistExtractor;
import music.player.extract.downd.services.bandcamp.extractors.BandcampRadioExtractor;
import music.player.extract.downd.services.bandcamp.extractors.BandcampRadioStreamExtractor;
import music.player.extract.downd.services.bandcamp.extractors.BandcampSearchExtractor;
import music.player.extract.downd.services.bandcamp.extractors.BandcampStreamExtractor;
import music.player.extract.downd.services.bandcamp.extractors.BandcampSuggestionExtractor;
import music.player.extract.downd.services.bandcamp.linkHandler.BandcampChannelLinkHandlerFactory;
import music.player.extract.downd.services.bandcamp.linkHandler.BandcampCommentsLinkHandlerFactory;
import music.player.extract.downd.services.bandcamp.linkHandler.BandcampFeaturedLinkHandlerFactory;
import music.player.extract.downd.services.bandcamp.linkHandler.BandcampPlaylistLinkHandlerFactory;
import music.player.extract.downd.services.bandcamp.linkHandler.BandcampSearchQueryHandlerFactory;
import music.player.extract.downd.services.bandcamp.linkHandler.BandcampStreamLinkHandlerFactory;
import music.player.extract.downd.stream.StreamExtractor;
import music.player.extract.downd.subscription.SubscriptionExtractor;
import music.player.extract.downd.suggestion.SuggestionExtractor;

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

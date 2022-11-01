// Created by Fynn Godau 2019, licensed GNU GPL version 3 or later

package tevd.nbapp.vide.downl.services.bandcamp;

import tevd.nbapp.vide.downl.StreamingService;
import tevd.nbapp.vide.downl.channel.ChannelExtractor;
import tevd.nbapp.vide.downl.comments.CommentsExtractor;
import tevd.nbapp.vide.downl.exceptions.ExtractionException;
import tevd.nbapp.vide.downl.kis.KioskList;
import tevd.nbapp.vide.downl.linkhandler.LinkHandler;
import tevd.nbapp.vide.downl.linkhandler.LinkHandlerFactory;
import tevd.nbapp.vide.downl.linkhandler.ListLinkHandler;
import tevd.nbapp.vide.downl.linkhandler.ListLinkHandlerFactory;
import tevd.nbapp.vide.downl.linkhandler.SearchQueryHandler;
import tevd.nbapp.vide.downl.linkhandler.SearchQueryHandlerFactory;
import tevd.nbapp.vide.downl.playlist.PlaylistExtractor;
import tevd.nbapp.vide.downl.search.SearchExtractor;
import tevd.nbapp.vide.downl.services.bandcamp.extractors.BandcampChannelExtractor;
import tevd.nbapp.vide.downl.services.bandcamp.extractors.BandcampCommentsExtractor;
import tevd.nbapp.vide.downl.services.bandcamp.extractors.BandcampExtractorHelper;
import tevd.nbapp.vide.downl.services.bandcamp.extractors.BandcampFeaturedExtractor;
import tevd.nbapp.vide.downl.services.bandcamp.extractors.BandcampPlaylistExtractor;
import tevd.nbapp.vide.downl.services.bandcamp.extractors.BandcampRadioExtractor;
import tevd.nbapp.vide.downl.services.bandcamp.extractors.BandcampRadioStreamExtractor;
import tevd.nbapp.vide.downl.services.bandcamp.extractors.BandcampSearchExtractor;
import tevd.nbapp.vide.downl.services.bandcamp.extractors.BandcampStreamExtractor;
import tevd.nbapp.vide.downl.services.bandcamp.extractors.BandcampSuggestionExtractor;
import tevd.nbapp.vide.downl.services.bandcamp.linkHandler.BandcampChannelLinkHandlerFactory;
import tevd.nbapp.vide.downl.services.bandcamp.linkHandler.BandcampCommentsLinkHandlerFactory;
import tevd.nbapp.vide.downl.services.bandcamp.linkHandler.BandcampFeaturedLinkHandlerFactory;
import tevd.nbapp.vide.downl.services.bandcamp.linkHandler.BandcampPlaylistLinkHandlerFactory;
import tevd.nbapp.vide.downl.services.bandcamp.linkHandler.BandcampSearchQueryHandlerFactory;
import tevd.nbapp.vide.downl.services.bandcamp.linkHandler.BandcampStreamLinkHandlerFactory;
import tevd.nbapp.vide.downl.stream.StreamExtractor;
import tevd.nbapp.vide.downl.subscription.SubscriptionExtractor;
import tevd.nbapp.vide.downl.suggestion.SuggestionExtractor;

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

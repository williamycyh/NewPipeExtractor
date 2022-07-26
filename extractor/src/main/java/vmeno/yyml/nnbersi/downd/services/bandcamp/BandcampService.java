// Created by Fynn Godau 2019, licensed GNU GPL version 3 or later

package vmeno.yyml.nnbersi.downd.services.bandcamp;

import static vmeno.yyml.nnbersi.downd.StreamingService.ServiceInfo.MediaCapability.AUDIO;
import static vmeno.yyml.nnbersi.downd.StreamingService.ServiceInfo.MediaCapability.COMMENTS;
import static vmeno.yyml.nnbersi.downd.services.bandcamp.extractors.BandcampExtractorHelper.BASE_URL;
import static vmeno.yyml.nnbersi.downd.services.bandcamp.extractors.BandcampFeaturedExtractor.FEATURED_API_URL;
import static vmeno.yyml.nnbersi.downd.services.bandcamp.extractors.BandcampFeaturedExtractor.KIOSK_FEATURED;
import static vmeno.yyml.nnbersi.downd.services.bandcamp.extractors.BandcampRadioExtractor.KIOSK_RADIO;
import static vmeno.yyml.nnbersi.downd.services.bandcamp.extractors.BandcampRadioExtractor.RADIO_API_URL;

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
import vmeno.yyml.nnbersi.downd.services.bandcamp.extractors.BandcampChannelExtractor;
import vmeno.yyml.nnbersi.downd.services.bandcamp.extractors.BandcampCommentsExtractor;
import vmeno.yyml.nnbersi.downd.services.bandcamp.extractors.BandcampExtractorHelper;
import vmeno.yyml.nnbersi.downd.services.bandcamp.extractors.BandcampFeaturedExtractor;
import vmeno.yyml.nnbersi.downd.services.bandcamp.extractors.BandcampPlaylistExtractor;
import vmeno.yyml.nnbersi.downd.services.bandcamp.extractors.BandcampRadioExtractor;
import vmeno.yyml.nnbersi.downd.services.bandcamp.extractors.BandcampRadioStreamExtractor;
import vmeno.yyml.nnbersi.downd.services.bandcamp.extractors.BandcampSearchExtractor;
import vmeno.yyml.nnbersi.downd.services.bandcamp.extractors.BandcampStreamExtractor;
import vmeno.yyml.nnbersi.downd.services.bandcamp.extractors.BandcampSuggestionExtractor;
import vmeno.yyml.nnbersi.downd.services.bandcamp.linkHandler.BandcampChannelLinkHandlerFactory;
import vmeno.yyml.nnbersi.downd.services.bandcamp.linkHandler.BandcampCommentsLinkHandlerFactory;
import vmeno.yyml.nnbersi.downd.services.bandcamp.linkHandler.BandcampFeaturedLinkHandlerFactory;
import vmeno.yyml.nnbersi.downd.services.bandcamp.linkHandler.BandcampPlaylistLinkHandlerFactory;
import vmeno.yyml.nnbersi.downd.services.bandcamp.linkHandler.BandcampSearchQueryHandlerFactory;
import vmeno.yyml.nnbersi.downd.services.bandcamp.linkHandler.BandcampStreamLinkHandlerFactory;
import vmeno.yyml.nnbersi.downd.stream.StreamExtractor;
import vmeno.yyml.nnbersi.downd.subscription.SubscriptionExtractor;
import vmeno.yyml.nnbersi.downd.suggestion.SuggestionExtractor;

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

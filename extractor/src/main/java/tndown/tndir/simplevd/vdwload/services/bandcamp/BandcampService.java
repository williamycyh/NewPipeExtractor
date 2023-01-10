// Created by Fynn Godau 2019, licensed GNU GPL version 3 or later

package tndown.tndir.simplevd.vdwload.services.bandcamp;

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
import tndown.tndir.simplevd.vdwload.services.bandcamp.extractors.BandcampChannelExtractor;
import tndown.tndir.simplevd.vdwload.services.bandcamp.extractors.BandcampCommentsExtractor;
import tndown.tndir.simplevd.vdwload.services.bandcamp.extractors.BandcampExtractorHelper;
import tndown.tndir.simplevd.vdwload.services.bandcamp.extractors.BandcampFeaturedExtractor;
import tndown.tndir.simplevd.vdwload.services.bandcamp.extractors.BandcampPlaylistExtractor;
import tndown.tndir.simplevd.vdwload.services.bandcamp.extractors.BandcampRadioExtractor;
import tndown.tndir.simplevd.vdwload.services.bandcamp.extractors.BandcampRadioStreamExtractor;
import tndown.tndir.simplevd.vdwload.services.bandcamp.extractors.BandcampSearchExtractor;
import tndown.tndir.simplevd.vdwload.services.bandcamp.extractors.BandcampStreamExtractor;
import tndown.tndir.simplevd.vdwload.services.bandcamp.extractors.BandcampSuggestionExtractor;
import tndown.tndir.simplevd.vdwload.services.bandcamp.linkHandler.BandcampChannelLinkHandlerFactory;
import tndown.tndir.simplevd.vdwload.services.bandcamp.linkHandler.BandcampCommentsLinkHandlerFactory;
import tndown.tndir.simplevd.vdwload.services.bandcamp.linkHandler.BandcampFeaturedLinkHandlerFactory;
import tndown.tndir.simplevd.vdwload.services.bandcamp.linkHandler.BandcampPlaylistLinkHandlerFactory;
import tndown.tndir.simplevd.vdwload.services.bandcamp.linkHandler.BandcampSearchQueryHandlerFactory;
import tndown.tndir.simplevd.vdwload.services.bandcamp.linkHandler.BandcampStreamLinkHandlerFactory;
import tndown.tndir.simplevd.vdwload.stream.StreamExtractor;
import tndown.tndir.simplevd.vdwload.subscription.SubscriptionExtractor;
import tndown.tndir.simplevd.vdwload.suggestion.SuggestionExtractor;

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

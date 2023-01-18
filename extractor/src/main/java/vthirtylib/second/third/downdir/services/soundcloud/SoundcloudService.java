package vthirtylib.second.third.downdir.services.soundcloud;

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
import vthirtylib.second.third.downdir.localization.ContentCountry;
import vthirtylib.second.third.downdir.playlist.PlaylistExtractor;
import vthirtylib.second.third.downdir.search.SearchExtractor;
import vthirtylib.second.third.downdir.services.soundcloud.extractors.SoundcloudChannelExtractor;
import vthirtylib.second.third.downdir.services.soundcloud.extractors.SoundcloudChartsExtractor;
import vthirtylib.second.third.downdir.services.soundcloud.extractors.SoundcloudCommentsExtractor;
import vthirtylib.second.third.downdir.services.soundcloud.extractors.SoundcloudSearchExtractor;
import vthirtylib.second.third.downdir.services.soundcloud.extractors.SoundcloudStreamExtractor;
import vthirtylib.second.third.downdir.services.soundcloud.extractors.SoundcloudSuggestionExtractor;
import vthirtylib.second.third.downdir.services.soundcloud.linkHandler.SoundcloudChannelLinkHandlerFactory;
import vthirtylib.second.third.downdir.services.soundcloud.linkHandler.SoundcloudChartsLinkHandlerFactory;
import vthirtylib.second.third.downdir.services.soundcloud.linkHandler.SoundcloudCommentsLinkHandlerFactory;
import vthirtylib.second.third.downdir.services.soundcloud.linkHandler.SoundcloudPlaylistLinkHandlerFactory;
import vthirtylib.second.third.downdir.services.soundcloud.linkHandler.SoundcloudSearchQueryHandlerFactory;
import vthirtylib.second.third.downdir.services.soundcloud.linkHandler.SoundcloudStreamLinkHandlerFactory;
import vthirtylib.second.third.downdir.stream.StreamExtractor;
import vthirtylib.second.third.downdir.subscription.SubscriptionExtractor;

import static java.util.Arrays.asList;

import vthirtylib.second.third.downdir.services.soundcloud.extractors.SoundcloudPlaylistExtractor;
import vthirtylib.second.third.downdir.services.soundcloud.extractors.SoundcloudSubscriptionExtractor;

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

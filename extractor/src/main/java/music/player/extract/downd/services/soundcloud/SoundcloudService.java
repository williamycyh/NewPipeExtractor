package music.player.extract.downd.services.soundcloud;

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
import music.player.extract.downd.localization.ContentCountry;
import music.player.extract.downd.playlist.PlaylistExtractor;
import music.player.extract.downd.search.SearchExtractor;
import music.player.extract.downd.services.soundcloud.extractors.SoundcloudChannelExtractor;
import music.player.extract.downd.services.soundcloud.extractors.SoundcloudChartsExtractor;
import music.player.extract.downd.services.soundcloud.extractors.SoundcloudCommentsExtractor;
import music.player.extract.downd.services.soundcloud.extractors.SoundcloudSearchExtractor;
import music.player.extract.downd.services.soundcloud.extractors.SoundcloudStreamExtractor;
import music.player.extract.downd.services.soundcloud.extractors.SoundcloudSuggestionExtractor;
import music.player.extract.downd.services.soundcloud.linkHandler.SoundcloudChannelLinkHandlerFactory;
import music.player.extract.downd.services.soundcloud.linkHandler.SoundcloudChartsLinkHandlerFactory;
import music.player.extract.downd.services.soundcloud.linkHandler.SoundcloudCommentsLinkHandlerFactory;
import music.player.extract.downd.services.soundcloud.linkHandler.SoundcloudPlaylistLinkHandlerFactory;
import music.player.extract.downd.services.soundcloud.linkHandler.SoundcloudSearchQueryHandlerFactory;
import music.player.extract.downd.services.soundcloud.linkHandler.SoundcloudStreamLinkHandlerFactory;
import music.player.extract.downd.stream.StreamExtractor;
import music.player.extract.downd.subscription.SubscriptionExtractor;

import static java.util.Arrays.asList;

import music.player.extract.downd.services.soundcloud.extractors.SoundcloudPlaylistExtractor;
import music.player.extract.downd.services.soundcloud.extractors.SoundcloudSubscriptionExtractor;

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

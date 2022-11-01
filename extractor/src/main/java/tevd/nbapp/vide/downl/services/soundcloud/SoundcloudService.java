package tevd.nbapp.vide.downl.services.soundcloud;

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
import tevd.nbapp.vide.downl.localization.ContentCountry;
import tevd.nbapp.vide.downl.playlist.PlaylistExtractor;
import tevd.nbapp.vide.downl.search.SearchExtractor;
import tevd.nbapp.vide.downl.services.soundcloud.extractors.SoundcloudChannelExtractor;
import tevd.nbapp.vide.downl.services.soundcloud.extractors.SoundcloudChartsExtractor;
import tevd.nbapp.vide.downl.services.soundcloud.extractors.SoundcloudCommentsExtractor;
import tevd.nbapp.vide.downl.services.soundcloud.extractors.SoundcloudSearchExtractor;
import tevd.nbapp.vide.downl.services.soundcloud.extractors.SoundcloudStreamExtractor;
import tevd.nbapp.vide.downl.services.soundcloud.extractors.SoundcloudSuggestionExtractor;
import tevd.nbapp.vide.downl.services.soundcloud.linkHandler.SoundcloudChannelLinkHandlerFactory;
import tevd.nbapp.vide.downl.services.soundcloud.linkHandler.SoundcloudChartsLinkHandlerFactory;
import tevd.nbapp.vide.downl.services.soundcloud.linkHandler.SoundcloudCommentsLinkHandlerFactory;
import tevd.nbapp.vide.downl.services.soundcloud.linkHandler.SoundcloudPlaylistLinkHandlerFactory;
import tevd.nbapp.vide.downl.services.soundcloud.linkHandler.SoundcloudSearchQueryHandlerFactory;
import tevd.nbapp.vide.downl.services.soundcloud.linkHandler.SoundcloudStreamLinkHandlerFactory;
import tevd.nbapp.vide.downl.stream.StreamExtractor;
import tevd.nbapp.vide.downl.subscription.SubscriptionExtractor;

import static java.util.Arrays.asList;

import tevd.nbapp.vide.downl.services.soundcloud.extractors.SoundcloudPlaylistExtractor;
import tevd.nbapp.vide.downl.services.soundcloud.extractors.SoundcloudSubscriptionExtractor;

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

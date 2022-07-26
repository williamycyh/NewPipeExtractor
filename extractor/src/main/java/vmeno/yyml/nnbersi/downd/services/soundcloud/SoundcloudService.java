package vmeno.yyml.nnbersi.downd.services.soundcloud;

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
import vmeno.yyml.nnbersi.downd.localization.ContentCountry;
import vmeno.yyml.nnbersi.downd.playlist.PlaylistExtractor;
import vmeno.yyml.nnbersi.downd.search.SearchExtractor;
import vmeno.yyml.nnbersi.downd.services.soundcloud.extractors.SoundcloudChannelExtractor;
import vmeno.yyml.nnbersi.downd.services.soundcloud.extractors.SoundcloudChartsExtractor;
import vmeno.yyml.nnbersi.downd.services.soundcloud.extractors.SoundcloudCommentsExtractor;
import vmeno.yyml.nnbersi.downd.services.soundcloud.extractors.SoundcloudSearchExtractor;
import vmeno.yyml.nnbersi.downd.services.soundcloud.extractors.SoundcloudStreamExtractor;
import vmeno.yyml.nnbersi.downd.services.soundcloud.extractors.SoundcloudSuggestionExtractor;
import vmeno.yyml.nnbersi.downd.services.soundcloud.linkHandler.SoundcloudChannelLinkHandlerFactory;
import vmeno.yyml.nnbersi.downd.services.soundcloud.linkHandler.SoundcloudChartsLinkHandlerFactory;
import vmeno.yyml.nnbersi.downd.services.soundcloud.linkHandler.SoundcloudCommentsLinkHandlerFactory;
import vmeno.yyml.nnbersi.downd.services.soundcloud.linkHandler.SoundcloudPlaylistLinkHandlerFactory;
import vmeno.yyml.nnbersi.downd.services.soundcloud.linkHandler.SoundcloudSearchQueryHandlerFactory;
import vmeno.yyml.nnbersi.downd.services.soundcloud.linkHandler.SoundcloudStreamLinkHandlerFactory;
import vmeno.yyml.nnbersi.downd.stream.StreamExtractor;
import vmeno.yyml.nnbersi.downd.subscription.SubscriptionExtractor;

import static java.util.Arrays.asList;

import vmeno.yyml.nnbersi.downd.services.soundcloud.extractors.SoundcloudPlaylistExtractor;
import vmeno.yyml.nnbersi.downd.services.soundcloud.extractors.SoundcloudSubscriptionExtractor;

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

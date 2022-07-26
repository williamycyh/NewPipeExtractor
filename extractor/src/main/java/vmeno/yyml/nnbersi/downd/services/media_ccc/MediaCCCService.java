package vmeno.yyml.nnbersi.downd.services.media_ccc;

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
import vmeno.yyml.nnbersi.downd.services.media_ccc.extractors.MediaCCCConferenceExtractor;
import vmeno.yyml.nnbersi.downd.services.media_ccc.extractors.MediaCCCConferenceKiosk;
import vmeno.yyml.nnbersi.downd.services.media_ccc.extractors.MediaCCCLiveStreamKiosk;
import vmeno.yyml.nnbersi.downd.services.media_ccc.extractors.MediaCCCParsingHelper;
import vmeno.yyml.nnbersi.downd.services.media_ccc.extractors.MediaCCCRecentKiosk;
import vmeno.yyml.nnbersi.downd.services.media_ccc.extractors.MediaCCCSearchExtractor;
import vmeno.yyml.nnbersi.downd.stream.StreamExtractor;
import vmeno.yyml.nnbersi.downd.subscription.SubscriptionExtractor;
import vmeno.yyml.nnbersi.downd.suggestion.SuggestionExtractor;

import static java.util.Arrays.asList;

import vmeno.yyml.nnbersi.downd.services.media_ccc.extractors.MediaCCCLiveStreamExtractor;
import vmeno.yyml.nnbersi.downd.services.media_ccc.extractors.MediaCCCStreamExtractor;
import vmeno.yyml.nnbersi.downd.services.media_ccc.linkHandler.MediaCCCConferenceLinkHandlerFactory;
import vmeno.yyml.nnbersi.downd.services.media_ccc.linkHandler.MediaCCCConferencesListLinkHandlerFactory;
import vmeno.yyml.nnbersi.downd.services.media_ccc.linkHandler.MediaCCCLiveListLinkHandlerFactory;
import vmeno.yyml.nnbersi.downd.services.media_ccc.linkHandler.MediaCCCRecentListLinkHandlerFactory;
import vmeno.yyml.nnbersi.downd.services.media_ccc.linkHandler.MediaCCCSearchQueryHandlerFactory;
import vmeno.yyml.nnbersi.downd.services.media_ccc.linkHandler.MediaCCCStreamLinkHandlerFactory;

public class MediaCCCService extends StreamingService {
    public MediaCCCService(final int id) {
        super(id, "media.ccc.de", asList(ServiceInfo.MediaCapability.AUDIO, ServiceInfo.MediaCapability.VIDEO));
    }

    @Override
    public SearchExtractor getSearchExtractor(final SearchQueryHandler query) {
        return new MediaCCCSearchExtractor(this, query);
    }

    @Override
    public LinkHandlerFactory getStreamLHFactory() {
        return new MediaCCCStreamLinkHandlerFactory();
    }

    @Override
    public ListLinkHandlerFactory getChannelLHFactory() {
        return new MediaCCCConferenceLinkHandlerFactory();
    }

    @Override
    public ListLinkHandlerFactory getPlaylistLHFactory() {
        return null;
    }

    @Override
    public SearchQueryHandlerFactory getSearchQHFactory() {
        return new MediaCCCSearchQueryHandlerFactory();
    }

    @Override
    public StreamExtractor getStreamExtractor(final LinkHandler linkHandler) {
        if (MediaCCCParsingHelper.isLiveStreamId(linkHandler.getId())) {
            return new MediaCCCLiveStreamExtractor(this, linkHandler);
        }
        return new MediaCCCStreamExtractor(this, linkHandler);
    }

    @Override
    public ChannelExtractor getChannelExtractor(final ListLinkHandler linkHandler) {
        return new MediaCCCConferenceExtractor(this, linkHandler);
    }

    @Override
    public PlaylistExtractor getPlaylistExtractor(final ListLinkHandler linkHandler) {
        return null;
    }

    @Override
    public SuggestionExtractor getSuggestionExtractor() {
        return null;
    }

    @Override
    public KioskList getKioskList() throws ExtractionException {
        final KioskList list = new KioskList(this);

        // add kiosks here e.g.:
        try {
            list.addKioskEntry(
                    (streamingService, url, kioskId) -> new MediaCCCConferenceKiosk(
                            MediaCCCService.this,
                            new MediaCCCConferencesListLinkHandlerFactory().fromUrl(url),
                            kioskId
                    ),
                    new MediaCCCConferencesListLinkHandlerFactory(),
                    "conferences"
            );

            list.addKioskEntry(
                    (streamingService, url, kioskId) -> new MediaCCCRecentKiosk(
                            MediaCCCService.this,
                            new MediaCCCRecentListLinkHandlerFactory().fromUrl(url),
                            kioskId
                    ),
                    new MediaCCCRecentListLinkHandlerFactory(),
                    "recent"
            );

            list.addKioskEntry(
                    (streamingService, url, kioskId) -> new MediaCCCLiveStreamKiosk(
                            MediaCCCService.this,
                            new MediaCCCLiveListLinkHandlerFactory().fromUrl(url),
                            kioskId
                    ),
                    new MediaCCCLiveListLinkHandlerFactory(),
                    "live"
            );

            list.setDefaultKiosk("recent");
        } catch (final Exception e) {
            throw new ExtractionException(e);
        }

        return list;
    }

    @Override
    public SubscriptionExtractor getSubscriptionExtractor() {
        return null;
    }

    @Override
    public ListLinkHandlerFactory getCommentsLHFactory() {
        return null;
    }

    @Override
    public CommentsExtractor getCommentsExtractor(final ListLinkHandler linkHandler) {
        return null;
    }

    @Override
    public String getBaseUrl() {
        return "https://media.ccc.de";
    }

}

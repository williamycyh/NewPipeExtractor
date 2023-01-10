package tndown.tndir.simplevd.vdwload.services.media_ccc;

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
import tndown.tndir.simplevd.vdwload.services.media_ccc.extractors.MediaCCCConferenceExtractor;
import tndown.tndir.simplevd.vdwload.services.media_ccc.extractors.MediaCCCConferenceKiosk;
import tndown.tndir.simplevd.vdwload.services.media_ccc.extractors.MediaCCCLiveStreamKiosk;
import tndown.tndir.simplevd.vdwload.services.media_ccc.extractors.MediaCCCParsingHelper;
import tndown.tndir.simplevd.vdwload.services.media_ccc.extractors.MediaCCCRecentKiosk;
import tndown.tndir.simplevd.vdwload.services.media_ccc.extractors.MediaCCCSearchExtractor;
import tndown.tndir.simplevd.vdwload.stream.StreamExtractor;
import tndown.tndir.simplevd.vdwload.subscription.SubscriptionExtractor;
import tndown.tndir.simplevd.vdwload.suggestion.SuggestionExtractor;

import static java.util.Arrays.asList;

import tndown.tndir.simplevd.vdwload.services.media_ccc.extractors.MediaCCCLiveStreamExtractor;
import tndown.tndir.simplevd.vdwload.services.media_ccc.extractors.MediaCCCStreamExtractor;
import tndown.tndir.simplevd.vdwload.services.media_ccc.linkHandler.MediaCCCConferenceLinkHandlerFactory;
import tndown.tndir.simplevd.vdwload.services.media_ccc.linkHandler.MediaCCCConferencesListLinkHandlerFactory;
import tndown.tndir.simplevd.vdwload.services.media_ccc.linkHandler.MediaCCCLiveListLinkHandlerFactory;
import tndown.tndir.simplevd.vdwload.services.media_ccc.linkHandler.MediaCCCRecentListLinkHandlerFactory;
import tndown.tndir.simplevd.vdwload.services.media_ccc.linkHandler.MediaCCCSearchQueryHandlerFactory;
import tndown.tndir.simplevd.vdwload.services.media_ccc.linkHandler.MediaCCCStreamLinkHandlerFactory;

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

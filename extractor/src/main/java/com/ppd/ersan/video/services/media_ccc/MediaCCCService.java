package com.ppd.ersan.video.services.media_ccc;

import com.ppd.ersan.video.StreamingService;
import com.ppd.ersan.video.channel.ChannelExtractor;
import com.ppd.ersan.video.comments.CommentsExtractor;
import com.ppd.ersan.video.exceptions.ExtractionException;
import com.ppd.ersan.video.kiosk.KioskList;
import com.ppd.ersan.video.linkhandler.LinkHandler;
import com.ppd.ersan.video.linkhandler.LinkHandlerFactory;
import com.ppd.ersan.video.linkhandler.ListLinkHandler;
import com.ppd.ersan.video.linkhandler.ListLinkHandlerFactory;
import com.ppd.ersan.video.linkhandler.SearchQueryHandler;
import com.ppd.ersan.video.linkhandler.SearchQueryHandlerFactory;
import com.ppd.ersan.video.playlist.PlaylistExtractor;
import com.ppd.ersan.video.search.SearchExtractor;
import com.ppd.ersan.video.services.media_ccc.extractors.MediaCCCConferenceExtractor;
import com.ppd.ersan.video.services.media_ccc.extractors.MediaCCCConferenceKiosk;
import com.ppd.ersan.video.services.media_ccc.extractors.MediaCCCLiveStreamKiosk;
import com.ppd.ersan.video.services.media_ccc.extractors.MediaCCCParsingHelper;
import com.ppd.ersan.video.services.media_ccc.extractors.MediaCCCRecentKiosk;
import com.ppd.ersan.video.services.media_ccc.extractors.MediaCCCSearchExtractor;
import com.ppd.ersan.video.stream.StreamExtractor;
import com.ppd.ersan.video.subscription.SubscriptionExtractor;
import com.ppd.ersan.video.suggestion.SuggestionExtractor;

import static java.util.Arrays.asList;

import com.ppd.ersan.video.services.media_ccc.extractors.MediaCCCLiveStreamExtractor;
import com.ppd.ersan.video.services.media_ccc.extractors.MediaCCCStreamExtractor;
import com.ppd.ersan.video.services.media_ccc.linkHandler.MediaCCCConferenceLinkHandlerFactory;
import com.ppd.ersan.video.services.media_ccc.linkHandler.MediaCCCConferencesListLinkHandlerFactory;
import com.ppd.ersan.video.services.media_ccc.linkHandler.MediaCCCLiveListLinkHandlerFactory;
import com.ppd.ersan.video.services.media_ccc.linkHandler.MediaCCCRecentListLinkHandlerFactory;
import com.ppd.ersan.video.services.media_ccc.linkHandler.MediaCCCSearchQueryHandlerFactory;
import com.ppd.ersan.video.services.media_ccc.linkHandler.MediaCCCStreamLinkHandlerFactory;

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

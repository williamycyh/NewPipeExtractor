package com.downloader.twotwo.video.services.media_ccc;

import com.downloader.twotwo.video.StreamingService;
import com.downloader.twotwo.video.channel.ChannelExtractor;
import com.downloader.twotwo.video.comments.CommentsExtractor;
import com.downloader.twotwo.video.exceptions.ExtractionException;
import com.downloader.twotwo.video.kiosk.KioskList;
import com.downloader.twotwo.video.linkhandler.LinkHandler;
import com.downloader.twotwo.video.linkhandler.LinkHandlerFactory;
import com.downloader.twotwo.video.linkhandler.ListLinkHandler;
import com.downloader.twotwo.video.linkhandler.ListLinkHandlerFactory;
import com.downloader.twotwo.video.linkhandler.SearchQueryHandler;
import com.downloader.twotwo.video.linkhandler.SearchQueryHandlerFactory;
import com.downloader.twotwo.video.playlist.PlaylistExtractor;
import com.downloader.twotwo.video.search.SearchExtractor;
import com.downloader.twotwo.video.services.media_ccc.extractors.MediaCCCConferenceExtractor;
import com.downloader.twotwo.video.services.media_ccc.extractors.MediaCCCConferenceKiosk;
import com.downloader.twotwo.video.services.media_ccc.extractors.MediaCCCLiveStreamKiosk;
import com.downloader.twotwo.video.services.media_ccc.extractors.MediaCCCParsingHelper;
import com.downloader.twotwo.video.services.media_ccc.extractors.MediaCCCRecentKiosk;
import com.downloader.twotwo.video.services.media_ccc.extractors.MediaCCCSearchExtractor;
import com.downloader.twotwo.video.stream.StreamExtractor;
import com.downloader.twotwo.video.subscription.SubscriptionExtractor;
import com.downloader.twotwo.video.suggestion.SuggestionExtractor;

import static java.util.Arrays.asList;

import com.downloader.twotwo.video.services.media_ccc.extractors.MediaCCCLiveStreamExtractor;
import com.downloader.twotwo.video.services.media_ccc.extractors.MediaCCCStreamExtractor;
import com.downloader.twotwo.video.services.media_ccc.linkHandler.MediaCCCConferenceLinkHandlerFactory;
import com.downloader.twotwo.video.services.media_ccc.linkHandler.MediaCCCConferencesListLinkHandlerFactory;
import com.downloader.twotwo.video.services.media_ccc.linkHandler.MediaCCCLiveListLinkHandlerFactory;
import com.downloader.twotwo.video.services.media_ccc.linkHandler.MediaCCCRecentListLinkHandlerFactory;
import com.downloader.twotwo.video.services.media_ccc.linkHandler.MediaCCCSearchQueryHandlerFactory;
import com.downloader.twotwo.video.services.media_ccc.linkHandler.MediaCCCStreamLinkHandlerFactory;

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

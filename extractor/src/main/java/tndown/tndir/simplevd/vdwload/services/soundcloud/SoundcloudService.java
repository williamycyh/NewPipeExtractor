package tndown.tndir.simplevd.vdwload.services.soundcloud;

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
import tndown.tndir.simplevd.vdwload.localization.ContentCountry;
import tndown.tndir.simplevd.vdwload.playlist.PlaylistExtractor;
import tndown.tndir.simplevd.vdwload.search.SearchExtractor;
import tndown.tndir.simplevd.vdwload.services.soundcloud.extractors.SoundcloudChannelExtractor;
import tndown.tndir.simplevd.vdwload.services.soundcloud.extractors.SoundcloudChartsExtractor;
import tndown.tndir.simplevd.vdwload.services.soundcloud.extractors.SoundcloudCommentsExtractor;
import tndown.tndir.simplevd.vdwload.services.soundcloud.extractors.SoundcloudSearchExtractor;
import tndown.tndir.simplevd.vdwload.services.soundcloud.extractors.SoundcloudStreamExtractor;
import tndown.tndir.simplevd.vdwload.services.soundcloud.extractors.SoundcloudSuggestionExtractor;
import tndown.tndir.simplevd.vdwload.services.soundcloud.linkHandler.SoundcloudChannelLinkHandlerFactory;
import tndown.tndir.simplevd.vdwload.services.soundcloud.linkHandler.SoundcloudChartsLinkHandlerFactory;
import tndown.tndir.simplevd.vdwload.services.soundcloud.linkHandler.SoundcloudCommentsLinkHandlerFactory;
import tndown.tndir.simplevd.vdwload.services.soundcloud.linkHandler.SoundcloudPlaylistLinkHandlerFactory;
import tndown.tndir.simplevd.vdwload.services.soundcloud.linkHandler.SoundcloudSearchQueryHandlerFactory;
import tndown.tndir.simplevd.vdwload.services.soundcloud.linkHandler.SoundcloudStreamLinkHandlerFactory;
import tndown.tndir.simplevd.vdwload.stream.StreamExtractor;
import tndown.tndir.simplevd.vdwload.subscription.SubscriptionExtractor;

import static java.util.Arrays.asList;

import tndown.tndir.simplevd.vdwload.services.soundcloud.extractors.SoundcloudPlaylistExtractor;
import tndown.tndir.simplevd.vdwload.services.soundcloud.extractors.SoundcloudSubscriptionExtractor;

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

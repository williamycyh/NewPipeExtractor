package tndown.tndir.simplevd.vdwload.services.peertube.extractors;

import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import tndown.tndir.simplevd.vdwload.InfoItem;
import tndown.tndir.simplevd.vdwload.ListExtractor;
import tndown.tndir.simplevd.vdwload.MetaInfo;
import tndown.tndir.simplevd.vdwload.MultiInfoItemsCollector;
import tndown.tndir.simplevd.vdwload.Page;
import tndown.tndir.simplevd.vdwload.StreamingService;
import tndown.tndir.simplevd.vdwload.downloader.Downloader;
import tndown.tndir.simplevd.vdwload.downloader.Response;
import tndown.tndir.simplevd.vdwload.exceptions.ExtractionException;
import tndown.tndir.simplevd.vdwload.exceptions.ParsingException;
import tndown.tndir.simplevd.vdwload.linkhandler.SearchQueryHandler;
import tndown.tndir.simplevd.vdwload.search.SearchExtractor;
import tndown.tndir.simplevd.vdwload.services.peertube.PeertubeParsingHelper;
import tndown.tndir.simplevd.vdwload.Utils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import static tndown.tndir.simplevd.vdwload.services.peertube.PeertubeParsingHelper.collectStreamsFrom;
import static tndown.tndir.simplevd.vdwload.Utils.isNullOrEmpty;

public class PeertubeSearchExtractor extends SearchExtractor {

    // if we should use PeertubeSepiaStreamInfoItemExtractor
    private final boolean sepia;

    public PeertubeSearchExtractor(final StreamingService service,
                                   final SearchQueryHandler linkHandler) {
        this(service, linkHandler, false);
    }

    public PeertubeSearchExtractor(final StreamingService service,
                                   final SearchQueryHandler linkHandler,
                                   final boolean sepia) {
        super(service, linkHandler);
        this.sepia = sepia;
    }

    @Nonnull
    @Override
    public String getSearchSuggestion() {
        return "";
    }

    @Override
    public boolean isCorrectedSearch() {
        return false;
    }

    @Nonnull
    @Override
    public List<MetaInfo> getMetaInfo() {
        return Collections.emptyList();
    }

    @Override
    public ListExtractor.InfoItemsPage<InfoItem> getInitialPage() throws IOException, ExtractionException {
        return getPage(new Page(getUrl() + "&" + PeertubeParsingHelper.START_KEY + "=0&"
                + PeertubeParsingHelper.COUNT_KEY + "=" + PeertubeParsingHelper.ITEMS_PER_PAGE));
    }

    @Override
    public ListExtractor.InfoItemsPage<InfoItem> getPage(final Page page)
            throws IOException, ExtractionException {
        if (page == null || Utils.isNullOrEmpty(page.getUrl())) {
            throw new IllegalArgumentException("Page doesn't contain an URL");
        }

        final Response response = getDownloader().get(page.getUrl());

        JsonObject json = null;
        if (response != null && !Utils.isBlank(response.responseBody())) {
            try {
                json = JsonParser.object().from(response.responseBody());
            } catch (final Exception e) {
                throw new ParsingException("Could not parse json data for search info", e);
            }
        }

        if (json != null) {
            PeertubeParsingHelper.validate(json);
            final long total = json.getLong("total");

            final MultiInfoItemsCollector collector = new MultiInfoItemsCollector(getServiceId());
            PeertubeParsingHelper.collectStreamsFrom(collector, json, getBaseUrl(), sepia);

            return new ListExtractor.InfoItemsPage<>(collector,
                    PeertubeParsingHelper.getNextPage(page.getUrl(), total));
        } else {
            throw new ExtractionException("Unable to get PeerTube search info");
        }
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader)
            throws IOException, ExtractionException {
    }
}

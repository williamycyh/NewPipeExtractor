// Created by Fynn Godau 2019, licensed GNU GPL version 3 or later

package tndown.tndir.simplevd.vdwload.services.bandcamp.extractors;

import edu.umd.cs.findbugs.annotations.NonNull;
import tndown.tndir.simplevd.vdwload.Page;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import tndown.tndir.simplevd.vdwload.InfoItem;
import tndown.tndir.simplevd.vdwload.MetaInfo;
import tndown.tndir.simplevd.vdwload.StreamingService;
import tndown.tndir.simplevd.vdwload.downloader.Downloader;
import tndown.tndir.simplevd.vdwload.exceptions.ExtractionException;
import tndown.tndir.simplevd.vdwload.exceptions.ParsingException;
import tndown.tndir.simplevd.vdwload.linkhandler.SearchQueryHandler;
import tndown.tndir.simplevd.vdwload.MultiInfoItemsCollector;
import tndown.tndir.simplevd.vdwload.search.SearchExtractor;
import tndown.tndir.simplevd.vdwload.services.bandcamp.extractors.streaminfoitem.BandcampSearchStreamInfoItemExtractor;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class BandcampSearchExtractor extends SearchExtractor {

    public BandcampSearchExtractor(final StreamingService service,
                                   final SearchQueryHandler linkHandler) {
        super(service, linkHandler);
    }

    @NonNull
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
    public List<MetaInfo> getMetaInfo() throws ParsingException {
        return Collections.emptyList();
    }

    public InfoItemsPage<InfoItem> getPage(final Page page)
            throws IOException, ExtractionException {
        final String html = getDownloader().get(page.getUrl()).responseBody();

        final MultiInfoItemsCollector collector = new MultiInfoItemsCollector(getServiceId());


        final Document d = Jsoup.parse(html);

        final Elements searchResultsElements = d.getElementsByClass("searchresult");

        for (final Element searchResult : searchResultsElements) {

            final String type = searchResult.getElementsByClass("result-info").first()
                    .getElementsByClass("itemtype").first().text();

            switch (type) {
                default:
                    continue;
                case "FAN":
                    // don't display fan results
                    break;

                case "ARTIST":
                    collector.commit(new BandcampChannelInfoItemExtractor(searchResult));
                    break;

                case "ALBUM":
                    collector.commit(new BandcampPlaylistInfoItemExtractor(searchResult));
                    break;

                case "TRACK":
                    collector.commit(new BandcampSearchStreamInfoItemExtractor(searchResult, null));
                    break;
            }

        }

        // Count pages
        final Elements pageLists = d.getElementsByClass("pagelist");
        if (pageLists.isEmpty()) {
            return new InfoItemsPage<>(collector, null);
        }

        final Elements pages = pageLists.first().getElementsByTag("li");

        // Find current page
        int currentPage = -1;
        for (int i = 0; i < pages.size(); i++) {
            final Element pageElement = pages.get(i);
            if (!pageElement.getElementsByTag("span").isEmpty()) {
                currentPage = i + 1;
                break;
            }
        }

        // Search results appear to be capped at six pages
        assert pages.size() < 10;

        String nextUrl = null;
        if (currentPage < pages.size()) {
            nextUrl = page.getUrl().substring(0, page.getUrl().length() - 1) + (currentPage + 1);
        }

        return new InfoItemsPage<>(collector, new Page(nextUrl));

    }

    @Nonnull
    @Override
    public InfoItemsPage<InfoItem> getInitialPage() throws IOException, ExtractionException {
        return getPage(new Page(getUrl()));
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader)
            throws IOException, ExtractionException {
    }
}

package music.player.extract.downd.services.soundcloud.extractors;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import music.player.extract.downd.InfoItem;
import music.player.extract.downd.InfoItemExtractor;
import music.player.extract.downd.InfoItemsCollector;
import music.player.extract.downd.ListExtractor;
import music.player.extract.downd.MetaInfo;
import music.player.extract.downd.MultiInfoItemsCollector;
import music.player.extract.downd.Page;
import music.player.extract.downd.StreamingService;
import music.player.extract.downd.downloader.Downloader;
import music.player.extract.downd.linkhandler.SearchQueryHandler;
import music.player.extract.downd.search.SearchExtractor;
import music.player.extract.downd.services.soundcloud.linkHandler.SoundcloudSearchQueryHandlerFactory;
import music.player.extract.downd.utils.Parser;
import music.player.extract.downd.utils.Utils;

import music.player.extract.downd.exceptions.ExtractionException;
import music.player.extract.downd.exceptions.ParsingException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.function.IntUnaryOperator;

import static music.player.extract.downd.utils.Utils.isNullOrEmpty;

public class SoundcloudSearchExtractor extends SearchExtractor {
    private JsonArray initialSearchCollection;

    public SoundcloudSearchExtractor(final StreamingService service,
                                     final SearchQueryHandler linkHandler) {
        super(service, linkHandler);
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

    @Nonnull
    @Override
    public ListExtractor.InfoItemsPage<InfoItem> getInitialPage() throws IOException, ExtractionException {
        return new ListExtractor.InfoItemsPage<>(
                collectItems(initialSearchCollection),
                getNextPageFromCurrentUrl(getUrl(), currentOffset -> SoundcloudSearchQueryHandlerFactory.ITEMS_PER_PAGE));
    }

    @Override
    public ListExtractor.InfoItemsPage<InfoItem> getPage(final Page page) throws IOException,
            ExtractionException {
        if (page == null || Utils.isNullOrEmpty(page.getUrl())) {
            throw new IllegalArgumentException("Page doesn't contain an URL");
        }

        final Downloader dl = getDownloader();
        final JsonArray searchCollection;
        try {
            final String response = dl.get(page.getUrl(), getExtractorLocalization())
                    .responseBody();
            searchCollection = JsonParser.object().from(response).getArray("collection");
        } catch (final JsonParserException e) {
            throw new ParsingException("Could not parse json response", e);
        }

        return new ListExtractor.InfoItemsPage<>(collectItems(searchCollection),
                getNextPageFromCurrentUrl(page.getUrl(),
                        currentOffset -> currentOffset + SoundcloudSearchQueryHandlerFactory.ITEMS_PER_PAGE));
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader) throws IOException,
            ExtractionException {
        final Downloader dl = getDownloader();
        final String url = getUrl();
        try {
            final String response = dl.get(url, getExtractorLocalization()).responseBody();
            initialSearchCollection = JsonParser.object().from(response).getArray("collection");
        } catch (final JsonParserException e) {
            throw new ParsingException("Could not parse json response", e);
        }

        if (initialSearchCollection.isEmpty()) {
            throw new SearchExtractor.NothingFoundException("Nothing found");
        }
    }

    private InfoItemsCollector<InfoItem, InfoItemExtractor> collectItems(
            final JsonArray searchCollection) {
        final MultiInfoItemsCollector collector = new MultiInfoItemsCollector(getServiceId());

        for (final Object result : searchCollection) {
            if (!(result instanceof JsonObject)) {
                continue;
            }

            final JsonObject searchResult = (JsonObject) result;
            final String kind = searchResult.getString("kind", Utils.EMPTY_STRING);
            switch (kind) {
                case "user":
                    collector.commit(new SoundcloudChannelInfoItemExtractor(searchResult));
                    break;
                case "track":
                    collector.commit(new SoundcloudStreamInfoItemExtractor(searchResult));
                    break;
                case "playlist":
                    collector.commit(new SoundcloudPlaylistInfoItemExtractor(searchResult));
                    break;
            }
        }

        return collector;
    }

    private Page getNextPageFromCurrentUrl(final String currentUrl,
                                           final IntUnaryOperator newPageOffsetCalculator)
            throws MalformedURLException, UnsupportedEncodingException {
        final int currentPageOffset = Integer.parseInt(
                    Parser.compatParseMap(new URL(currentUrl).getQuery()).get("offset"));

        return new Page(
                currentUrl.replace(
                        "&offset=" + currentPageOffset,
                        "&offset=" + newPageOffsetCalculator.applyAsInt(currentPageOffset)));
    }
}

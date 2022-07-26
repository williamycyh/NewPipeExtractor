package vmeno.yyml.nnbersi.downd.services.soundcloud.extractors;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import vmeno.yyml.nnbersi.downd.ListExtractor;
import vmeno.yyml.nnbersi.downd.NewPipe;
import vmeno.yyml.nnbersi.downd.Page;
import vmeno.yyml.nnbersi.downd.StreamingService;
import vmeno.yyml.nnbersi.downd.comments.CommentsExtractor;
import vmeno.yyml.nnbersi.downd.comments.CommentsInfoItem;
import vmeno.yyml.nnbersi.downd.comments.CommentsInfoItemsCollector;
import vmeno.yyml.nnbersi.downd.downloader.Downloader;
import vmeno.yyml.nnbersi.downd.linkhandler.ListLinkHandler;
import vmeno.yyml.nnbersi.downd.utils.Utils;

import vmeno.yyml.nnbersi.downd.downloader.Response;
import vmeno.yyml.nnbersi.downd.exceptions.ExtractionException;
import vmeno.yyml.nnbersi.downd.exceptions.ParsingException;

import java.io.IOException;

import javax.annotation.Nonnull;

import static vmeno.yyml.nnbersi.downd.utils.Utils.isNullOrEmpty;

public class SoundcloudCommentsExtractor extends CommentsExtractor {
    public SoundcloudCommentsExtractor(final StreamingService service,
                                       final ListLinkHandler uiHandler) {
        super(service, uiHandler);
    }

    @Nonnull
    @Override
    public ListExtractor.InfoItemsPage<CommentsInfoItem> getInitialPage() throws ExtractionException,
            IOException {
        final Downloader downloader = NewPipe.getDownloader();
        final Response response = downloader.get(getUrl());

        final JsonObject json;
        try {
            json = JsonParser.object().from(response.responseBody());
        } catch (final JsonParserException e) {
            throw new ParsingException("Could not parse json", e);
        }

        final CommentsInfoItemsCollector collector = new CommentsInfoItemsCollector(
                getServiceId());

        collectStreamsFrom(collector, json.getArray("collection"));

        return new ListExtractor.InfoItemsPage<>(collector, new Page(json.getString("next_href")));
    }

    @Override
    public ListExtractor.InfoItemsPage<CommentsInfoItem> getPage(final Page page) throws ExtractionException,
            IOException {
        if (page == null || Utils.isNullOrEmpty(page.getUrl())) {
            throw new IllegalArgumentException("Page doesn't contain an URL");
        }

        final Downloader downloader = NewPipe.getDownloader();
        final Response response = downloader.get(page.getUrl());

        final JsonObject json;
        try {
            json = JsonParser.object().from(response.responseBody());
        } catch (final JsonParserException e) {
            throw new ParsingException("Could not parse json", e);
        }

        final CommentsInfoItemsCollector collector = new CommentsInfoItemsCollector(
                getServiceId());

        collectStreamsFrom(collector, json.getArray("collection"));

        return new ListExtractor.InfoItemsPage<>(collector, new Page(json.getString("next_href")));
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader) { }

    private void collectStreamsFrom(final CommentsInfoItemsCollector collector,
                                    final JsonArray entries) throws ParsingException {
        final String url = getUrl();
        for (final Object comment : entries) {
            collector.commit(new SoundcloudCommentsInfoItemExtractor((JsonObject) comment, url));
        }
    }
}

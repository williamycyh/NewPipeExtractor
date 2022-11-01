package tevd.nbapp.vide.downl.services.soundcloud.extractors;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import tevd.nbapp.vide.downl.ListExtractor;
import tevd.nbapp.vide.downl.NewPipe;
import tevd.nbapp.vide.downl.Page;
import tevd.nbapp.vide.downl.StreamingService;
import tevd.nbapp.vide.downl.comments.CommentsExtractor;
import tevd.nbapp.vide.downl.comments.CommentsInfoItem;
import tevd.nbapp.vide.downl.comments.CommentsInfoItemsCollector;
import tevd.nbapp.vide.downl.downloader.Downloader;
import tevd.nbapp.vide.downl.linkhandler.ListLinkHandler;
import tevd.nbapp.vide.downl.utils.Utils;

import tevd.nbapp.vide.downl.downloader.Response;
import tevd.nbapp.vide.downl.exceptions.ExtractionException;
import tevd.nbapp.vide.downl.exceptions.ParsingException;

import java.io.IOException;

import javax.annotation.Nonnull;

import static tevd.nbapp.vide.downl.utils.Utils.isNullOrEmpty;

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

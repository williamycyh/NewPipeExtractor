package vmeno.yyml.nnbersi.downd.services.youtube.extractors;

import vmeno.yyml.nnbersi.downd.services.youtube.YoutubeParsingHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import vmeno.yyml.nnbersi.downd.ListExtractor;
import vmeno.yyml.nnbersi.downd.Page;
import vmeno.yyml.nnbersi.downd.StreamingService;
import vmeno.yyml.nnbersi.downd.downloader.Downloader;
import vmeno.yyml.nnbersi.downd.downloader.Response;
import vmeno.yyml.nnbersi.downd.exceptions.ContentNotAvailableException;
import vmeno.yyml.nnbersi.downd.exceptions.ExtractionException;
import vmeno.yyml.nnbersi.downd.feed.FeedExtractor;
import vmeno.yyml.nnbersi.downd.linkhandler.ListLinkHandler;
import vmeno.yyml.nnbersi.downd.stream.StreamInfoItem;
import vmeno.yyml.nnbersi.downd.stream.StreamInfoItemsCollector;

import java.io.IOException;

import javax.annotation.Nonnull;

public class YoutubeFeedExtractor extends FeedExtractor {
    public YoutubeFeedExtractor(final StreamingService service, final ListLinkHandler linkHandler) {
        super(service, linkHandler);
    }

    private Document document;

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader)
            throws IOException, ExtractionException {
        final String channelIdOrUser = getLinkHandler().getId();
        final String feedUrl = YoutubeParsingHelper.getFeedUrlFrom(channelIdOrUser);

        final Response response = downloader.get(feedUrl);
        if (response.responseCode() == 404) {
            throw new ContentNotAvailableException("Could not get feed: 404 - not found");
        }
        document = Jsoup.parse(response.responseBody());
    }

    @Nonnull
    @Override
    public ListExtractor.InfoItemsPage<StreamInfoItem> getInitialPage() {
        final Elements entries = document.select("feed > entry");
        final StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());

        for (final Element entryElement : entries) {
            collector.commit(new YoutubeFeedInfoItemExtractor(entryElement));
        }

        return new InfoItemsPage<>(collector, null);
    }

    @Nonnull
    @Override
    public String getId() {
        return document.getElementsByTag("yt:channelId").first().text();
    }

    @Nonnull
    @Override
    public String getUrl() {
        return document.select("feed > author > uri").first().text();
    }

    @Nonnull
    @Override
    public String getName() {
        return document.select("feed > author > name").first().text();
    }

    @Override
    public InfoItemsPage<StreamInfoItem> getPage(final Page page) {
        return InfoItemsPage.emptyPage();
    }
}

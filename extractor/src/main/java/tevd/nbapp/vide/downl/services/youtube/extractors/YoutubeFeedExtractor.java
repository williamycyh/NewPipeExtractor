package tevd.nbapp.vide.downl.services.youtube.extractors;

import tevd.nbapp.vide.downl.services.youtube.YoutubeParsingHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import tevd.nbapp.vide.downl.ListExtractor;
import tevd.nbapp.vide.downl.Page;
import tevd.nbapp.vide.downl.StreamingService;
import tevd.nbapp.vide.downl.downloader.Downloader;
import tevd.nbapp.vide.downl.downloader.Response;
import tevd.nbapp.vide.downl.exceptions.ContentNotAvailableException;
import tevd.nbapp.vide.downl.exceptions.ExtractionException;
import tevd.nbapp.vide.downl.feed.FeedExtractor;
import tevd.nbapp.vide.downl.linkhandler.ListLinkHandler;
import tevd.nbapp.vide.downl.stream.StreamInfoItem;
import tevd.nbapp.vide.downl.stream.StreamInfoItemsCollector;

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

        return new ListExtractor.InfoItemsPage<>(collector, null);
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
    public ListExtractor.InfoItemsPage<StreamInfoItem> getPage(final Page page) {
        return ListExtractor.InfoItemsPage.emptyPage();
    }
}

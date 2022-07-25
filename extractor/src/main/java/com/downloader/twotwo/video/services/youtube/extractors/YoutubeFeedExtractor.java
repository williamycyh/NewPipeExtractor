package com.downloader.twotwo.video.services.youtube.extractors;

import com.downloader.twotwo.video.services.youtube.YoutubeParsingHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.downloader.twotwo.video.ListExtractor;
import com.downloader.twotwo.video.Page;
import com.downloader.twotwo.video.StreamingService;
import com.downloader.twotwo.video.downloader.Downloader;
import com.downloader.twotwo.video.downloader.Response;
import com.downloader.twotwo.video.exceptions.ContentNotAvailableException;
import com.downloader.twotwo.video.exceptions.ExtractionException;
import com.downloader.twotwo.video.feed.FeedExtractor;
import com.downloader.twotwo.video.linkhandler.ListLinkHandler;
import com.downloader.twotwo.video.stream.StreamInfoItem;
import com.downloader.twotwo.video.stream.StreamInfoItemsCollector;

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

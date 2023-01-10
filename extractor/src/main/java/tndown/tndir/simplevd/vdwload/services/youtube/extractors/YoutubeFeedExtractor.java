package tndown.tndir.simplevd.vdwload.services.youtube.extractors;

import tndown.tndir.simplevd.vdwload.services.youtube.YoutubeParsingHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import tndown.tndir.simplevd.vdwload.ListExtractor;
import tndown.tndir.simplevd.vdwload.Page;
import tndown.tndir.simplevd.vdwload.StreamingService;
import tndown.tndir.simplevd.vdwload.downloader.Downloader;
import tndown.tndir.simplevd.vdwload.downloader.Response;
import tndown.tndir.simplevd.vdwload.exceptions.ContentNotAvailableException;
import tndown.tndir.simplevd.vdwload.exceptions.ExtractionException;
import tndown.tndir.simplevd.vdwload.feed.FeedExtractor;
import tndown.tndir.simplevd.vdwload.linkhandler.ListLinkHandler;
import tndown.tndir.simplevd.vdwload.stream.StreamInfoItem;
import tndown.tndir.simplevd.vdwload.stream.StreamInfoItemsCollector;

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

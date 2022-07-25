package com.ppd.ersan.video.services.bandcamp.extractors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.ppd.ersan.video.Page;
import com.ppd.ersan.video.StreamingService;
import com.ppd.ersan.video.comments.CommentsExtractor;
import com.ppd.ersan.video.comments.CommentsInfoItem;
import com.ppd.ersan.video.comments.CommentsInfoItemsCollector;
import com.ppd.ersan.video.downloader.Downloader;
import com.ppd.ersan.video.exceptions.ExtractionException;
import com.ppd.ersan.video.linkhandler.ListLinkHandler;

import javax.annotation.Nonnull;
import java.io.IOException;

public class BandcampCommentsExtractor extends CommentsExtractor {

    private Document document;


    public BandcampCommentsExtractor(final StreamingService service,
                                     final ListLinkHandler linkHandler) {
        super(service, linkHandler);
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader)
            throws IOException, ExtractionException {
        document = Jsoup.parse(downloader.get(getLinkHandler().getUrl()).responseBody());
    }

    @Nonnull
    @Override
    public InfoItemsPage<CommentsInfoItem> getInitialPage()
            throws IOException, ExtractionException {

        final CommentsInfoItemsCollector collector = new CommentsInfoItemsCollector(getServiceId());

        final Elements writings = document.getElementsByClass("writing");

        for (final Element writing : writings) {
            collector.commit(new BandcampCommentsInfoItemExtractor(writing, getUrl()));
        }

        return new InfoItemsPage<>(collector, null);
    }

    @Override
    public InfoItemsPage<CommentsInfoItem> getPage(final Page page)
            throws IOException, ExtractionException {
        return null;
    }
}

package com.ppd.ersan.video.feed;

import com.ppd.ersan.video.ListExtractor;
import com.ppd.ersan.video.ListInfo;
import com.ppd.ersan.video.NewPipe;
import com.ppd.ersan.video.StreamingService;
import com.ppd.ersan.video.stream.StreamInfoItem;
import com.ppd.ersan.video.utils.ExtractorHelper;
import com.ppd.ersan.video.exceptions.ExtractionException;

import java.io.IOException;
import java.util.List;

public class FeedInfo extends ListInfo<StreamInfoItem> {

    public FeedInfo(final int serviceId,
                    final String id,
                    final String url,
                    final String originalUrl,
                    final String name,
                    final List<String> contentFilter,
                    final String sortFilter) {
        super(serviceId, id, url, originalUrl, name, contentFilter, sortFilter);
    }

    public static FeedInfo getInfo(final String url) throws IOException, ExtractionException {
        return getInfo(NewPipe.getServiceByUrl(url), url);
    }

    public static FeedInfo getInfo(final StreamingService service, final String url)
            throws IOException, ExtractionException {
        final FeedExtractor extractor = service.getFeedExtractor(url);

        if (extractor == null) {
            throw new IllegalArgumentException("Service \"" + service.getServiceInfo().getName()
                    + "\" doesn't support FeedExtractor.");
        }

        extractor.fetchPage();
        return getInfo(extractor);
    }

    public static FeedInfo getInfo(final FeedExtractor extractor)
            throws IOException, ExtractionException {
        extractor.fetchPage();

        final int serviceId = extractor.getServiceId();
        final String id = extractor.getId();
        final String url = extractor.getUrl();
        final String originalUrl = extractor.getOriginalUrl();
        final String name = extractor.getName();

        final FeedInfo info = new FeedInfo(serviceId, id, url, originalUrl, name, null, null);

        final ListExtractor.InfoItemsPage<StreamInfoItem> itemsPage
                = ExtractorHelper.getItemsPageOrLogError(info, extractor);
        info.setRelatedItems(itemsPage.getItems());
        info.setNextPage(itemsPage.getNextPage());

        return info;
    }
}

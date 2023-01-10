package tndown.tndir.simplevd.vdwload.feed;

import tndown.tndir.simplevd.vdwload.ListExtractor;
import tndown.tndir.simplevd.vdwload.ListInfo;
import tndown.tndir.simplevd.vdwload.NewPipe;
import tndown.tndir.simplevd.vdwload.StreamingService;
import tndown.tndir.simplevd.vdwload.stream.StreamInfoItem;
import tndown.tndir.simplevd.vdwload.utils.ExtractorHelper;
import tndown.tndir.simplevd.vdwload.exceptions.ExtractionException;

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

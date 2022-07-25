package com.downloader.twotwo.video.feed;

import com.downloader.twotwo.video.ListExtractor;
import com.downloader.twotwo.video.StreamingService;
import com.downloader.twotwo.video.stream.StreamInfoItem;
import com.downloader.twotwo.video.linkhandler.ListLinkHandler;

/**
 * This class helps to extract items from lightweight feeds that the services may provide.
 * <p>
 * YouTube is an example of a service that has this alternative available.
 */
public abstract class FeedExtractor extends ListExtractor<StreamInfoItem> {
    public FeedExtractor(final StreamingService service, final ListLinkHandler listLinkHandler) {
        super(service, listLinkHandler);
    }
}

package com.github.video.downloader.feed;

import com.github.video.downloader.ListExtractor;
import com.github.video.downloader.StreamingService;
import com.github.video.downloader.stream.StreamInfoItem;
import com.github.video.downloader.linkhandler.ListLinkHandler;

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

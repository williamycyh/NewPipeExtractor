package com.ppd.ersan.video.feed;

import com.ppd.ersan.video.ListExtractor;
import com.ppd.ersan.video.StreamingService;
import com.ppd.ersan.video.stream.StreamInfoItem;
import com.ppd.ersan.video.linkhandler.ListLinkHandler;

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

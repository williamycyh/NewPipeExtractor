package tndown.tndir.simplevd.vdwload.feed;

import tndown.tndir.simplevd.vdwload.ListExtractor;
import tndown.tndir.simplevd.vdwload.StreamingService;
import tndown.tndir.simplevd.vdwload.stream.StreamInfoItem;
import tndown.tndir.simplevd.vdwload.linkhandler.ListLinkHandler;

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

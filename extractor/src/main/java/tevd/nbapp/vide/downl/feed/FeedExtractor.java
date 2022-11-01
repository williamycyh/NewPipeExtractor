package tevd.nbapp.vide.downl.feed;

import tevd.nbapp.vide.downl.ListExtractor;
import tevd.nbapp.vide.downl.StreamingService;
import tevd.nbapp.vide.downl.stream.StreamInfoItem;
import tevd.nbapp.vide.downl.linkhandler.ListLinkHandler;

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

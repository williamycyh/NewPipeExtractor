package music.player.extract.downd.feed;

import music.player.extract.downd.ListExtractor;
import music.player.extract.downd.StreamingService;
import music.player.extract.downd.stream.StreamInfoItem;
import music.player.extract.downd.linkhandler.ListLinkHandler;

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

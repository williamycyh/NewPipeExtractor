package vmeno.yyml.nnbersi.downd.feed;

import vmeno.yyml.nnbersi.downd.ListExtractor;
import vmeno.yyml.nnbersi.downd.StreamingService;
import vmeno.yyml.nnbersi.downd.stream.StreamInfoItem;
import vmeno.yyml.nnbersi.downd.linkhandler.ListLinkHandler;

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

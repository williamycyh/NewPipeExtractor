package vmeno.yyml.nnbersi.downd.comments;

import vmeno.yyml.nnbersi.downd.ListExtractor;
import vmeno.yyml.nnbersi.downd.StreamingService;
import vmeno.yyml.nnbersi.downd.exceptions.ExtractionException;
import vmeno.yyml.nnbersi.downd.exceptions.ParsingException;
import vmeno.yyml.nnbersi.downd.linkhandler.ListLinkHandler;

import javax.annotation.Nonnull;

public abstract class CommentsExtractor extends ListExtractor<CommentsInfoItem> {

    public CommentsExtractor(final StreamingService service, final ListLinkHandler uiHandler) {
        super(service, uiHandler);
    }

    /**
     * @apiNote Warning: This method is experimental and may get removed in a future release.
     * @return <code>true</code> if the comments are disabled otherwise <code>false</code> (default)
     */
    public boolean isCommentsDisabled() throws ExtractionException {
        return false;
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        return "Comments";
    }
}

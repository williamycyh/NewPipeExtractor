package music.player.extract.downd.comments;

import music.player.extract.downd.ListExtractor;
import music.player.extract.downd.StreamingService;
import music.player.extract.downd.exceptions.ExtractionException;
import music.player.extract.downd.exceptions.ParsingException;
import music.player.extract.downd.linkhandler.ListLinkHandler;

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

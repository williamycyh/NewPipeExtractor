package com.ppd.ersan.video.comments;

import com.ppd.ersan.video.ListExtractor;
import com.ppd.ersan.video.StreamingService;
import com.ppd.ersan.video.exceptions.ExtractionException;
import com.ppd.ersan.video.exceptions.ParsingException;
import com.ppd.ersan.video.linkhandler.ListLinkHandler;

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

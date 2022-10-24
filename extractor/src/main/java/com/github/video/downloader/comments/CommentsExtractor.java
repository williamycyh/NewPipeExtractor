package com.github.video.downloader.comments;

import com.github.video.downloader.ListExtractor;
import com.github.video.downloader.StreamingService;
import com.github.video.downloader.exceptions.ExtractionException;
import com.github.video.downloader.exceptions.ParsingException;
import com.github.video.downloader.linkhandler.ListLinkHandler;

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

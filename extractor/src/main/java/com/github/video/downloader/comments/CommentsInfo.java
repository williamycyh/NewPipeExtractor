package com.github.video.downloader.comments;

import com.github.video.downloader.ListExtractor.InfoItemsPage;
import com.github.video.downloader.ListInfo;
import com.github.video.downloader.NewPipe;
import com.github.video.downloader.Page;
import com.github.video.downloader.StreamingService;
import com.github.video.downloader.exceptions.ExtractionException;
import com.github.video.downloader.linkhandler.ListLinkHandler;
import com.github.video.downloader.utils.ExtractorHelper;

import java.io.IOException;

public final class CommentsInfo extends ListInfo<CommentsInfoItem> {

    private CommentsInfo(
            final int serviceId,
            final ListLinkHandler listUrlIdHandler,
            final String name) {
        super(serviceId, listUrlIdHandler, name);
    }

    public static CommentsInfo getInfo(final String url) throws IOException, ExtractionException {
        return getInfo(NewPipe.getServiceByUrl(url), url);
    }

    public static CommentsInfo getInfo(final StreamingService service, final String url)
            throws ExtractionException, IOException {
        return getInfo(service.getCommentsExtractor(url));
    }

    public static CommentsInfo getInfo(final CommentsExtractor commentsExtractor)
            throws IOException, ExtractionException {
        // for services which do not have a comments extractor
        if (commentsExtractor == null) {
            return null;
        }

        commentsExtractor.fetchPage();

        final String name = commentsExtractor.getName();
        final int serviceId = commentsExtractor.getServiceId();
        final ListLinkHandler listUrlIdHandler = commentsExtractor.getLinkHandler();

        final CommentsInfo commentsInfo = new CommentsInfo(serviceId, listUrlIdHandler, name);
        commentsInfo.setCommentsExtractor(commentsExtractor);
        final InfoItemsPage<CommentsInfoItem> initialCommentsPage =
                ExtractorHelper.getItemsPageOrLogError(commentsInfo, commentsExtractor);
        commentsInfo.setCommentsDisabled(commentsExtractor.isCommentsDisabled());
        commentsInfo.setRelatedItems(initialCommentsPage.getItems());
        commentsInfo.setNextPage(initialCommentsPage.getNextPage());

        return commentsInfo;
    }

    public static InfoItemsPage<CommentsInfoItem> getMoreItems(
            final CommentsInfo commentsInfo,
            final Page page) throws ExtractionException, IOException {
        return getMoreItems(NewPipe.getService(commentsInfo.getServiceId()), commentsInfo.getUrl(),
                page);
    }

    public static InfoItemsPage<CommentsInfoItem> getMoreItems(
            final StreamingService service,
            final CommentsInfo commentsInfo,
            final Page page) throws IOException, ExtractionException {
        return getMoreItems(service, commentsInfo.getUrl(), page);
    }

    public static InfoItemsPage<CommentsInfoItem> getMoreItems(
            final StreamingService service,
            final String url,
            final Page page) throws IOException, ExtractionException {
        return service.getCommentsExtractor(url).getPage(page);
    }

    private transient CommentsExtractor commentsExtractor;
    private boolean commentsDisabled = false;

    public CommentsExtractor getCommentsExtractor() {
        return commentsExtractor;
    }

    public void setCommentsExtractor(final CommentsExtractor commentsExtractor) {
        this.commentsExtractor = commentsExtractor;
    }

    /**
     * @apiNote Warning: This method is experimental and may get removed in a future release.
     * @return {@code true} if the comments are disabled otherwise {@code false} (default)
     * @see CommentsExtractor#isCommentsDisabled()
     */
    public boolean isCommentsDisabled() {
        return commentsDisabled;
    }

    /**
     * @apiNote Warning: This method is experimental and may get removed in a future release.
     * @param commentsDisabled {@code true} if the comments are disabled otherwise {@code false}
     */
    public void setCommentsDisabled(final boolean commentsDisabled) {
        this.commentsDisabled = commentsDisabled;
    }
}

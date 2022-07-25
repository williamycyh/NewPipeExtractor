package com.ppd.ersan.video.services.youtube.linkHandler;

import com.ppd.ersan.video.exceptions.FoundAdException;
import com.ppd.ersan.video.exceptions.ParsingException;
import com.ppd.ersan.video.linkhandler.ListLinkHandlerFactory;

import java.util.List;

public final class YoutubeCommentsLinkHandlerFactory extends ListLinkHandlerFactory {

    private static final YoutubeCommentsLinkHandlerFactory INSTANCE
            = new YoutubeCommentsLinkHandlerFactory();

    private YoutubeCommentsLinkHandlerFactory() {
    }

    public static YoutubeCommentsLinkHandlerFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public String getUrl(final String id) {
        return "https://www.youtube.com/watch?v=" + id;
    }

    @Override
    public String getId(final String urlString) throws ParsingException, IllegalArgumentException {
        // we need the same id, avoids duplicate code
        return YoutubeStreamLinkHandlerFactory.getInstance().getId(urlString);
    }

    @Override
    public boolean onAcceptUrl(final String url) throws FoundAdException {
        try {
            getId(url);
            return true;
        } catch (final FoundAdException fe) {
            throw fe;
        } catch (final ParsingException e) {
            return false;
        }
    }

    @Override
    public String getUrl(final String id,
                         final List<String> contentFilter,
                         final String sortFilter) throws ParsingException {
        return getUrl(id);
    }
}

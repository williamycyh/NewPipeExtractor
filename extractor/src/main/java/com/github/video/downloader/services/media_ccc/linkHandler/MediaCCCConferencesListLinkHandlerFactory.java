package com.github.video.downloader.services.media_ccc.linkHandler;

import com.github.video.downloader.exceptions.ParsingException;
import com.github.video.downloader.linkhandler.ListLinkHandlerFactory;

import java.util.List;

public class MediaCCCConferencesListLinkHandlerFactory extends ListLinkHandlerFactory {
    @Override
    public String getId(final String url) throws ParsingException {
        return "conferences";
    }

    @Override
    public String getUrl(final String id, final List<String> contentFilter,
                         final String sortFilter) throws ParsingException {
        return "https://media.ccc.de/public/conferences";
    }

    @Override
    public boolean onAcceptUrl(final String url) {
        return url.equals("https://media.ccc.de/b/conferences")
                || url.equals("https://media.ccc.de/public/conferences")
                || url.equals("https://api.media.ccc.de/public/conferences");
    }
}

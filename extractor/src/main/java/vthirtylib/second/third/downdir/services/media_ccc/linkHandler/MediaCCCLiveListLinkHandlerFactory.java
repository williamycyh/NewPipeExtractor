package vthirtylib.second.third.downdir.services.media_ccc.linkHandler;

import vthirtylib.second.third.downdir.exceptions.ParsingException;
import vthirtylib.second.third.downdir.linkhandler.ListLinkHandlerFactory;

import java.util.List;
import java.util.regex.Pattern;

public class MediaCCCLiveListLinkHandlerFactory extends ListLinkHandlerFactory {
    private static final String STREAM_PATTERN = "^(?:https?://)?media\\.ccc\\.de/live$";

    @Override
    public String getId(final String url) throws ParsingException {
        return "live";
    }

    @Override
    public boolean onAcceptUrl(final String url) throws ParsingException {
        return Pattern.matches(STREAM_PATTERN, url);
    }

    @Override
    public String getUrl(final String id,
                         final List<String> contentFilter,
                         final String sortFilter) throws ParsingException {
        // FIXME: wrong URL; should be https://streaming.media.ccc.de/{conference_slug}/{room_slug}
        return "https://media.ccc.de/live";
    }
}
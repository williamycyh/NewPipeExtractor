package vthirtylib.second.third.downdir.services.media_ccc.linkHandler;

import vthirtylib.second.third.downdir.exceptions.ParsingException;
import vthirtylib.second.third.downdir.linkhandler.ListLinkHandlerFactory;
import vthirtylib.second.third.downdir.utils.Parser;

import java.util.List;

public class MediaCCCConferenceLinkHandlerFactory extends ListLinkHandlerFactory {
    public static final String CONFERENCE_API_ENDPOINT
            = "https://api.media.ccc.de/public/conferences/";
    public static final String CONFERENCE_PATH = "https://media.ccc.de/c/";
    private static final String ID_PATTERN
            = "(?:(?:(?:api\\.)?media\\.ccc\\.de/public/conferences/)"
            + "|(?:media\\.ccc\\.de/[bc]/))([^/?&#]*)";

    @Override
    public String getUrl(final String id,
                         final List<String> contentFilter,
                         final String sortFilter) throws ParsingException {
        return CONFERENCE_PATH + id;
    }

    @Override
    public String getId(final String url) throws ParsingException {
        return Parser.matchGroup1(ID_PATTERN, url);
    }

    @Override
    public boolean onAcceptUrl(final String url) {
        try {
            return getId(url) != null;
        } catch (final ParsingException e) {
            return false;
        }
    }
}
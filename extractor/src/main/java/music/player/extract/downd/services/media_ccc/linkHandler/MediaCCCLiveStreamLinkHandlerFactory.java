package music.player.extract.downd.services.media_ccc.linkHandler;

import music.player.extract.downd.exceptions.ParsingException;
import music.player.extract.downd.linkhandler.LinkHandlerFactory;
import music.player.extract.downd.utils.Parser;

public class MediaCCCLiveStreamLinkHandlerFactory extends LinkHandlerFactory {
    public static final String VIDEO_API_ENDPOINT = "https://api.media.ccc.de/public/events/";
    private static final String VIDEO_PATH = "https://streaming.media.ccc.de/v/";
    private static final String ID_PATTERN
            = "(?:(?:(?:api\\.)?media\\.ccc\\.de/public/events/)"
            + "|(?:media\\.ccc\\.de/v/))([^/?&#]*)";

    @Override
    public String getId(final String url) throws ParsingException {
        return Parser.matchGroup1(ID_PATTERN, url);
    }

    @Override
    public String getUrl(final String id) throws ParsingException {
        return VIDEO_PATH + id;
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

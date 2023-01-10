package tndown.tndir.simplevd.vdwload.services.soundcloud.linkHandler;

import tndown.tndir.simplevd.vdwload.utils.Parser;
import tndown.tndir.simplevd.vdwload.Utils;
import tndown.tndir.simplevd.vdwload.exceptions.ParsingException;
import tndown.tndir.simplevd.vdwload.linkhandler.ListLinkHandlerFactory;
import tndown.tndir.simplevd.vdwload.services.soundcloud.SoundcloudParsingHelper;

import java.util.List;

public final class SoundcloudPlaylistLinkHandlerFactory extends ListLinkHandlerFactory {
    private static final SoundcloudPlaylistLinkHandlerFactory INSTANCE =
            new SoundcloudPlaylistLinkHandlerFactory();
    private static final String URL_PATTERN = "^https?://(www\\.|m\\.)?soundcloud.com/[0-9a-z_-]+"
            + "/sets/[0-9a-z_-]+/?([#?].*)?$";

    private SoundcloudPlaylistLinkHandlerFactory() {
    }

    public static SoundcloudPlaylistLinkHandlerFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public String getId(final String url) throws ParsingException {
        Utils.checkUrl(URL_PATTERN, url);

        try {
            return SoundcloudParsingHelper.resolveIdWithWidgetApi(url);
        } catch (final Exception e) {
            throw new ParsingException("Could not get id of url: " + url + " " + e.getMessage(),
                    e);
        }
    }

    @Override
    public String getUrl(final String id,
                         final List<String> contentFilter,
                         final String sortFilter)
            throws ParsingException {
        try {
            return SoundcloudParsingHelper.resolveUrlWithEmbedPlayer(
                    "https://api.soundcloud.com/playlists/" + id);
        } catch (final Exception e) {
            throw new ParsingException(e.getMessage(), e);
        }
    }

    @Override
    public boolean onAcceptUrl(final String url) throws ParsingException {
        return Parser.isMatch(URL_PATTERN, url.toLowerCase());
    }
}

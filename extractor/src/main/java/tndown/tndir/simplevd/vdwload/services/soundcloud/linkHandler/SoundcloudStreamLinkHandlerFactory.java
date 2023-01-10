package tndown.tndir.simplevd.vdwload.services.soundcloud.linkHandler;

import tndown.tndir.simplevd.vdwload.linkhandler.LinkHandlerFactory;
import tndown.tndir.simplevd.vdwload.utils.Parser;
import tndown.tndir.simplevd.vdwload.Utils;
import tndown.tndir.simplevd.vdwload.exceptions.ParsingException;
import tndown.tndir.simplevd.vdwload.services.soundcloud.SoundcloudParsingHelper;

public final class SoundcloudStreamLinkHandlerFactory extends LinkHandlerFactory {
    private static final SoundcloudStreamLinkHandlerFactory INSTANCE
            = new SoundcloudStreamLinkHandlerFactory();
    private static final String URL_PATTERN = "^https?://(www\\.|m\\.)?soundcloud.com/[0-9a-z_-]+"
            + "/(?!(tracks|albums|sets|reposts|followers|following)/?$)[0-9a-z_-]+/?([#?].*)?$";

    private SoundcloudStreamLinkHandlerFactory() {
    }

    public static SoundcloudStreamLinkHandlerFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public String getUrl(final String id) throws ParsingException {
        try {
            return SoundcloudParsingHelper.resolveUrlWithEmbedPlayer(
                    "https://api.soundcloud.com/tracks/" + id);
        } catch (final Exception e) {
            throw new ParsingException(e.getMessage(), e);
        }
    }

    @Override
    public String getId(final String url) throws ParsingException {
        Utils.checkUrl(URL_PATTERN, url);

        try {
            return SoundcloudParsingHelper.resolveIdWithWidgetApi(url);
        } catch (final Exception e) {
            throw new ParsingException(e.getMessage(), e);
        }
    }

    @Override
    public boolean onAcceptUrl(final String url) throws ParsingException {
        return Parser.isMatch(URL_PATTERN, url.toLowerCase());
    }
}

package tndown.tndir.simplevd.vdwload.services.soundcloud.linkHandler;

import tndown.tndir.simplevd.vdwload.utils.Parser;
import tndown.tndir.simplevd.vdwload.Utils;
import tndown.tndir.simplevd.vdwload.exceptions.ParsingException;
import tndown.tndir.simplevd.vdwload.linkhandler.ListLinkHandlerFactory;
import tndown.tndir.simplevd.vdwload.services.soundcloud.SoundcloudParsingHelper;

import java.util.List;

public final class SoundcloudChannelLinkHandlerFactory extends ListLinkHandlerFactory {
    private static final SoundcloudChannelLinkHandlerFactory INSTANCE
            = new SoundcloudChannelLinkHandlerFactory();
    private static final String URL_PATTERN = "^https?://(www\\.|m\\.)?soundcloud.com/[0-9a-z_-]+"
            + "(/((tracks|albums|sets|reposts|followers|following)/?)?)?([#?].*)?$";

    private SoundcloudChannelLinkHandlerFactory() {
    }

    public static SoundcloudChannelLinkHandlerFactory getInstance() {
        return INSTANCE;
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
    public String getUrl(final String id,
                         final List<String> contentFilter,
                         final String sortFilter) throws ParsingException {
        try {
            return SoundcloudParsingHelper.resolveUrlWithEmbedPlayer(
                    "https://api.soundcloud.com/users/" + id);
        } catch (final Exception e) {
            throw new ParsingException(e.getMessage(), e);
        }
    }

    @Override
    public boolean onAcceptUrl(final String url) {
        return Parser.isMatch(URL_PATTERN, url.toLowerCase());
    }
}

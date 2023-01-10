package tndown.tndir.simplevd.vdwload.playlist;

import tndown.tndir.simplevd.vdwload.ListExtractor;
import tndown.tndir.simplevd.vdwload.StreamingService;
import tndown.tndir.simplevd.vdwload.exceptions.ParsingException;
import tndown.tndir.simplevd.vdwload.linkhandler.ListLinkHandler;
import tndown.tndir.simplevd.vdwload.stream.StreamInfoItem;

import javax.annotation.Nonnull;

import tndown.tndir.simplevd.vdwload.Utils;

public abstract class PlaylistExtractor extends ListExtractor<StreamInfoItem> {

    public PlaylistExtractor(final StreamingService service, final ListLinkHandler linkHandler) {
        super(service, linkHandler);
    }

    public abstract String getUploaderUrl() throws ParsingException;
    public abstract String getUploaderName() throws ParsingException;
    public abstract String getUploaderAvatarUrl() throws ParsingException;
    public abstract boolean isUploaderVerified() throws ParsingException;

    public abstract long getStreamCount() throws ParsingException;

    @Nonnull
    public String getThumbnailUrl() throws ParsingException {
        return Utils.EMPTY_STRING;
    }

    @Nonnull
    public String getBannerUrl() throws ParsingException {
        // Banner can't be handled by frontend right now.
        // Whoever is willing to implement this should also implement it in the frontend.
        return Utils.EMPTY_STRING;
    }

    @Nonnull
    public String getSubChannelName() throws ParsingException {
        return Utils.EMPTY_STRING;
    }

    @Nonnull
    public String getSubChannelUrl() throws ParsingException {
        return Utils.EMPTY_STRING;
    }

    @Nonnull
    public String getSubChannelAvatarUrl() throws ParsingException {
        return Utils.EMPTY_STRING;
    }

    public PlaylistInfo.PlaylistType getPlaylistType() throws ParsingException {
        return PlaylistInfo.PlaylistType.NORMAL;
    }
}

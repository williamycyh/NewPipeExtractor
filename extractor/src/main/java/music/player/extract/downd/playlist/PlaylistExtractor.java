package music.player.extract.downd.playlist;

import music.player.extract.downd.ListExtractor;
import music.player.extract.downd.StreamingService;
import music.player.extract.downd.exceptions.ParsingException;
import music.player.extract.downd.linkhandler.ListLinkHandler;
import music.player.extract.downd.stream.StreamInfoItem;

import javax.annotation.Nonnull;

import static music.player.extract.downd.utils.Utils.EMPTY_STRING;

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
        return EMPTY_STRING;
    }

    @Nonnull
    public String getBannerUrl() throws ParsingException {
        // Banner can't be handled by frontend right now.
        // Whoever is willing to implement this should also implement it in the frontend.
        return EMPTY_STRING;
    }

    @Nonnull
    public String getSubChannelName() throws ParsingException {
        return EMPTY_STRING;
    }

    @Nonnull
    public String getSubChannelUrl() throws ParsingException {
        return EMPTY_STRING;
    }

    @Nonnull
    public String getSubChannelAvatarUrl() throws ParsingException {
        return EMPTY_STRING;
    }

    public PlaylistInfo.PlaylistType getPlaylistType() throws ParsingException {
        return PlaylistInfo.PlaylistType.NORMAL;
    }
}

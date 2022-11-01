package tevd.nbapp.vide.downl.playlist;

import tevd.nbapp.vide.downl.ListExtractor;
import tevd.nbapp.vide.downl.StreamingService;
import tevd.nbapp.vide.downl.exceptions.ParsingException;
import tevd.nbapp.vide.downl.linkhandler.ListLinkHandler;
import tevd.nbapp.vide.downl.stream.StreamInfoItem;

import javax.annotation.Nonnull;

import tevd.nbapp.vide.downl.utils.Utils;

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

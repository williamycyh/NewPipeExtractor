package com.github.video.downloader.services.youtube.extractors;

import com.grack.nanojson.JsonObject;
import com.github.video.downloader.services.youtube.YoutubeParsingHelper;
import com.github.video.downloader.services.youtube.linkHandler.YoutubePlaylistLinkHandlerFactory;

import com.github.video.downloader.exceptions.ParsingException;
import com.github.video.downloader.playlist.PlaylistInfoItemExtractor;
import com.github.video.downloader.utils.Utils;

import static com.github.video.downloader.services.youtube.YoutubeParsingHelper.getTextFromObject;

public class YoutubePlaylistInfoItemExtractor implements PlaylistInfoItemExtractor {
    private final JsonObject playlistInfoItem;

    public YoutubePlaylistInfoItemExtractor(final JsonObject playlistInfoItem) {
        this.playlistInfoItem = playlistInfoItem;
    }

    @Override
    public String getThumbnailUrl() throws ParsingException {
        try {
            final String url = playlistInfoItem.getArray("thumbnails").getObject(0)
                    .getArray("thumbnails").getObject(0).getString("url");

            return YoutubeParsingHelper.fixThumbnailUrl(url);
        } catch (final Exception e) {
            throw new ParsingException("Could not get thumbnail url", e);
        }
    }

    @Override
    public String getName() throws ParsingException {
        try {
            return YoutubeParsingHelper.getTextFromObject(playlistInfoItem.getObject("title"));
        } catch (final Exception e) {
            throw new ParsingException("Could not get name", e);
        }
    }

    @Override
    public String getUrl() throws ParsingException {
        try {
            final String id = playlistInfoItem.getString("playlistId");
            return YoutubePlaylistLinkHandlerFactory.getInstance().getUrl(id);
        } catch (final Exception e) {
            throw new ParsingException("Could not get url", e);
        }
    }

    @Override
    public String getUploaderName() throws ParsingException {
        try {
            return YoutubeParsingHelper.getTextFromObject(playlistInfoItem.getObject("longBylineText"));
        } catch (final Exception e) {
            throw new ParsingException("Could not get uploader name", e);
        }
    }

    @Override
    public long getStreamCount() throws ParsingException {
        try {
            return Long.parseLong(Utils.removeNonDigitCharacters(
                    playlistInfoItem.getString("videoCount")));
        } catch (final Exception e) {
            throw new ParsingException("Could not get stream count", e);
        }
    }
}

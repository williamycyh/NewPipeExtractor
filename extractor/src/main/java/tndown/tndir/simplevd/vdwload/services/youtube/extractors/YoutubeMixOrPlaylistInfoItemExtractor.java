package tndown.tndir.simplevd.vdwload.services.youtube.extractors;

import static tndown.tndir.simplevd.vdwload.services.youtube.YoutubeParsingHelper.getTextFromObject;
import static tndown.tndir.simplevd.vdwload.Utils.isNullOrEmpty;

import com.grack.nanojson.JsonObject;
import tndown.tndir.simplevd.vdwload.services.youtube.YoutubeParsingHelper;

import tndown.tndir.simplevd.vdwload.ListExtractor;
import tndown.tndir.simplevd.vdwload.exceptions.ParsingException;
import tndown.tndir.simplevd.vdwload.playlist.PlaylistInfo;
import tndown.tndir.simplevd.vdwload.playlist.PlaylistInfoItemExtractor;

import javax.annotation.Nonnull;

import tndown.tndir.simplevd.vdwload.Utils;

public class YoutubeMixOrPlaylistInfoItemExtractor implements PlaylistInfoItemExtractor {
    private final JsonObject mixInfoItem;

    public YoutubeMixOrPlaylistInfoItemExtractor(final JsonObject mixInfoItem) {
        this.mixInfoItem = mixInfoItem;
    }

    @Override
    public String getName() throws ParsingException {
        final String name = YoutubeParsingHelper.getTextFromObject(mixInfoItem.getObject("title"));
        if (Utils.isNullOrEmpty(name)) {
            throw new ParsingException("Could not get name");
        }
        return name;
    }

    @Override
    public String getUrl() throws ParsingException {
        final String url = mixInfoItem.getString("shareUrl");
        if (Utils.isNullOrEmpty(url)) {
            throw new ParsingException("Could not get url");
        }
        return url;
    }

    @Override
    public String getThumbnailUrl() throws ParsingException {
        return YoutubeParsingHelper.getThumbnailUrlFromInfoItem(mixInfoItem);
    }

    @Override
    public String getUploaderName() throws ParsingException {
        // this will be "YouTube" for mixes
        return YoutubeParsingHelper.getTextFromObject(mixInfoItem.getObject("longBylineText"));
    }

    @Override
    public long getStreamCount() throws ParsingException {
        final String countString = YoutubeParsingHelper.getTextFromObject(
                mixInfoItem.getObject("videoCountShortText"));
        if (countString == null) {
            throw new ParsingException("Could not extract item count for playlist/mix info item");
        }

        try {
            return Integer.parseInt(countString);
        } catch (final NumberFormatException ignored) {
            // un-parsable integer: this is a mix with infinite items and "50+" as count string
            // (though youtube music mixes do not necessarily have an infinite count of songs)
            return ListExtractor.ITEM_COUNT_INFINITE;
        }
    }

    @Nonnull
    @Override
    public PlaylistInfo.PlaylistType getPlaylistType() throws ParsingException {
        return YoutubeParsingHelper.extractPlaylistTypeFromPlaylistUrl(getUrl());
    }
}

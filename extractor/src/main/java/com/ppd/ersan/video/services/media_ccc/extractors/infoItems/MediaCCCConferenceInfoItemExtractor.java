package com.ppd.ersan.video.services.media_ccc.extractors.infoItems;

import com.grack.nanojson.JsonObject;
import com.ppd.ersan.video.ListExtractor;
import com.ppd.ersan.video.channel.ChannelInfoItemExtractor;
import com.ppd.ersan.video.exceptions.ParsingException;

public class MediaCCCConferenceInfoItemExtractor implements ChannelInfoItemExtractor {
    private final JsonObject conference;

    public MediaCCCConferenceInfoItemExtractor(final JsonObject conference) {
        this.conference = conference;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public long getSubscriberCount() {
        return -1;
    }

    @Override
    public long getStreamCount() {
        return ListExtractor.ITEM_COUNT_UNKNOWN;
    }

    @Override
    public boolean isVerified() throws ParsingException {
        return false;
    }

    @Override
    public String getName() throws ParsingException {
        return conference.getString("title");
    }

    @Override
    public String getUrl() throws ParsingException {
        return conference.getString("url");
    }

    @Override
    public String getThumbnailUrl() {
        return conference.getString("logo_url");
    }
}

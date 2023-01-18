package vthirtylib.second.third.downdir.services.media_ccc.extractors.infoItems;

import com.grack.nanojson.JsonObject;
import vthirtylib.second.third.downdir.exceptions.ParsingException;
import vthirtylib.second.third.downdir.localization.DateWrapper;
import vthirtylib.second.third.downdir.services.media_ccc.extractors.MediaCCCParsingHelper;
import vthirtylib.second.third.downdir.stream.StreamInfoItemExtractor;
import vthirtylib.second.third.downdir.stream.StreamType;

import javax.annotation.Nullable;

public class MediaCCCStreamInfoItemExtractor implements StreamInfoItemExtractor {
    private final JsonObject event;

    public MediaCCCStreamInfoItemExtractor(final JsonObject event) {
        this.event = event;
    }

    @Override
    public StreamType getStreamType() {
        return StreamType.VIDEO_STREAM;
    }

    @Override
    public boolean isAd() {
        return false;
    }

    @Override
    public long getDuration() {
        return event.getInt("length");
    }

    @Override
    public long getViewCount() {
        return event.getInt("view_count");
    }

    @Override
    public String getUploaderName() {
        return event.getString("conference_title");
    }

    @Override
    public String getUploaderUrl() {
        return event.getString("conference_url");
    }

    @Nullable
    @Override
    public String getUploaderAvatarUrl() {
        return null;
    }

    @Override
    public boolean isUploaderVerified() throws ParsingException {
        return false;
    }

    @Nullable
    @Override
    public String getTextualUploadDate() {
        return event.getString("release_date");
    }

    @Nullable
    @Override
    public DateWrapper getUploadDate() throws ParsingException {
        final String date = getTextualUploadDate();
        if (date == null) {
            return null; // event is in the future...
        }
        return new DateWrapper(MediaCCCParsingHelper.parseDateFrom(date));
    }

    @Override
    public String getName() throws ParsingException {
        return event.getString("title");
    }

    @Override
    public String getUrl() throws ParsingException {
        return "https://media.ccc.de/public/events/"
                + event.getString("guid");
    }

    @Override
    public String getThumbnailUrl() {
        return event.getString("thumb_url");
    }
}
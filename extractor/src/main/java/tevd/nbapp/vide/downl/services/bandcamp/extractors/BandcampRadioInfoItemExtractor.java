// Created by Fynn Godau 2019, licensed GNU GPL version 3 or later

package tevd.nbapp.vide.downl.services.bandcamp.extractors;

import com.grack.nanojson.JsonObject;
import tevd.nbapp.vide.downl.exceptions.ParsingException;
import tevd.nbapp.vide.downl.localization.DateWrapper;
import tevd.nbapp.vide.downl.stream.StreamInfoItemExtractor;
import tevd.nbapp.vide.downl.stream.StreamType;

import javax.annotation.Nullable;

public class BandcampRadioInfoItemExtractor implements StreamInfoItemExtractor {

    private final JsonObject show;

    public BandcampRadioInfoItemExtractor(final JsonObject radioShow) {
        show = radioShow;
    }

    @Override
    public long getDuration() {
        /* Duration is only present in the more detailed information that has to be queried
        separately. Therefore, over 300 queries would be needed every time the kiosk is opened if we
        were to display the real value. */
        //return query(show.getInt("id")).getLong("audio_duration");
        return 0;
    }

    @Nullable
    @Override
    public String getTextualUploadDate() {
        return show.getString("date");
    }

    @Nullable
    @Override
    public DateWrapper getUploadDate() throws ParsingException {
        return BandcampExtractorHelper.parseDate(getTextualUploadDate());
    }

    @Override
    public String getName() throws ParsingException {
        return show.getString("subtitle");
    }

    @Override
    public String getUrl() {
        return BandcampExtractorHelper.BASE_URL + "/?show=" + show.getInt("id");
    }

    @Override
    public String getThumbnailUrl() {
        return BandcampExtractorHelper.getImageUrl(show.getLong("image_id"), false);
    }

    @Override
    public StreamType getStreamType() {
        return StreamType.AUDIO_STREAM;
    }

    @Override
    public long getViewCount() {
        return -1;
    }

    @Override
    public String getUploaderName() {
        // JSON does not contain uploader name
        return "";
    }

    @Override
    public String getUploaderUrl() {
        return "";
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

    @Override
    public boolean isAd() {
        return false;
    }
}

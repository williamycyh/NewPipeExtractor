package vmeno.yyml.nnbersi.downd.services.soundcloud.extractors;

import com.grack.nanojson.JsonObject;
import vmeno.yyml.nnbersi.downd.localization.DateWrapper;
import vmeno.yyml.nnbersi.downd.stream.StreamInfoItemExtractor;
import vmeno.yyml.nnbersi.downd.stream.StreamType;
import vmeno.yyml.nnbersi.downd.utils.Utils;
import vmeno.yyml.nnbersi.downd.exceptions.ParsingException;
import vmeno.yyml.nnbersi.downd.services.soundcloud.SoundcloudParsingHelper;

import javax.annotation.Nullable;

public class SoundcloudStreamInfoItemExtractor implements StreamInfoItemExtractor {

    protected final JsonObject itemObject;

    public SoundcloudStreamInfoItemExtractor(final JsonObject itemObject) {
        this.itemObject = itemObject;
    }

    @Override
    public String getUrl() {
        return Utils.replaceHttpWithHttps(itemObject.getString("permalink_url"));
    }

    @Override
    public String getName() {
        return itemObject.getString("title");
    }

    @Override
    public long getDuration() {
        return itemObject.getLong("duration") / 1000L;
    }

    @Override
    public String getUploaderName() {
        return itemObject.getObject("user").getString("username");
    }

    @Override
    public String getUploaderUrl() {
        return Utils.replaceHttpWithHttps(itemObject.getObject("user").getString("permalink_url"));
    }

    @Nullable
    @Override
    public String getUploaderAvatarUrl() {
        return null;
    }

    @Override
    public boolean isUploaderVerified() throws ParsingException {
        return itemObject.getObject("user").getBoolean("verified");
    }

    @Override
    public String getTextualUploadDate() {
        return itemObject.getString("created_at");
    }

    @Override
    public DateWrapper getUploadDate() throws ParsingException {
        return new DateWrapper(SoundcloudParsingHelper.parseDateFrom(getTextualUploadDate()));
    }

    @Override
    public long getViewCount() {
        return itemObject.getLong("playback_count");
    }

    @Override
    public String getThumbnailUrl() {
        String artworkUrl = itemObject.getString("artwork_url", Utils.EMPTY_STRING);
        if (artworkUrl.isEmpty()) {
            artworkUrl = itemObject.getObject("user").getString("avatar_url");
        }
        return artworkUrl.replace("large.jpg", "crop.jpg");
    }

    @Override
    public StreamType getStreamType() {
        return StreamType.AUDIO_STREAM;
    }

    @Override
    public boolean isAd() {
        return false;
    }
}

package vmeno.yyml.nnbersi.downd.services.media_ccc.extractors;

import com.grack.nanojson.JsonObject;
import vmeno.yyml.nnbersi.downd.exceptions.ParsingException;
import vmeno.yyml.nnbersi.downd.localization.DateWrapper;
import vmeno.yyml.nnbersi.downd.stream.StreamInfoItemExtractor;
import vmeno.yyml.nnbersi.downd.stream.StreamType;

import vmeno.yyml.nnbersi.downd.services.media_ccc.linkHandler.MediaCCCConferenceLinkHandlerFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Nullable;

public class MediaCCCRecentKioskExtractor implements StreamInfoItemExtractor {

    private final JsonObject event;

    public MediaCCCRecentKioskExtractor(final JsonObject event) {
        this.event = event;
    }

    @Override
    public String getName() throws ParsingException {
        return event.getString("title");
    }

    @Override
    public String getUrl() throws ParsingException {
        return event.getString("frontend_link");
    }

    @Override
    public String getThumbnailUrl() throws ParsingException {
        return event.getString("thumb_url");
    }

    @Override
    public StreamType getStreamType() throws ParsingException {
        return StreamType.VIDEO_STREAM;
    }

    @Override
    public boolean isAd() {
        return false;
    }

    @Override
    public long getDuration() {
        // duration and length have the same value, see
        // https://github.com/voc/voctoweb/blob/master/app/views/public/shared/_event.json.jbuilder
        return event.getInt("duration");
    }

    @Override
    public long getViewCount() throws ParsingException {
        return event.getInt("view_count");
    }

    @Override
    public String getUploaderName() throws ParsingException {
        return event.getString("conference_title");
    }

    @Override
    public String getUploaderUrl() throws ParsingException {
        return new MediaCCCConferenceLinkHandlerFactory()
                .fromUrl(event.getString("conference_url")) // API URL
                .getUrl(); // web URL
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
    public String getTextualUploadDate() throws ParsingException {
        return event.getString("date");
    }

    @Nullable
    @Override
    public DateWrapper getUploadDate() throws ParsingException {
        final ZonedDateTime zonedDateTime = ZonedDateTime.parse(event.getString("date"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSzzzz"));
        return new DateWrapper(zonedDateTime.toOffsetDateTime(), false);
    }
}

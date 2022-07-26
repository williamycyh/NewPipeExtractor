package vmeno.yyml.nnbersi.downd.services.bandcamp.extractors.streaminfoitem;

import vmeno.yyml.nnbersi.downd.exceptions.ParsingException;
import vmeno.yyml.nnbersi.downd.localization.DateWrapper;
import vmeno.yyml.nnbersi.downd.stream.StreamInfoItemExtractor;
import vmeno.yyml.nnbersi.downd.stream.StreamType;

import javax.annotation.Nullable;

/**
 * Implements methods that return a constant value in subclasses for better readability.
 */
public abstract class BandcampStreamInfoItemExtractor implements StreamInfoItemExtractor {
    private final String uploaderUrl;

    public BandcampStreamInfoItemExtractor(final String uploaderUrl) {
        this.uploaderUrl = uploaderUrl;
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
    public String getUploaderUrl() {
        return uploaderUrl;
    }

    @Nullable
    @Override
    public String getTextualUploadDate() {
        return null;
    }

    @Nullable
    @Override
    public DateWrapper getUploadDate() {
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

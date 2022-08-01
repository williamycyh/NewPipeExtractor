package music.player.extract.downd.services.peertube;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import music.player.extract.downd.InfoItemsCollector;
import music.player.extract.downd.Page;
import music.player.extract.downd.exceptions.ContentNotAvailableException;
import music.player.extract.downd.exceptions.ParsingException;
import music.player.extract.downd.utils.JsonUtils;
import music.player.extract.downd.utils.Parser;
import music.player.extract.downd.utils.Utils;
import music.player.extract.downd.services.peertube.extractors.PeertubeSepiaStreamInfoItemExtractor;
import music.player.extract.downd.services.peertube.extractors.PeertubeStreamInfoItemExtractor;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;

public final class PeertubeParsingHelper {
    public static final String START_KEY = "start";
    public static final String COUNT_KEY = "count";
    public static final int ITEMS_PER_PAGE = 12;
    public static final String START_PATTERN = "start=(\\d*)";

    private PeertubeParsingHelper() {
    }

    public static void validate(final JsonObject json) throws ContentNotAvailableException {
        final String error = json.getString("error");
        if (!Utils.isBlank(error)) {
            throw new ContentNotAvailableException(error);
        }
    }

    public static OffsetDateTime parseDateFrom(final String textualUploadDate)
            throws ParsingException {
        try {
            return OffsetDateTime.ofInstant(Instant.parse(textualUploadDate), ZoneOffset.UTC);
        } catch (final DateTimeParseException e) {
            throw new ParsingException("Could not parse date: \"" + textualUploadDate + "\"", e);
        }
    }

    public static Page getNextPage(final String prevPageUrl, final long total) {
        final String prevStart;
        try {
            prevStart = Parser.matchGroup1(START_PATTERN, prevPageUrl);
        } catch (final Parser.RegexException e) {
            return null;
        }
        if (Utils.isBlank(prevStart)) {
            return null;
        }

        final long nextStart;
        try {
            nextStart = Long.parseLong(prevStart) + ITEMS_PER_PAGE;
        } catch (final NumberFormatException e) {
            return null;
        }

        if (nextStart >= total) {
            return null;
        } else {
            return new Page(prevPageUrl.replace(
                    START_KEY + "=" + prevStart, START_KEY + "=" + nextStart));
        }
    }

    public static void collectStreamsFrom(final InfoItemsCollector collector,
                                          final JsonObject json,
                                          final String baseUrl) throws ParsingException {
        collectStreamsFrom(collector, json, baseUrl, false);
    }

    /**
     * Collect stream from json with collector
     *
     * @param collector the collector used to collect information
     * @param json      the file to retrieve data from
     * @param baseUrl   the base Url of the instance
     * @param sepia     if we should use PeertubeSepiaStreamInfoItemExtractor
     */
    public static void collectStreamsFrom(final InfoItemsCollector collector,
                                          final JsonObject json,
                                          final String baseUrl,
                                          final boolean sepia) throws ParsingException {
        final JsonArray contents;
        try {
            contents = (JsonArray) JsonUtils.getValue(json, "data");
        } catch (final Exception e) {
            throw new ParsingException("Unable to extract list info", e);
        }

        for (final Object c : contents) {
            if (c instanceof JsonObject) {
                JsonObject item = (JsonObject) c;

                // PeerTube playlists have the stream info encapsulated in an "video" object
                if (item.has("video")) {
                    item = item.getObject("video");
                }

                final PeertubeStreamInfoItemExtractor extractor;
                if (sepia) {
                    extractor = new PeertubeSepiaStreamInfoItemExtractor(item, baseUrl);
                } else {
                    extractor = new PeertubeStreamInfoItemExtractor(item, baseUrl);
                }
                collector.commit(extractor);
            }
        }
    }

}

package vmeno.yyml.nnbersi.downd.services.youtube.extractors;

import static vmeno.yyml.nnbersi.downd.utils.Utils.EMPTY_STRING;
import static vmeno.yyml.nnbersi.downd.utils.Utils.getQueryValue;
import static vmeno.yyml.nnbersi.downd.utils.Utils.isNullOrEmpty;
import static vmeno.yyml.nnbersi.downd.utils.Utils.stringToURL;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonBuilder;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonWriter;
import vmeno.yyml.nnbersi.downd.playlist.PlaylistExtractor;
import vmeno.yyml.nnbersi.downd.services.youtube.YoutubeParsingHelper;

import vmeno.yyml.nnbersi.downd.ListExtractor;
import vmeno.yyml.nnbersi.downd.Page;
import vmeno.yyml.nnbersi.downd.StreamingService;
import vmeno.yyml.nnbersi.downd.downloader.Downloader;
import vmeno.yyml.nnbersi.downd.downloader.Response;
import vmeno.yyml.nnbersi.downd.exceptions.ExtractionException;
import vmeno.yyml.nnbersi.downd.exceptions.ParsingException;
import vmeno.yyml.nnbersi.downd.linkhandler.ListLinkHandler;
import vmeno.yyml.nnbersi.downd.localization.Localization;
import vmeno.yyml.nnbersi.downd.localization.TimeAgoParser;
import vmeno.yyml.nnbersi.downd.playlist.PlaylistInfo;
import vmeno.yyml.nnbersi.downd.stream.StreamInfoItem;
import vmeno.yyml.nnbersi.downd.stream.StreamInfoItemsCollector;
import vmeno.yyml.nnbersi.downd.utils.JsonUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A {@link YoutubePlaylistExtractor} for a mix (auto-generated playlist).
 * It handles URLs in the format of
 * {@code youtube.com/watch?v=videoId&list=playlistId}
 */
public class YoutubeMixPlaylistExtractor extends PlaylistExtractor {

    /**
     * YouTube identifies mixes based on this cookie. With this information it can generate
     * continuations without duplicates.
     */
    public static final String COOKIE_NAME = "VISITOR_INFO1_LIVE";

    private JsonObject initialData;
    private JsonObject playlistData;
    private String cookieValue;

    public YoutubeMixPlaylistExtractor(final StreamingService service,
                                       final ListLinkHandler linkHandler) {
        super(service, linkHandler);
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader)
            throws IOException, ExtractionException {
        final Localization localization = getExtractorLocalization();
        final URL url = stringToURL(getUrl());
        final String mixPlaylistId = getId();
        final String videoId = getQueryValue(url, "v");
        final String playlistIndexString = getQueryValue(url, "index");

        final JsonBuilder<JsonObject> jsonBody = YoutubeParsingHelper.prepareDesktopJsonBuilder(localization,
                getExtractorContentCountry()).value("playlistId", mixPlaylistId);
        if (videoId != null) {
            jsonBody.value("videoId", videoId);
        }
        if (playlistIndexString != null) {
            jsonBody.value("playlistIndex", Integer.parseInt(playlistIndexString));
        }

        final byte[] body = JsonWriter.string(jsonBody.done()).getBytes(StandardCharsets.UTF_8);

        final Map<String, List<String>> headers = new HashMap<>();
        YoutubeParsingHelper.addClientInfoHeaders(headers);

        final Response response = getDownloader().post(YoutubeParsingHelper.YOUTUBEI_V1_URL + "next?key=" + YoutubeParsingHelper.getKey()
                + YoutubeParsingHelper.DISABLE_PRETTY_PRINT_PARAMETER, headers, body, localization);

        initialData = JsonUtils.toJsonObject(YoutubeParsingHelper.getValidJsonResponseBody(response));
        playlistData = initialData.getObject("contents").getObject("twoColumnWatchNextResults")
                .getObject("playlist").getObject("playlist");
        if (isNullOrEmpty(playlistData)) {
            throw new ExtractionException("Could not get playlistData");
        }
        cookieValue = YoutubeParsingHelper.extractCookieValue(COOKIE_NAME, response);
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        final String name = YoutubeParsingHelper.getTextAtKey(playlistData, "title");
        if (isNullOrEmpty(name)) {
            throw new ParsingException("Could not get playlist name");
        }
        return name;
    }

    @Nonnull
    @Override
    public String getThumbnailUrl() throws ParsingException {
        try {
            return getThumbnailUrlFromPlaylistId(playlistData.getString("playlistId"));
        } catch (final Exception e) {
            try {
                // Fallback to thumbnail of current video. Always the case for channel mix
                return getThumbnailUrlFromVideoId(initialData.getObject("currentVideoEndpoint")
                        .getObject("watchEndpoint").getString("videoId"));
            } catch (final Exception ignored) {
            }

            throw new ParsingException("Could not get playlist thumbnail", e);
        }
    }

    @Override
    public String getUploaderUrl() {
        // YouTube mixes are auto-generated by YouTube
        return EMPTY_STRING;
    }

    @Override
    public String getUploaderName() {
        // YouTube mixes are auto-generated by YouTube
        return "YouTube";
    }

    @Override
    public String getUploaderAvatarUrl() {
        // YouTube mixes are auto-generated by YouTube
        return EMPTY_STRING;
    }

    @Override
    public boolean isUploaderVerified() throws ParsingException {
        return false;
    }

    @Override
    public long getStreamCount() {
        // Auto-generated playlists always start with 25 videos and are endless
        return ListExtractor.ITEM_COUNT_INFINITE;
    }

    @Nonnull
    @Override
    public InfoItemsPage<StreamInfoItem> getInitialPage()
            throws IOException, ExtractionException {
        final StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
        collectStreamsFrom(collector, playlistData.getArray("contents"));

        final Map<String, String> cookies = new HashMap<>();
        cookies.put(COOKIE_NAME, cookieValue);

        return new InfoItemsPage<>(collector, getNextPageFrom(playlistData, cookies));
    }

    @Nonnull
    private Page getNextPageFrom(@Nonnull final JsonObject playlistJson,
                                 final Map<String, String> cookies)
            throws IOException, ExtractionException {
        final JsonObject lastStream = ((JsonObject) playlistJson.getArray("contents")
                .get(playlistJson.getArray("contents").size() - 1));
        if (lastStream == null || lastStream.getObject("playlistPanelVideoRenderer") == null) {
            throw new ExtractionException("Could not extract next page url");
        }

        final JsonObject watchEndpoint = lastStream.getObject("playlistPanelVideoRenderer")
                .getObject("navigationEndpoint").getObject("watchEndpoint");
        final String playlistId = watchEndpoint.getString("playlistId");
        final String videoId = watchEndpoint.getString("videoId");
        final int index = watchEndpoint.getInt("index");
        final String params = watchEndpoint.getString("params");
        final byte[] body = JsonWriter.string(YoutubeParsingHelper.prepareDesktopJsonBuilder(getExtractorLocalization(),
                getExtractorContentCountry())
                .value("videoId", videoId)
                .value("playlistId", playlistId)
                .value("playlistIndex", index)
                .value("params", params)
                .done())
                .getBytes(StandardCharsets.UTF_8);

        return new Page(YoutubeParsingHelper.YOUTUBEI_V1_URL + "next?key=" + YoutubeParsingHelper.getKey(), null, null, cookies, body);
    }

    @Override
    public InfoItemsPage<StreamInfoItem> getPage(final Page page) throws IOException,
            ExtractionException {
        if (page == null || isNullOrEmpty(page.getUrl())) {
            throw new IllegalArgumentException("Page doesn't contain an URL");
        }
        if (!page.getCookies().containsKey(COOKIE_NAME)) {
            throw new IllegalArgumentException("Cookie '" + COOKIE_NAME + "' is missing");
        }

        final StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
        final Map<String, List<String>> headers = new HashMap<>();
        YoutubeParsingHelper.addClientInfoHeaders(headers);

        final Response response = getDownloader().post(page.getUrl(), headers, page.getBody(),
                getExtractorLocalization());
        final JsonObject ajaxJson = JsonUtils.toJsonObject(YoutubeParsingHelper.getValidJsonResponseBody(response));
        final JsonObject playlistJson = ajaxJson.getObject("contents")
                .getObject("twoColumnWatchNextResults").getObject("playlist").getObject("playlist");
        final JsonArray allStreams = playlistJson.getArray("contents");
        // Sublist because YouTube returns up to 24 previous streams in the mix
        // +1 because the stream of "currentIndex" was already extracted in previous request
        final List<Object> newStreams =
                allStreams.subList(playlistJson.getInt("currentIndex") + 1, allStreams.size());

        collectStreamsFrom(collector, newStreams);
        return new InfoItemsPage<>(collector, getNextPageFrom(playlistJson, page.getCookies()));
    }

    private void collectStreamsFrom(@Nonnull final StreamInfoItemsCollector collector,
                                    @Nullable final List<Object> streams) {
        if (streams == null) {
            return;
        }

        final TimeAgoParser timeAgoParser = getTimeAgoParser();

        streams.stream()
                .filter(JsonObject.class::isInstance)
                .map(JsonObject.class::cast)
                .map(stream -> stream.getObject("playlistPanelVideoRenderer"))
                .filter(Objects::nonNull)
                .map(streamInfo -> new YoutubeStreamInfoItemExtractor(streamInfo, timeAgoParser))
                .forEachOrdered(collector::commit);
    }

    @Nonnull
    private String getThumbnailUrlFromPlaylistId(@Nonnull final String playlistId)
            throws ParsingException {
        return getThumbnailUrlFromVideoId(YoutubeParsingHelper.extractVideoIdFromMixId(playlistId));
    }

    @Nonnull
    private String getThumbnailUrlFromVideoId(final String videoId) {
        return "https://i.ytimg.com/vi/" + videoId + "/hqdefault.jpg";
    }

    @Nonnull
    @Override
    public PlaylistInfo.PlaylistType getPlaylistType() throws ParsingException {
        return YoutubeParsingHelper.extractPlaylistTypeFromPlaylistId(playlistData.getString("playlistId"));
    }
}

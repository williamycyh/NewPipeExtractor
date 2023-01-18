package vthirtylib.second.third.downdir.services.youtube.extractors;

import static vthirtylib.second.third.downdir.services.youtube.YoutubeParsingHelper.getTextFromObject;
import static vthirtylib.second.third.downdir.Utils.isNullOrEmpty;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonWriter;
import vthirtylib.second.third.downdir.playlist.PlaylistExtractor;
import vthirtylib.second.third.downdir.services.youtube.YoutubeParsingHelper;

import vthirtylib.second.third.downdir.Page;
import vthirtylib.second.third.downdir.StreamingService;
import vthirtylib.second.third.downdir.downloader.Downloader;
import vthirtylib.second.third.downdir.downloader.Response;
import vthirtylib.second.third.downdir.exceptions.ExtractionException;
import vthirtylib.second.third.downdir.exceptions.ParsingException;
import vthirtylib.second.third.downdir.linkhandler.ListLinkHandler;
import vthirtylib.second.third.downdir.localization.Localization;
import vthirtylib.second.third.downdir.localization.TimeAgoParser;
import vthirtylib.second.third.downdir.playlist.PlaylistInfo;
import vthirtylib.second.third.downdir.stream.StreamInfoItem;
import vthirtylib.second.third.downdir.stream.StreamInfoItemsCollector;
import vthirtylib.second.third.downdir.utils.JsonUtils;
import vthirtylib.second.third.downdir.Utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class YoutubePlaylistExtractor extends PlaylistExtractor {
    // Minimum size of the stats array in the browse response which includes the streams count
    private static final int STATS_ARRAY_WITH_STREAMS_COUNT_MIN_SIZE = 2;

    // Names of some objects in JSON response frequently used in this class
    private static final String PLAYLIST_VIDEO_RENDERER = "playlistVideoRenderer";
    private static final String PLAYLIST_VIDEO_LIST_RENDERER = "playlistVideoListRenderer";
    private static final String VIDEO_OWNER_RENDERER = "videoOwnerRenderer";

    private JsonObject browseResponse;
    private JsonObject playlistInfo;

    public YoutubePlaylistExtractor(final StreamingService service,
                                    final ListLinkHandler linkHandler) {
        super(service, linkHandler);
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader) throws IOException,
            ExtractionException {
        final Localization localization = getExtractorLocalization();
        final byte[] body = JsonWriter.string(YoutubeParsingHelper.prepareDesktopJsonBuilder(localization,
                        getExtractorContentCountry())
                        .value("browseId", "VL" + getId())
                        .value("params", "wgYCCAA%3D") // Show unavailable videos
                        .done())
                .getBytes(StandardCharsets.UTF_8);

        browseResponse = YoutubeParsingHelper.getJsonPostResponse("browse", body, localization);
        YoutubeParsingHelper.defaultAlertsCheck(browseResponse);

        playlistInfo = getPlaylistInfo();
    }

    private JsonObject getUploaderInfo() throws ParsingException {
        final JsonArray items = browseResponse.getObject("sidebar")
                .getObject("playlistSidebarRenderer")
                .getArray("items");

        JsonObject videoOwner = items.getObject(1)
                .getObject("playlistSidebarSecondaryInfoRenderer")
                .getObject("videoOwner");
        if (videoOwner.has(VIDEO_OWNER_RENDERER)) {
            return videoOwner.getObject(VIDEO_OWNER_RENDERER);
        }

        // we might want to create a loop here instead of using duplicated code
        videoOwner = items.getObject(items.size())
                .getObject("playlistSidebarSecondaryInfoRenderer")
                .getObject("videoOwner");
        if (videoOwner.has(VIDEO_OWNER_RENDERER)) {
            return videoOwner.getObject(VIDEO_OWNER_RENDERER);
        }
        throw new ParsingException("Could not get uploader info");
    }

    private JsonObject getPlaylistInfo() throws ParsingException {
        try {
            return browseResponse.getObject("sidebar")
                    .getObject("playlistSidebarRenderer")
                    .getArray("items")
                    .getObject(0)
                    .getObject("playlistSidebarPrimaryInfoRenderer");
        } catch (final Exception e) {
            throw new ParsingException("Could not get PlaylistInfo", e);
        }
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        final String name = YoutubeParsingHelper.getTextFromObject(playlistInfo.getObject("title"));
        if (!Utils.isNullOrEmpty(name)) {
            return name;
        }

        return browseResponse.getObject("microformat")
                .getObject("microformatDataRenderer")
                .getString("title");
    }

    @Nonnull
    @Override
    public String getThumbnailUrl() throws ParsingException {
        String url = playlistInfo.getObject("thumbnailRenderer")
                .getObject("playlistVideoThumbnailRenderer")
                .getObject("thumbnail")
                .getArray("thumbnails")
                .getObject(0)
                .getString("url");

        if (Utils.isNullOrEmpty(url)) {
            url = browseResponse.getObject("microformat")
                    .getObject("microformatDataRenderer")
                    .getObject("thumbnail")
                    .getArray("thumbnails")
                    .getObject(0)
                    .getString("url");

            if (Utils.isNullOrEmpty(url)) {
                throw new ParsingException("Could not get playlist thumbnail");
            }
        }

        return YoutubeParsingHelper.fixThumbnailUrl(url);
    }

    @Override
    public String getUploaderUrl() throws ParsingException {
        try {
            return YoutubeParsingHelper.getUrlFromNavigationEndpoint(getUploaderInfo().getObject("navigationEndpoint"));
        } catch (final Exception e) {
            throw new ParsingException("Could not get playlist uploader url", e);
        }
    }

    @Override
    public String getUploaderName() throws ParsingException {
        try {
            return YoutubeParsingHelper.getTextFromObject(getUploaderInfo().getObject("title"));
        } catch (final Exception e) {
            throw new ParsingException("Could not get playlist uploader name", e);
        }
    }

    @Override
    public String getUploaderAvatarUrl() throws ParsingException {
        try {
            final String url = getUploaderInfo()
                    .getObject("thumbnail")
                    .getArray("thumbnails")
                    .getObject(0)
                    .getString("url");

            return YoutubeParsingHelper.fixThumbnailUrl(url);
        } catch (final Exception e) {
            throw new ParsingException("Could not get playlist uploader avatar", e);
        }
    }

    @Override
    public boolean isUploaderVerified() throws ParsingException {
        // YouTube doesn't provide this information
        return false;
    }

    @Override
    public long getStreamCount() throws ParsingException {
        try {
            final JsonArray stats = playlistInfo.getArray("stats");
            // For unknown reasons, YouTube don't provide the stream count for learning playlists
            // on the desktop client but only the number of views and the playlist modified date
            // On normal playlists, at least 3 items are returned: the number of videos, the number
            // of views and the playlist modification date
            // We can get it by using another client, however it seems we can't get the avatar
            // uploader URL with another client than the WEB client
            if (stats.size() > STATS_ARRAY_WITH_STREAMS_COUNT_MIN_SIZE) {
                final String videosText = YoutubeParsingHelper.getTextFromObject(playlistInfo.getArray("stats")
                        .getObject(0));
                if (videosText != null) {
                    return Long.parseLong(Utils.removeNonDigitCharacters(videosText));
                }
            }

            return ITEM_COUNT_UNKNOWN;
        } catch (final Exception e) {
            throw new ParsingException("Could not get video count from playlist", e);
        }
    }

    @Nonnull
    @Override
    public String getSubChannelName() {
        return Utils.EMPTY_STRING;
    }

    @Nonnull
    @Override
    public String getSubChannelUrl() {
        return Utils.EMPTY_STRING;
    }

    @Nonnull
    @Override
    public String getSubChannelAvatarUrl() {
        return Utils.EMPTY_STRING;
    }

    @Nonnull
    @Override
    public InfoItemsPage<StreamInfoItem> getInitialPage() throws IOException, ExtractionException {
        final StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
        Page nextPage = null;

        final JsonArray contents = browseResponse.getObject("contents")
                .getObject("twoColumnBrowseResultsRenderer")
                .getArray("tabs")
                .getObject(0)
                .getObject("tabRenderer")
                .getObject("content")
                .getObject("sectionListRenderer")
                .getArray("contents");

        final JsonObject videoPlaylistObject = contents.stream()
                .filter(JsonObject.class::isInstance)
                .map(JsonObject.class::cast)
                .map(content -> content.getObject("itemSectionRenderer")
                        .getArray("contents")
                        .getObject(0))
                .filter(contentItemSectionRendererContents ->
                        contentItemSectionRendererContents.has(PLAYLIST_VIDEO_LIST_RENDERER)
                                || contentItemSectionRendererContents.has(
                                "playlistSegmentRenderer"))
                .findFirst()
                .orElse(null);

        if (videoPlaylistObject != null && videoPlaylistObject.has(PLAYLIST_VIDEO_LIST_RENDERER)) {
            final JsonArray videosArray = videoPlaylistObject
                    .getObject(PLAYLIST_VIDEO_LIST_RENDERER)
                    .getArray("contents");
            collectStreamsFrom(collector, videosArray);

            nextPage = getNextPageFrom(videosArray);
        }

        return new InfoItemsPage<>(collector, nextPage);
    }

    @Override
    public InfoItemsPage<StreamInfoItem> getPage(final Page page) throws IOException,
            ExtractionException {
        if (page == null || Utils.isNullOrEmpty(page.getUrl())) {
            throw new IllegalArgumentException("Page doesn't contain an URL");
        }

        final StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
        final Map<String, List<String>> headers = new HashMap<>();
        YoutubeParsingHelper.addClientInfoHeaders(headers);

        final Response response = getDownloader().post(page.getUrl(), headers, page.getBody(),
                getExtractorLocalization());
        final JsonObject ajaxJson = JsonUtils.toJsonObject(YoutubeParsingHelper.getValidJsonResponseBody(response));

        final JsonArray continuation = ajaxJson.getArray("onResponseReceivedActions")
                .getObject(0)
                .getObject("appendContinuationItemsAction")
                .getArray("continuationItems");

        collectStreamsFrom(collector, continuation);

        return new InfoItemsPage<>(collector, getNextPageFrom(continuation));
    }

    @Nullable
    private Page getNextPageFrom(final JsonArray contents)
            throws IOException, ExtractionException {
        if (Utils.isNullOrEmpty(contents)) {
            return null;
        }

        final JsonObject lastElement = contents.getObject(contents.size() - 1);
        if (lastElement.has("continuationItemRenderer")) {
            final String continuation = lastElement
                    .getObject("continuationItemRenderer")
                    .getObject("continuationEndpoint")
                    .getObject("continuationCommand")
                    .getString("token");

            final byte[] body = JsonWriter.string(YoutubeParsingHelper.prepareDesktopJsonBuilder(
                            getExtractorLocalization(), getExtractorContentCountry())
                            .value("continuation", continuation)
                            .done())
                    .getBytes(StandardCharsets.UTF_8);

            return new Page(YoutubeParsingHelper.YOUTUBEI_V1_URL + "browse?key=" + YoutubeParsingHelper.getKey()
                    + YoutubeParsingHelper.DISABLE_PRETTY_PRINT_PARAMETER, body);
        } else {
            return null;
        }
    }

    private void collectStreamsFrom(@Nonnull final StreamInfoItemsCollector collector,
                                    @Nonnull final JsonArray videos) {
        final TimeAgoParser timeAgoParser = getTimeAgoParser();

        videos.stream()
                .filter(JsonObject.class::isInstance)
                .map(JsonObject.class::cast)
                .filter(video -> video.has(PLAYLIST_VIDEO_RENDERER))
                .map(video -> new YoutubeStreamInfoItemExtractor(
                        video.getObject(PLAYLIST_VIDEO_RENDERER), timeAgoParser) {
                    @Override
                    public long getViewCount() {
                        return -1;
                    }
                })
                .forEachOrdered(collector::commit);
    }

    @Nonnull
    @Override
    public PlaylistInfo.PlaylistType getPlaylistType() throws ParsingException {
        return YoutubeParsingHelper.extractPlaylistTypeFromPlaylistUrl(getUrl());
    }
}

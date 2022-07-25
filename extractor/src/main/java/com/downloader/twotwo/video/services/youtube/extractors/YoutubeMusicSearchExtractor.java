package com.downloader.twotwo.video.services.youtube.extractors;

import static com.downloader.twotwo.video.services.youtube.YoutubeParsingHelper.getTextFromObject;
import static com.downloader.twotwo.video.utils.Utils.EMPTY_STRING;
import static com.downloader.twotwo.video.utils.Utils.UTF_8;
import static com.downloader.twotwo.video.utils.Utils.isNullOrEmpty;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import com.grack.nanojson.JsonWriter;
import com.downloader.twotwo.video.services.youtube.YoutubeParsingHelper;
import com.downloader.twotwo.video.services.youtube.linkHandler.YoutubeSearchQueryHandlerFactory;

import com.downloader.twotwo.video.InfoItem;
import com.downloader.twotwo.video.MetaInfo;
import com.downloader.twotwo.video.Page;
import com.downloader.twotwo.video.StreamingService;
import com.downloader.twotwo.video.downloader.Downloader;
import com.downloader.twotwo.video.exceptions.ExtractionException;
import com.downloader.twotwo.video.exceptions.ParsingException;
import com.downloader.twotwo.video.exceptions.ReCaptchaException;
import com.downloader.twotwo.video.linkhandler.SearchQueryHandler;
import com.downloader.twotwo.video.localization.DateWrapper;
import com.downloader.twotwo.video.localization.TimeAgoParser;
import com.downloader.twotwo.video.MultiInfoItemsCollector;
import com.downloader.twotwo.video.search.SearchExtractor;
import com.downloader.twotwo.video.utils.JsonUtils;
import com.downloader.twotwo.video.utils.Parser;
import com.downloader.twotwo.video.utils.Utils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class YoutubeMusicSearchExtractor extends SearchExtractor {
    private JsonObject initialData;

    public YoutubeMusicSearchExtractor(final StreamingService service,
                                       final SearchQueryHandler linkHandler) {
        super(service, linkHandler);
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader)
            throws IOException, ExtractionException {
        final String[] youtubeMusicKeys = YoutubeParsingHelper.getYoutubeMusicKey();

        final String url = "https://music.youtube.com/youtubei/v1/search?alt=json&key="
                + youtubeMusicKeys[0] + YoutubeParsingHelper.DISABLE_PRETTY_PRINT_PARAMETER;

        final String params;

        switch (getLinkHandler().getContentFilters().get(0)) {
            case YoutubeSearchQueryHandlerFactory.MUSIC_SONGS:
                params = "Eg-KAQwIARAAGAAgACgAMABqChAEEAUQAxAKEAk%3D";
                break;
            case YoutubeSearchQueryHandlerFactory.MUSIC_VIDEOS:
                params = "Eg-KAQwIABABGAAgACgAMABqChAEEAUQAxAKEAk%3D";
                break;
            case YoutubeSearchQueryHandlerFactory.MUSIC_ALBUMS:
                params = "Eg-KAQwIABAAGAEgACgAMABqChAEEAUQAxAKEAk%3D";
                break;
            case YoutubeSearchQueryHandlerFactory.MUSIC_PLAYLISTS:
                params = "Eg-KAQwIABAAGAAgACgBMABqChAEEAUQAxAKEAk%3D";
                break;
            case YoutubeSearchQueryHandlerFactory.MUSIC_ARTISTS:
                params = "Eg-KAQwIABAAGAAgASgAMABqChAEEAUQAxAKEAk%3D";
                break;
            default:
                params = null;
                break;
        }

        // @formatter:off
        final byte[] json = JsonWriter.string()
            .object()
                .object("context")
                    .object("client")
                        .value("clientName", "WEB_REMIX")
                        .value("clientVersion", youtubeMusicKeys[2])
                        .value("hl", "en-GB")
                        .value("gl", getExtractorContentCountry().getCountryCode())
                        .array("experimentIds").end()
                        .value("experimentsToken", EMPTY_STRING)
                        .object("locationInfo").end()
                        .object("musicAppInfo").end()
                    .end()
                    .object("capabilities").end()
                    .object("request")
                        .array("internalExperimentFlags").end()
                        .object("sessionIndex").end()
                    .end()
                    .object("activePlayers").end()
                    .object("user")
                        // TO DO: provide a way to enable restricted mode with:
                        .value("enableSafetyMode", false)
                    .end()
                .end()
                .value("query", getSearchString())
                .value("params", params)
            .end().done().getBytes(UTF_8);
        // @formatter:on

        final Map<String, List<String>> headers = new HashMap<>();
        headers.put("X-YouTube-Client-Name", Collections.singletonList(youtubeMusicKeys[1]));
        headers.put("X-YouTube-Client-Version", Collections.singletonList(youtubeMusicKeys[2]));
        headers.put("Origin", Collections.singletonList("https://music.youtube.com"));
        headers.put("Referer", Collections.singletonList("music.youtube.com"));
        headers.put("Content-Type", Collections.singletonList("application/json"));

        final String responseBody = YoutubeParsingHelper.getValidJsonResponseBody(getDownloader().post(url, headers,
                json));

        try {
            initialData = JsonParser.object().from(responseBody);
        } catch (final JsonParserException e) {
            throw new ParsingException("Could not parse JSON", e);
        }
    }

    @Nonnull
    @Override
    public String getUrl() throws ParsingException {
        return super.getUrl();
    }

    @Nonnull
    @Override
    public String getSearchSuggestion() throws ParsingException {
        final JsonObject itemSectionRenderer = JsonUtils.getArray(JsonUtils.getArray(initialData,
                "contents.tabbedSearchResultsRenderer.tabs").getObject(0),
                "tabRenderer.content.sectionListRenderer.contents")
                .getObject(0)
                .getObject("itemSectionRenderer");
        if (itemSectionRenderer.isEmpty()) {
            return "";
        }

        final JsonObject didYouMeanRenderer = itemSectionRenderer.getArray("contents")
                .getObject(0).getObject("didYouMeanRenderer");
        final JsonObject showingResultsForRenderer = itemSectionRenderer.getArray("contents")
                .getObject(0)
                .getObject("showingResultsForRenderer");

        if (!didYouMeanRenderer.isEmpty()) {
            return YoutubeParsingHelper.getTextFromObject(didYouMeanRenderer.getObject("correctedQuery"));
        } else if (!showingResultsForRenderer.isEmpty()) {
            return JsonUtils.getString(showingResultsForRenderer,
                    "correctedQueryEndpoint.searchEndpoint.query");
        } else {
            return "";
        }
    }

    @Override
    public boolean isCorrectedSearch() throws ParsingException {
        final JsonObject itemSectionRenderer = JsonUtils.getArray(JsonUtils.getArray(initialData,
                "contents.tabbedSearchResultsRenderer.tabs").getObject(0),
                "tabRenderer.content.sectionListRenderer.contents")
                .getObject(0)
                .getObject("itemSectionRenderer");
        if (itemSectionRenderer.isEmpty()) {
            return false;
        }

        final JsonObject firstContent = itemSectionRenderer.getArray("contents").getObject(0);

        return firstContent.has("didYouMeanRenderer")
                || firstContent.has("showingResultsForRenderer");
    }

    @Nonnull
    @Override
    public List<MetaInfo> getMetaInfo() {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public InfoItemsPage<InfoItem> getInitialPage() throws IOException, ExtractionException {
        final MultiInfoItemsCollector collector = new MultiInfoItemsCollector(getServiceId());

        final JsonArray contents = JsonUtils.getArray(JsonUtils.getArray(initialData,
                "contents.tabbedSearchResultsRenderer.tabs").getObject(0),
                "tabRenderer.content.sectionListRenderer.contents");

        Page nextPage = null;

        for (final Object content : contents) {
            if (((JsonObject) content).has("musicShelfRenderer")) {
                final JsonObject musicShelfRenderer = ((JsonObject) content)
                        .getObject("musicShelfRenderer");

                collectMusicStreamsFrom(collector, musicShelfRenderer.getArray("contents"));

                nextPage = getNextPageFrom(musicShelfRenderer.getArray("continuations"));
            }
        }

        return new InfoItemsPage<>(collector, nextPage);
    }

    @Override
    public InfoItemsPage<InfoItem> getPage(final Page page)
            throws IOException, ExtractionException {
        if (page == null || isNullOrEmpty(page.getUrl())) {
            throw new IllegalArgumentException("Page doesn't contain an URL");
        }

        final MultiInfoItemsCollector collector = new MultiInfoItemsCollector(getServiceId());

        final String[] youtubeMusicKeys = YoutubeParsingHelper.getYoutubeMusicKey();

        // @formatter:off
        final byte[] json = JsonWriter.string()
            .object()
                .object("context")
                    .object("client")
                        .value("clientName", "WEB_REMIX")
                        .value("clientVersion", youtubeMusicKeys[2])
                        .value("hl", "en")
                        .value("gl", getExtractorContentCountry().getCountryCode())
                        .array("experimentIds").end()
                        .value("experimentsToken", "")
                        .value("utcOffsetMinutes", 0)
                        .object("locationInfo").end()
                        .object("musicAppInfo").end()
                    .end()
                    .object("capabilities").end()
                    .object("request")
                        .array("internalExperimentFlags").end()
                        .object("sessionIndex").end()
                    .end()
                    .object("activePlayers").end()
                    .object("user")
                        .value("enableSafetyMode", false)
                    .end()
                .end()
            .end().done().getBytes(UTF_8);
        // @formatter:on

        final Map<String, List<String>> headers = new HashMap<>();
        headers.put("X-YouTube-Client-Name", Collections.singletonList(youtubeMusicKeys[1]));
        headers.put("X-YouTube-Client-Version", Collections.singletonList(youtubeMusicKeys[2]));
        headers.put("Origin", Collections.singletonList("https://music.youtube.com"));
        headers.put("Referer", Collections.singletonList("music.youtube.com"));
        headers.put("Content-Type", Collections.singletonList("application/json"));

        final String responseBody = YoutubeParsingHelper.getValidJsonResponseBody(getDownloader().post(page.getUrl(),
                headers, json));

        final JsonObject ajaxJson;
        try {
            ajaxJson = JsonParser.object().from(responseBody);
        } catch (final JsonParserException e) {
            throw new ParsingException("Could not parse JSON", e);
        }

        final JsonObject musicShelfContinuation = ajaxJson.getObject("continuationContents")
                .getObject("musicShelfContinuation");

        collectMusicStreamsFrom(collector, musicShelfContinuation.getArray("contents"));
        final JsonArray continuations = musicShelfContinuation.getArray("continuations");

        return new InfoItemsPage<>(collector, getNextPageFrom(continuations));
    }

    @SuppressWarnings("MethodLength")
    private void collectMusicStreamsFrom(final MultiInfoItemsCollector collector,
                                         @Nonnull final JsonArray videos) {
        final TimeAgoParser timeAgoParser = getTimeAgoParser();

        for (final Object item : videos) {
            final JsonObject info = ((JsonObject) item)
                    .getObject("musicResponsiveListItemRenderer", null);
            if (info != null) {
                final String displayPolicy = info.getString("musicItemRendererDisplayPolicy",
                        EMPTY_STRING);
                if (displayPolicy.equals("MUSIC_ITEM_RENDERER_DISPLAY_POLICY_GREY_OUT")) {
                    continue; // No info about video URL available
                }

                final JsonObject flexColumnRenderer = info.getArray("flexColumns")
                        .getObject(1)
                        .getObject("musicResponsiveListItemFlexColumnRenderer");
                final JsonArray descriptionElements = flexColumnRenderer.getObject("text")
                        .getArray("runs");
                final String searchType = getLinkHandler().getContentFilters().get(0);
                if (searchType.equals(YoutubeSearchQueryHandlerFactory.MUSIC_SONGS) || searchType.equals(YoutubeSearchQueryHandlerFactory.MUSIC_VIDEOS)) {
                    collector.commit(new YoutubeStreamInfoItemExtractor(info, timeAgoParser) {
                        @Override
                        public String getUrl() throws ParsingException {
                            final String id = info.getObject("playlistItemData")
                                    .getString("videoId");
                            if (!isNullOrEmpty(id)) {
                                return "https://music.youtube.com/watch?v=" + id;
                            }
                            throw new ParsingException("Could not get url");
                        }

                        @Override
                        public String getName() throws ParsingException {
                            final String name = YoutubeParsingHelper.getTextFromObject(info.getArray("flexColumns")
                                    .getObject(0)
                                    .getObject("musicResponsiveListItemFlexColumnRenderer")
                                    .getObject("text"));
                            if (!isNullOrEmpty(name)) {
                                return name;
                            }
                            throw new ParsingException("Could not get name");
                        }

                        @Override
                        public long getDuration() throws ParsingException {
                            final String duration = descriptionElements
                                    .getObject(descriptionElements.size() - 1)
                                    .getString("text");
                            if (!isNullOrEmpty(duration)) {
                                return YoutubeParsingHelper.parseDurationString(duration);
                            }
                            throw new ParsingException("Could not get duration");
                        }

                        @Override
                        public String getUploaderName() throws ParsingException {
                            final String name = descriptionElements.getObject(0).getString("text");
                            if (!isNullOrEmpty(name)) {
                                return name;
                            }
                            throw new ParsingException("Could not get uploader name");
                        }

                        @Override
                        public String getUploaderUrl() throws ParsingException {
                            if (searchType.equals(YoutubeSearchQueryHandlerFactory.MUSIC_VIDEOS)) {
                                final JsonArray items = info.getObject("menu")
                                        .getObject("menuRenderer")
                                        .getArray("items");
                                for (final Object item : items) {
                                    final JsonObject menuNavigationItemRenderer =
                                            ((JsonObject) item).getObject(
                                                    "menuNavigationItemRenderer");
                                    if (menuNavigationItemRenderer.getObject("icon")
                                            .getString("iconType", EMPTY_STRING)
                                            .equals("ARTIST")) {
                                        return YoutubeParsingHelper.getUrlFromNavigationEndpoint(
                                                menuNavigationItemRenderer
                                                        .getObject("navigationEndpoint"));
                                    }
                                }

                                return null;
                            } else {
                                final JsonObject navigationEndpointHolder = info
                                        .getArray("flexColumns")
                                        .getObject(1)
                                        .getObject("musicResponsiveListItemFlexColumnRenderer")
                                        .getObject("text").getArray("runs").getObject(0);

                                if (!navigationEndpointHolder.has("navigationEndpoint")) {
                                    return null;
                                }

                                final String url = YoutubeParsingHelper.getUrlFromNavigationEndpoint(
                                        navigationEndpointHolder.getObject("navigationEndpoint"));

                                if (!isNullOrEmpty(url)) {
                                    return url;
                                }

                                throw new ParsingException("Could not get uploader URL");
                            }
                        }

                        @Override
                        public String getTextualUploadDate() {
                            return null;
                        }

                        @Override
                        public DateWrapper getUploadDate() {
                            return null;
                        }

                        @Override
                        public long getViewCount() throws ParsingException {
                            if (searchType.equals(YoutubeSearchQueryHandlerFactory.MUSIC_SONGS)) {
                                return -1;
                            }
                            final String viewCount = descriptionElements
                                    .getObject(descriptionElements.size() - 3)
                                    .getString("text");
                            if (!isNullOrEmpty(viewCount)) {
                                try {
                                    return Utils.mixedNumberWordToLong(viewCount);
                                } catch (final Parser.RegexException e) {
                                    // probably viewCount == "No views" or similar
                                    return 0;
                                }
                            }
                            throw new ParsingException("Could not get view count");
                        }

                        @Override
                        public String getThumbnailUrl() throws ParsingException {
                            try {
                                final JsonArray thumbnails = info.getObject("thumbnail")
                                        .getObject("musicThumbnailRenderer")
                                        .getObject("thumbnail").getArray("thumbnails");
                                // the last thumbnail is the one with the highest resolution
                                final String url = thumbnails.getObject(thumbnails.size() - 1)
                                        .getString("url");

                                return YoutubeParsingHelper.fixThumbnailUrl(url);
                            } catch (final Exception e) {
                                throw new ParsingException("Could not get thumbnail url", e);
                            }
                        }
                    });
                } else if (searchType.equals(YoutubeSearchQueryHandlerFactory.MUSIC_ARTISTS)) {
                    collector.commit(new YoutubeChannelInfoItemExtractor(info) {
                        @Override
                        public String getThumbnailUrl() throws ParsingException {
                            try {
                                final JsonArray thumbnails = info.getObject("thumbnail")
                                        .getObject("musicThumbnailRenderer")
                                        .getObject("thumbnail").getArray("thumbnails");
                                // the last thumbnail is the one with the highest resolution
                                final String url = thumbnails.getObject(thumbnails.size() - 1)
                                        .getString("url");

                                return YoutubeParsingHelper.fixThumbnailUrl(url);
                            } catch (final Exception e) {
                                throw new ParsingException("Could not get thumbnail url", e);
                            }
                        }

                        @Override
                        public String getName() throws ParsingException {
                            final String name = YoutubeParsingHelper.getTextFromObject(info.getArray("flexColumns")
                                    .getObject(0)
                                    .getObject("musicResponsiveListItemFlexColumnRenderer")
                                    .getObject("text"));
                            if (!isNullOrEmpty(name)) {
                                return name;
                            }
                            throw new ParsingException("Could not get name");
                        }

                        @Override
                        public String getUrl() throws ParsingException {
                            final String url = YoutubeParsingHelper.getUrlFromNavigationEndpoint(info
                                    .getObject("navigationEndpoint"));
                            if (!isNullOrEmpty(url)) {
                                return url;
                            }
                            throw new ParsingException("Could not get url");
                        }

                        @Override
                        public long getSubscriberCount() throws ParsingException {
                            final String subscriberCount = YoutubeParsingHelper.getTextFromObject(info
                                    .getArray("flexColumns").getObject(2)
                                    .getObject("musicResponsiveListItemFlexColumnRenderer")
                                    .getObject("text"));
                            if (!isNullOrEmpty(subscriberCount)) {
                                try {
                                    return Utils.mixedNumberWordToLong(subscriberCount);
                                } catch (final Parser.RegexException ignored) {
                                    // probably subscriberCount == "No subscribers" or similar
                                    return 0;
                                }
                            }
                            throw new ParsingException("Could not get subscriber count");
                        }

                        @Override
                        public long getStreamCount() {
                            return -1;
                        }

                        @Override
                        public String getDescription() {
                            return null;
                        }
                    });
                } else if (searchType.equals(YoutubeSearchQueryHandlerFactory.MUSIC_ALBUMS) || searchType.equals(YoutubeSearchQueryHandlerFactory.MUSIC_PLAYLISTS)) {
                    collector.commit(new YoutubePlaylistInfoItemExtractor(info) {
                        @Override
                        public String getThumbnailUrl() throws ParsingException {
                            try {
                                final JsonArray thumbnails = info.getObject("thumbnail")
                                        .getObject("musicThumbnailRenderer")
                                        .getObject("thumbnail").getArray("thumbnails");
                                // the last thumbnail is the one with the highest resolution
                                final String url = thumbnails.getObject(thumbnails.size() - 1)
                                        .getString("url");

                                return YoutubeParsingHelper.fixThumbnailUrl(url);
                            } catch (final Exception e) {
                                throw new ParsingException("Could not get thumbnail url", e);
                            }
                        }

                        @Override
                        public String getName() throws ParsingException {
                            final String name = YoutubeParsingHelper.getTextFromObject(info.getArray("flexColumns")
                                    .getObject(0)
                                    .getObject("musicResponsiveListItemFlexColumnRenderer")
                                    .getObject("text"));
                            if (!isNullOrEmpty(name)) {
                                return name;
                            }
                            throw new ParsingException("Could not get name");
                        }

                        @Override
                        public String getUrl() throws ParsingException {
                            String playlistId = info.getObject("menu")
                                    .getObject("menuRenderer")
                                    .getArray("items")
                                    .getObject(4)
                                    .getObject("toggleMenuServiceItemRenderer")
                                    .getObject("toggledServiceEndpoint")
                                    .getObject("likeEndpoint")
                                    .getObject("target")
                                    .getString("playlistId");

                            if (isNullOrEmpty(playlistId)) {
                                playlistId = info.getObject("overlay")
                                        .getObject("musicItemThumbnailOverlayRenderer")
                                        .getObject("content")
                                        .getObject("musicPlayButtonRenderer")
                                        .getObject("playNavigationEndpoint")
                                        .getObject("watchPlaylistEndpoint")
                                        .getString("playlistId");
                            }
                            if (!isNullOrEmpty(playlistId)) {
                                return "https://music.youtube.com/playlist?list=" + playlistId;
                            }
                            throw new ParsingException("Could not get url");
                        }

                        @Override
                        public String getUploaderName() throws ParsingException {
                            final String name;
                            if (searchType.equals(YoutubeSearchQueryHandlerFactory.MUSIC_ALBUMS)) {
                                name = descriptionElements.getObject(2).getString("text");
                            } else {
                                name = descriptionElements.getObject(0).getString("text");
                            }
                            if (!isNullOrEmpty(name)) {
                                return name;
                            }
                            throw new ParsingException("Could not get uploader name");
                        }

                        @Override
                        public long getStreamCount() throws ParsingException {
                            if (searchType.equals(YoutubeSearchQueryHandlerFactory.MUSIC_ALBUMS)) {
                                return ITEM_COUNT_UNKNOWN;
                            }
                            final String count = descriptionElements.getObject(2)
                                    .getString("text");
                            if (!isNullOrEmpty(count)) {
                                if (count.contains("100+")) {
                                    return ITEM_COUNT_MORE_THAN_100;
                                } else {
                                    return Long.parseLong(Utils.removeNonDigitCharacters(count));
                                }
                            }
                            throw new ParsingException("Could not get count");
                        }
                    });
                }
            }
        }
    }

    @Nullable
    private Page getNextPageFrom(final JsonArray continuations)
            throws IOException, ParsingException, ReCaptchaException {
        if (isNullOrEmpty(continuations)) {
            return null;
        }

        final JsonObject nextContinuationData = continuations.getObject(0)
                .getObject("nextContinuationData");
        final String continuation = nextContinuationData.getString("continuation");

        return new Page("https://music.youtube.com/youtubei/v1/search?ctoken=" + continuation
                + "&continuation=" + continuation + "&alt=json" + "&key="
                + YoutubeParsingHelper.getYoutubeMusicKey()[0]);
    }
}

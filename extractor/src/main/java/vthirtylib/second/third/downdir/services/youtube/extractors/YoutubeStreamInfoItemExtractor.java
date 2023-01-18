package vthirtylib.second.third.downdir.services.youtube.extractors;

import static vthirtylib.second.third.downdir.services.youtube.YoutubeParsingHelper.getTextFromObject;
import static vthirtylib.second.third.downdir.Utils.isNullOrEmpty;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import vthirtylib.second.third.downdir.services.youtube.YoutubeParsingHelper;
import vthirtylib.second.third.downdir.services.youtube.linkHandler.YoutubeStreamLinkHandlerFactory;

import vthirtylib.second.third.downdir.exceptions.ParsingException;
import vthirtylib.second.third.downdir.localization.DateWrapper;
import vthirtylib.second.third.downdir.localization.TimeAgoParser;
import vthirtylib.second.third.downdir.stream.StreamInfoItemExtractor;
import vthirtylib.second.third.downdir.stream.StreamType;
import vthirtylib.second.third.downdir.utils.JsonUtils;
import vthirtylib.second.third.downdir.Utils;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import javax.annotation.Nullable;

/*
 * Copyright (C) Christian Schabesberger 2016 <chris.schabesberger@mailbox.org>
 * YoutubeStreamInfoItemExtractor.java is part of NewPipe.
 *
 * NewPipe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NewPipe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NewPipe.  If not, see <http://www.gnu.org/licenses/>.
 */

public class YoutubeStreamInfoItemExtractor implements StreamInfoItemExtractor {
    private final JsonObject videoInfo;
    private final TimeAgoParser timeAgoParser;
    private StreamType cachedStreamType;

    /**
     * Creates an extractor of StreamInfoItems from a YouTube page.
     *
     * @param videoInfoItem The JSON page element
     * @param timeAgoParser A parser of the textual dates or {@code null}.
     */
    public YoutubeStreamInfoItemExtractor(final JsonObject videoInfoItem,
                                          @Nullable final TimeAgoParser timeAgoParser) {
        this.videoInfo = videoInfoItem;
        this.timeAgoParser = timeAgoParser;
    }

    @Override
    public StreamType getStreamType() {
        if (cachedStreamType != null) {
            return cachedStreamType;
        }

        final JsonArray badges = videoInfo.getArray("badges");
        for (final Object badge : badges) {
            final JsonObject badgeRenderer
                    = ((JsonObject) badge).getObject("metadataBadgeRenderer");
            if (badgeRenderer.getString("style", Utils.EMPTY_STRING).equals("BADGE_STYLE_TYPE_LIVE_NOW")
                    || badgeRenderer.getString("label", Utils.EMPTY_STRING).equals("LIVE NOW")) {
                cachedStreamType = StreamType.LIVE_STREAM;
                return cachedStreamType;
            }
        }

        for (final Object overlay : videoInfo.getArray("thumbnailOverlays")) {
            final String style = ((JsonObject) overlay)
                    .getObject("thumbnailOverlayTimeStatusRenderer")
                    .getString("style", Utils.EMPTY_STRING);
            if (style.equalsIgnoreCase("LIVE")) {
                cachedStreamType = StreamType.LIVE_STREAM;
                return cachedStreamType;
            }
        }

        cachedStreamType = StreamType.VIDEO_STREAM;
        return cachedStreamType;
    }

    @Override
    public boolean isAd() throws ParsingException {
        return isPremium() || getName().equals("[Private video]")
                || getName().equals("[Deleted video]");
    }

    @Override
    public String getUrl() throws ParsingException {
        try {
            final String videoId = videoInfo.getString("videoId");
            return YoutubeStreamLinkHandlerFactory.getInstance().getUrl(videoId);
        } catch (final Exception e) {
            throw new ParsingException("Could not get url", e);
        }
    }

    @Override
    public String getName() throws ParsingException {
        final String name = YoutubeParsingHelper.getTextFromObject(videoInfo.getObject("title"));
        if (!Utils.isNullOrEmpty(name)) {
            return name;
        }
        throw new ParsingException("Could not get name");
    }

    @Override
    public long getDuration() throws ParsingException {
        if (getStreamType() == StreamType.LIVE_STREAM || isPremiere()) {
            return -1;
        }

        String duration = YoutubeParsingHelper.getTextFromObject(videoInfo.getObject("lengthText"));

        if (Utils.isNullOrEmpty(duration)) {
            for (final Object thumbnailOverlay : videoInfo.getArray("thumbnailOverlays")) {
                if (((JsonObject) thumbnailOverlay).has("thumbnailOverlayTimeStatusRenderer")) {
                    duration = YoutubeParsingHelper.getTextFromObject(((JsonObject) thumbnailOverlay)
                            .getObject("thumbnailOverlayTimeStatusRenderer").getObject("text"));
                }
            }

            if (Utils.isNullOrEmpty(duration)) {
                throw new ParsingException("Could not get duration");
            }
        }

        // NewPipe#8034 - YT returns not a correct duration for "YT shorts" videos
        if ("SHORTS".equalsIgnoreCase(duration)) {
            return 0;
        }

        return YoutubeParsingHelper.parseDurationString(duration);
    }

    @Override
    public String getUploaderName() throws ParsingException {
        String name = YoutubeParsingHelper.getTextFromObject(videoInfo.getObject("longBylineText"));

        if (Utils.isNullOrEmpty(name)) {
            name = YoutubeParsingHelper.getTextFromObject(videoInfo.getObject("ownerText"));

            if (Utils.isNullOrEmpty(name)) {
                name = YoutubeParsingHelper.getTextFromObject(videoInfo.getObject("shortBylineText"));

                if (Utils.isNullOrEmpty(name)) {
                    throw new ParsingException("Could not get uploader name");
                }
            }
        }

        return name;
    }

    @Override
    public String getUploaderUrl() throws ParsingException {
        String url = YoutubeParsingHelper.getUrlFromNavigationEndpoint(videoInfo.getObject("longBylineText")
                .getArray("runs").getObject(0).getObject("navigationEndpoint"));

        if (Utils.isNullOrEmpty(url)) {
            url = YoutubeParsingHelper.getUrlFromNavigationEndpoint(videoInfo.getObject("ownerText")
                    .getArray("runs").getObject(0).getObject("navigationEndpoint"));

            if (Utils.isNullOrEmpty(url)) {
                url = YoutubeParsingHelper.getUrlFromNavigationEndpoint(videoInfo.getObject("shortBylineText")
                        .getArray("runs").getObject(0).getObject("navigationEndpoint"));

                if (Utils.isNullOrEmpty(url)) {
                    throw new ParsingException("Could not get uploader url");
                }
            }
        }

        return url;
    }

    @Nullable
    @Override
    public String getUploaderAvatarUrl() throws ParsingException {

        if (videoInfo.has("channelThumbnailSupportedRenderers")) {
            return JsonUtils.getArray(videoInfo, "channelThumbnailSupportedRenderers"
                    + ".channelThumbnailWithLinkRenderer.thumbnail.thumbnails")
                    .getObject(0).getString("url");
        }

        if (videoInfo.has("channelThumbnail")) {
            return JsonUtils.getArray(videoInfo, "channelThumbnail.thumbnails")
                    .getObject(0).getString("url");
        }

        return null;
    }

    @Override
    public boolean isUploaderVerified() throws ParsingException {
        return YoutubeParsingHelper.isVerified(videoInfo.getArray("ownerBadges"));
    }

    @Nullable
    @Override
    public String getTextualUploadDate() throws ParsingException {
        if (getStreamType().equals(StreamType.LIVE_STREAM)) {
            return null;
        }

        if (isPremiere()) {
            return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(getDateFromPremiere());
        }

        final String publishedTimeText
                = YoutubeParsingHelper.getTextFromObject(videoInfo.getObject("publishedTimeText"));
        if (publishedTimeText != null && !publishedTimeText.isEmpty()) {
            return publishedTimeText;
        }

        return null;
    }

    @Nullable
    @Override
    public DateWrapper getUploadDate() throws ParsingException {
        if (getStreamType().equals(StreamType.LIVE_STREAM)) {
            return null;
        }

        if (isPremiere()) {
            return new DateWrapper(getDateFromPremiere());
        }

        final String textualUploadDate = getTextualUploadDate();
        if (timeAgoParser != null && !Utils.isNullOrEmpty(textualUploadDate)) {
            try {
                return timeAgoParser.parse(textualUploadDate);
            } catch (final ParsingException e) {
                throw new ParsingException("Could not get upload date", e);
            }
        }
        return null;
    }

    @Override
    public long getViewCount() throws ParsingException {
        try {
            if (videoInfo.has("topStandaloneBadge") || isPremium()) {
                return -1;
            }

            if (!videoInfo.has("viewCountText")) {
                // This object is null when a video has its views hidden.
                return -1;
            }

            final String viewCount = YoutubeParsingHelper.getTextFromObject(videoInfo.getObject("viewCountText"));

            if (viewCount.toLowerCase().contains("no views")) {
                return 0;
            } else if (viewCount.toLowerCase().contains("recommended")) {
                return -1;
            }

            return Long.parseLong(Utils.removeNonDigitCharacters(viewCount));
        } catch (final Exception e) {
            throw new ParsingException("Could not get view count", e);
        }
    }

    @Override
    public String getThumbnailUrl() throws ParsingException {
        return YoutubeParsingHelper.getThumbnailUrlFromInfoItem(videoInfo);
    }

    private boolean isPremium() {
        final JsonArray badges = videoInfo.getArray("badges");
        for (final Object badge : badges) {
            if (((JsonObject) badge).getObject("metadataBadgeRenderer")
                    .getString("label", Utils.EMPTY_STRING).equals("Premium")) {
                return true;
            }
        }
        return false;
    }

    private boolean isPremiere() {
        return videoInfo.has("upcomingEventData");
    }

    private OffsetDateTime getDateFromPremiere() throws ParsingException {
        final JsonObject upcomingEventData = videoInfo.getObject("upcomingEventData");
        final String startTime = upcomingEventData.getString("startTime");

        try {
            return OffsetDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(startTime)),
                    ZoneOffset.UTC);
        } catch (final Exception e) {
            throw new ParsingException("Could not parse date from premiere: \"" + startTime + "\"");
        }
    }

    @Nullable
    @Override
    public String getShortDescription() throws ParsingException {

        if (videoInfo.has("detailedMetadataSnippets")) {
            return YoutubeParsingHelper.getTextFromObject(videoInfo.getArray("detailedMetadataSnippets")
                    .getObject(0).getObject("snippetText"));
        }

        if (videoInfo.has("descriptionSnippet")) {
            return YoutubeParsingHelper.getTextFromObject(videoInfo.getObject("descriptionSnippet"));
        }

        return null;
    }
}

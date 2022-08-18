// Created by Fynn Godau 2019, licensed GNU GPL version 3 or later

package com.downloader.twotwo.video.services.bandcamp.extractors;

import static com.downloader.twotwo.video.services.bandcamp.extractors.BandcampExtractorHelper.getImageUrl;

import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParserException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.downloader.twotwo.video.MediaFormat;
import com.downloader.twotwo.video.StreamingService;
import com.downloader.twotwo.video.downloader.Downloader;
import com.downloader.twotwo.video.exceptions.ExtractionException;
import com.downloader.twotwo.video.exceptions.ParsingException;
import com.downloader.twotwo.video.linkhandler.LinkHandler;
import com.downloader.twotwo.video.localization.DateWrapper;
import com.downloader.twotwo.video.playlist.PlaylistInfoItemsCollector;
import com.downloader.twotwo.video.stream.AudioStream;
import com.downloader.twotwo.video.stream.Description;
import com.downloader.twotwo.video.stream.StreamExtractor;
import com.downloader.twotwo.video.stream.StreamType;
import com.downloader.twotwo.video.stream.VideoStream;
import com.downloader.twotwo.video.utils.JsonUtils;
import com.downloader.twotwo.video.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BandcampStreamExtractor extends StreamExtractor {

    private JsonObject albumJson;
    private JsonObject current;
    private Document document;

    public BandcampStreamExtractor(final StreamingService service, final LinkHandler linkHandler) {
        super(service, linkHandler);
    }


    @Override
    public void onFetchPage(@Nonnull final Downloader downloader)
            throws IOException, ExtractionException {
        final String html = downloader.get(getLinkHandler().getUrl()).responseBody();
        document = Jsoup.parse(html);
        albumJson = getAlbumInfoJson(html);
        current = albumJson.getObject("current");

        if (albumJson.getArray("trackinfo").size() > 1) {
            // In this case, we are actually viewing an album page!
            throw new ExtractionException("Page is actually an album, not a track");
        }
    }

    /**
     * Get the JSON that contains album's metadata from page
     *
     * @param html Website
     * @return Album metadata JSON
     * @throws ParsingException In case of a faulty website
     */
    public static JsonObject getAlbumInfoJson(final String html) throws ParsingException {
        try {
            return JsonUtils.getJsonData(html, "data-tralbum");
        } catch (final JsonParserException e) {
            throw new ParsingException("Faulty JSON; page likely does not contain album data", e);
        } catch (final ArrayIndexOutOfBoundsException e) {
            throw new ParsingException("JSON does not exist", e);
        }
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        return current.getString("title");
    }

    @Nonnull
    @Override
    public String getUploaderUrl() throws ParsingException {
        final String[] parts = getUrl().split("/");
        // https: (/) (/) * .bandcamp.com (/) and leave out the rest
        return "https://" + parts[2] + "/";
    }

    @Nonnull
    @Override
    public String getUrl() throws ParsingException {
        return albumJson.getString("url").replace("http://", "https://");
    }

    @Nonnull
    @Override
    public String getUploaderName() throws ParsingException {
        return albumJson.getString("artist");
    }

    @Nullable
    @Override
    public String getTextualUploadDate() {
        return current.getString("publish_date");
    }

    @Nullable
    @Override
    public DateWrapper getUploadDate() throws ParsingException {
        return BandcampExtractorHelper.parseDate(getTextualUploadDate());
    }

    @Nonnull
    @Override
    public String getThumbnailUrl() throws ParsingException {
        if (albumJson.isNull("art_id")) {
            return "";
        } else {
            return getImageUrl(albumJson.getLong("art_id"), true);
        }
    }

    @Nonnull
    @Override
    public String getUploaderAvatarUrl() {
        try {
            return document.getElementsByClass("band-photo").first().attr("src");
        } catch (final NullPointerException e) {
            return "";
        }
    }

    @Nonnull
    @Override
    public Description getDescription() {
        final String s = Utils.nonEmptyAndNullJoin(
                "\n\n",
                new String[]{
                        current.getString("about"),
                        current.getString("lyrics"),
                        current.getString("credits")
                }
        );
        return new Description(s, Description.PLAIN_TEXT);
    }

    @Override
    public List<AudioStream> getAudioStreams() {
        final List<AudioStream> audioStreams = new ArrayList<>();

        audioStreams.add(new AudioStream(
                albumJson.getArray("trackinfo").getObject(0)
                        .getObject("file").getString("mp3-128"),
                MediaFormat.MP3, 128
        ));
        return audioStreams;
    }

    @Override
    public List<VideoStream> getVideoStreams() {
        return Collections.emptyList();
    }

    @Override
    public List<VideoStream> getVideoOnlyStreams() {
        return Collections.emptyList();
    }

    @Override
    public StreamType getStreamType() {
        return StreamType.AUDIO_STREAM;
    }

    @Override
    public PlaylistInfoItemsCollector getRelatedItems() {
        final PlaylistInfoItemsCollector collector = new PlaylistInfoItemsCollector(getServiceId());
        final Elements recommendedAlbums = document.getElementsByClass("recommended-album");

        for (final Element album : recommendedAlbums) {
            collector.commit(new BandcampRelatedPlaylistInfoItemExtractor(album));
        }
        return collector;
    }

    @Nonnull
    @Override
    public String getCategory() {
        // Get first tag from html, which is the artist's Genre
        return document.getElementsByClass("tralbum-tags").stream()
                .flatMap(element -> element.getElementsByClass("tag").stream())
                .map(Element::text)
                .findFirst()
                .orElse("");
    }

    @Nonnull
    @Override
    public String getLicence() {
        /* Tests resulted in this mapping of ints to licence:
        https://cloud.disroot.org/s/ZTWBxbQ9fKRmRWJ/preview (screenshot from a Bandcamp artist's
        account) */

        switch (current.getInt("license_type")) {
            case 1:
                return "All rights reserved ©";
            case 2:
                return "CC BY-NC-ND 3.0";
            case 3:
                return "CC BY-NC-SA 3.0";
            case 4:
                return "CC BY-NC 3.0";
            case 5:
                return "CC BY-ND 3.0";
            case 6:
                return "CC BY 3.0";
            case 8:
                return "CC BY-SA 3.0";
            default:
                return "Unknown";
        }
    }

    @Nonnull
    @Override
    public List<String> getTags() {
        final Elements tagElements = document.getElementsByAttributeValue("itemprop", "keywords");

        final List<String> tags = new ArrayList<>();

        for (final Element e : tagElements) {
            tags.add(e.text());
        }

        return tags;
    }
}
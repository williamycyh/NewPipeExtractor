package com.downloader.twotwo.video.services.soundcloud.extractors;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import com.downloader.twotwo.video.ListExtractor;
import com.downloader.twotwo.video.NewPipe;
import com.downloader.twotwo.video.Page;
import com.downloader.twotwo.video.StreamingService;
import com.downloader.twotwo.video.downloader.Downloader;
import com.downloader.twotwo.video.exceptions.ExtractionException;
import com.downloader.twotwo.video.exceptions.ParsingException;
import com.downloader.twotwo.video.linkhandler.ListLinkHandler;
import com.downloader.twotwo.video.playlist.PlaylistExtractor;
import com.downloader.twotwo.video.services.soundcloud.SoundcloudParsingHelper;
import com.downloader.twotwo.video.stream.StreamInfoItem;
import com.downloader.twotwo.video.stream.StreamInfoItemsCollector;
import com.downloader.twotwo.video.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import static com.downloader.twotwo.video.utils.Utils.isNullOrEmpty;

public class SoundcloudPlaylistExtractor extends PlaylistExtractor {
    private static final int STREAMS_PER_REQUESTED_PAGE = 15;

    private String playlistId;
    private JsonObject playlist;

    public SoundcloudPlaylistExtractor(final StreamingService service,
                                       final ListLinkHandler linkHandler) {
        super(service, linkHandler);
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader) throws IOException,
            ExtractionException {

        playlistId = getLinkHandler().getId();
        final String apiUrl = SoundcloudParsingHelper.SOUNDCLOUD_API_V2_URL + "playlists/" + playlistId + "?client_id="
                + SoundcloudParsingHelper.clientId() + "&representation=compact";

        final String response = downloader.get(apiUrl, getExtractorLocalization()).responseBody();
        try {
            playlist = JsonParser.object().from(response);
        } catch (final JsonParserException e) {
            throw new ParsingException("Could not parse json response", e);
        }
    }

    @Nonnull
    @Override
    public String getId() {
        return playlistId;
    }

    @Nonnull
    @Override
    public String getName() {
        return playlist.getString("title");
    }

    @Nonnull
    @Override
    public String getThumbnailUrl() {
        String artworkUrl = playlist.getString("artwork_url");

        if (artworkUrl == null) {
            // If the thumbnail is null, traverse the items list and get a valid one,
            // if it also fails, return null
            try {
                final ListExtractor.InfoItemsPage<StreamInfoItem> infoItems = getInitialPage();

                for (final StreamInfoItem item : infoItems.getItems()) {
                    artworkUrl = item.getThumbnailUrl();
                    if (!Utils.isNullOrEmpty(artworkUrl)) {
                        break;
                    }
                }
            } catch (final Exception ignored) {
            }

            if (artworkUrl == null) {
                return Utils.EMPTY_STRING;
            }
        }

        return artworkUrl.replace("large.jpg", "crop.jpg");
    }

    @Override
    public String getUploaderUrl() {
        return SoundcloudParsingHelper.getUploaderUrl(playlist);
    }

    @Override
    public String getUploaderName() {
        return SoundcloudParsingHelper.getUploaderName(playlist);
    }

    @Override
    public String getUploaderAvatarUrl() {
        return SoundcloudParsingHelper.getAvatarUrl(playlist);
    }

    @Override
    public boolean isUploaderVerified() throws ParsingException {
        return playlist.getObject("user").getBoolean("verified");
    }

    @Override
    public long getStreamCount() {
        return playlist.getLong("track_count");
    }

    @Nonnull
    @Override
    public ListExtractor.InfoItemsPage<StreamInfoItem> getInitialPage() {
        final StreamInfoItemsCollector streamInfoItemsCollector =
                new StreamInfoItemsCollector(getServiceId());
        final List<String> ids = new ArrayList<>();

        playlist.getArray("tracks")
                .stream()
                .filter(JsonObject.class::isInstance)
                .map(JsonObject.class::cast)
                .forEachOrdered(track -> {
                    // i.e. if full info is available
                    if (track.has("title")) {
                        streamInfoItemsCollector.commit(
                                new SoundcloudStreamInfoItemExtractor(track));
                    } else {
                        // %09d would be enough, but a 0 before the number does not create
                        // problems, so let's be sure
                        ids.add(String.format("%010d", track.getInt("id")));
                    }
                });

        return new ListExtractor.InfoItemsPage<>(streamInfoItemsCollector, new Page(ids));
    }

    @Override
    public ListExtractor.InfoItemsPage<StreamInfoItem> getPage(final Page page) throws IOException,
            ExtractionException {
        if (page == null || Utils.isNullOrEmpty(page.getIds())) {
            throw new IllegalArgumentException("Page doesn't contain IDs");
        }

        final List<String> currentIds;
        final List<String> nextIds;
        if (page.getIds().size() <= STREAMS_PER_REQUESTED_PAGE) {
            // Fetch every remaining stream, there are less than the max
            currentIds = page.getIds();
            nextIds = null;
        } else {
            currentIds = page.getIds().subList(0, STREAMS_PER_REQUESTED_PAGE);
            nextIds = page.getIds().subList(STREAMS_PER_REQUESTED_PAGE, page.getIds().size());
        }

        final String currentPageUrl = SoundcloudParsingHelper.SOUNDCLOUD_API_V2_URL + "tracks?client_id="
                + SoundcloudParsingHelper.clientId() + "&ids=" + Utils.join(",", currentIds);

        final StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
        final String response = NewPipe.getDownloader().get(currentPageUrl,
                getExtractorLocalization()).responseBody();

        try {
            final JsonArray tracks = JsonParser.array().from(response);
            for (final Object track : tracks) {
                if (track instanceof JsonObject) {
                    collector.commit(new SoundcloudStreamInfoItemExtractor((JsonObject) track));
                }
            }
        } catch (final JsonParserException e) {
            throw new ParsingException("Could not parse json response", e);
        }

        return new ListExtractor.InfoItemsPage<>(collector, new Page(nextIds));
    }
}
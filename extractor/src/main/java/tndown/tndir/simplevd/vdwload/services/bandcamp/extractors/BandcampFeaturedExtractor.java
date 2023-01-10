// Created by Fynn Godau 2019, licensed GNU GPL version 3 or later

package tndown.tndir.simplevd.vdwload.services.bandcamp.extractors;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import tndown.tndir.simplevd.vdwload.Page;
import tndown.tndir.simplevd.vdwload.StreamingService;
import tndown.tndir.simplevd.vdwload.downloader.Downloader;
import tndown.tndir.simplevd.vdwload.exceptions.ExtractionException;
import tndown.tndir.simplevd.vdwload.exceptions.ParsingException;
import tndown.tndir.simplevd.vdwload.kis.KioskExtractor;
import tndown.tndir.simplevd.vdwload.linkhandler.ListLinkHandler;
import tndown.tndir.simplevd.vdwload.playlist.PlaylistInfoItem;
import tndown.tndir.simplevd.vdwload.playlist.PlaylistInfoItemsCollector;

import javax.annotation.Nonnull;
import java.io.IOException;

public class BandcampFeaturedExtractor extends KioskExtractor<PlaylistInfoItem> {

    public static final String KIOSK_FEATURED = "Featured";
    public static final String FEATURED_API_URL = BandcampExtractorHelper.BASE_API_URL + "/mobile/24/bootstrap_data";
    public static final String MORE_FEATURED_API_URL
            = BandcampExtractorHelper.BASE_API_URL + "/mobile/24/feed_older_logged_out";

    private JsonObject json;

    public BandcampFeaturedExtractor(final StreamingService streamingService,
                                     final ListLinkHandler listLinkHandler,
                                     final String kioskId) {
        super(streamingService, listLinkHandler, kioskId);
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader)
            throws IOException, ExtractionException {
        try {
            json = JsonParser.object().from(
                    getDownloader().post(
                            FEATURED_API_URL, null, "{\"platform\":\"\",\"version\":0}".getBytes()
                    ).responseBody()
            );
        } catch (final JsonParserException e) {
            throw new ParsingException("Could not parse Bandcamp featured API response", e);
        }
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        return KIOSK_FEATURED;
    }

    @Nonnull
    @Override
    public InfoItemsPage<PlaylistInfoItem> getInitialPage()
            throws IOException, ExtractionException {
        final JsonArray featuredStories = json.getObject("feed_content")
                .getObject("stories")
                .getArray("featured");

        return extractItems(featuredStories);
    }

    private InfoItemsPage<PlaylistInfoItem> extractItems(final JsonArray featuredStories) {
        final PlaylistInfoItemsCollector c = new PlaylistInfoItemsCollector(getServiceId());

        for (int i = 0; i < featuredStories.size(); i++) {
            final JsonObject featuredStory = featuredStories.getObject(i);

            if (featuredStory.isNull("album_title")) {
                // Is not an album, ignore
                continue;
            }

            c.commit(new BandcampPlaylistInfoItemFeaturedExtractor(featuredStory));
        }

        final JsonObject lastFeaturedStory = featuredStories.getObject(featuredStories.size() - 1);
        return new InfoItemsPage<>(c, getNextPageFrom(lastFeaturedStory));
    }

    /**
     * Next Page can be generated from metadata of last featured story
     */
    private Page getNextPageFrom(final JsonObject lastFeaturedStory) {
        final long lastStoryDate = lastFeaturedStory.getLong("story_date");
        final long lastStoryId = lastFeaturedStory.getLong("ntid");
        final String lastStoryType = lastFeaturedStory.getString("story_type");
        return new Page(
                MORE_FEATURED_API_URL + "?story_groups=featured"
                        + ':' + lastStoryDate + ':' + lastStoryType + ':' + lastStoryId
        );
    }

    @Override
    public InfoItemsPage<PlaylistInfoItem> getPage(final Page page)
            throws IOException, ExtractionException {

        final JsonObject response;
        try {
            response = JsonParser.object().from(
                    getDownloader().get(page.getUrl()).responseBody()
            );
        } catch (final JsonParserException e) {
            throw new ParsingException("Could not parse Bandcamp featured API response", e);
        }

        return extractItems(response.getObject("stories").getArray("featured"));
    }
}

package tndown.tndir.simplevd.vdwload.services.bandcamp.extractors;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParserException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import tndown.tndir.simplevd.vdwload.Page;
import tndown.tndir.simplevd.vdwload.StreamingService;
import tndown.tndir.simplevd.vdwload.downloader.Downloader;
import tndown.tndir.simplevd.vdwload.exceptions.ContentNotAvailableException;
import tndown.tndir.simplevd.vdwload.exceptions.ExtractionException;
import tndown.tndir.simplevd.vdwload.exceptions.ParsingException;
import tndown.tndir.simplevd.vdwload.linkhandler.ListLinkHandler;
import tndown.tndir.simplevd.vdwload.playlist.PlaylistExtractor;
import tndown.tndir.simplevd.vdwload.services.bandcamp.extractors.streaminfoitem.BandcampPlaylistStreamInfoItemExtractor;
import tndown.tndir.simplevd.vdwload.stream.StreamInfoItem;
import tndown.tndir.simplevd.vdwload.stream.StreamInfoItemsCollector;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Objects;

import static tndown.tndir.simplevd.vdwload.services.bandcamp.extractors.BandcampExtractorHelper.getImageUrl;
import static tndown.tndir.simplevd.vdwload.utils.JsonUtils.getJsonData;
import static tndown.tndir.simplevd.vdwload.Utils.EMPTY_STRING;
import static tndown.tndir.simplevd.vdwload.Utils.HTTPS;

public class BandcampPlaylistExtractor extends PlaylistExtractor {

    /**
     * An arbitrarily chosen number above which cover arts won't be fetched individually for each
     * track; instead, it will be assumed that every track has the same cover art as the album,
     * which is not always the case.
     */
    private static final int MAXIMUM_INDIVIDUAL_COVER_ARTS = 10;

    private Document document;
    private JsonObject albumJson;
    private JsonArray trackInfo;
    private String name;

    public BandcampPlaylistExtractor(final StreamingService service,
                                     final ListLinkHandler linkHandler) {
        super(service, linkHandler);
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader)
            throws IOException, ExtractionException {
        final String html = downloader.get(getLinkHandler().getUrl()).responseBody();
        document = Jsoup.parse(html);
        albumJson = BandcampStreamExtractor.getAlbumInfoJson(html);
        trackInfo = albumJson.getArray("trackinfo");

        try {
            name = getJsonData(html, "data-embed").getString("album_title");
        } catch (final JsonParserException e) {
            throw new ParsingException("Faulty JSON; page likely does not contain album data", e);
        } catch (final ArrayIndexOutOfBoundsException e) {
            throw new ParsingException("JSON does not exist", e);
        }

        if (trackInfo.isEmpty()) {
            // Albums without trackInfo need to be purchased before they can be played
            throw new ContentNotAvailableException("Album needs to be purchased");
        }
    }

    @Nonnull
    @Override
    public String getThumbnailUrl() throws ParsingException {
        if (albumJson.isNull("art_id")) {
            return EMPTY_STRING;
        } else {
            return getImageUrl(albumJson.getLong("art_id"), true);
        }
    }

    @Override
    public String getUploaderUrl() throws ParsingException {
        final String[] parts = getUrl().split("/");
        // https: (/) (/) * .bandcamp.com (/) and leave out the rest
        return HTTPS + parts[2] + "/";
    }

    @Override
    public String getUploaderName() {
        return albumJson.getString("artist");
    }

    @Override
    public String getUploaderAvatarUrl() {
        try {
            return Objects.requireNonNull(document.getElementsByClass("band-photo").first())
                    .attr("src");
        } catch (final NullPointerException e) {
            return EMPTY_STRING;
        }
    }

    @Override
    public boolean isUploaderVerified() throws ParsingException {
        return false;
    }

    @Override
    public long getStreamCount() {
        return trackInfo.size();
    }

    @Nonnull
    @Override
    public InfoItemsPage<StreamInfoItem> getInitialPage() throws ExtractionException {

        final StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());

        for (int i = 0; i < trackInfo.size(); i++) {
            final JsonObject track = trackInfo.getObject(i);

            if (trackInfo.size() < MAXIMUM_INDIVIDUAL_COVER_ARTS) {
                // Load cover art of every track individually
                collector.commit(new BandcampPlaylistStreamInfoItemExtractor(
                        track, getUploaderUrl(), getService()));
            } else {
                // Pretend every track has the same cover art as the album
                collector.commit(new BandcampPlaylistStreamInfoItemExtractor(
                        track, getUploaderUrl(), getThumbnailUrl()));
            }
        }

        return new InfoItemsPage<>(collector, null);
    }

    @Override
    public InfoItemsPage<StreamInfoItem> getPage(final Page page) {
        return null;
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        return name;
    }
}

package tevd.nbapp.vide.downl.services.bandcamp.extractors;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParserException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import tevd.nbapp.vide.downl.Page;
import tevd.nbapp.vide.downl.StreamingService;
import tevd.nbapp.vide.downl.downloader.Downloader;
import tevd.nbapp.vide.downl.exceptions.ContentNotAvailableException;
import tevd.nbapp.vide.downl.exceptions.ExtractionException;
import tevd.nbapp.vide.downl.exceptions.ParsingException;
import tevd.nbapp.vide.downl.linkhandler.ListLinkHandler;
import tevd.nbapp.vide.downl.playlist.PlaylistExtractor;
import tevd.nbapp.vide.downl.services.bandcamp.extractors.streaminfoitem.BandcampPlaylistStreamInfoItemExtractor;
import tevd.nbapp.vide.downl.stream.StreamInfoItem;
import tevd.nbapp.vide.downl.stream.StreamInfoItemsCollector;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Objects;

import static tevd.nbapp.vide.downl.services.bandcamp.extractors.BandcampExtractorHelper.getImageUrl;
import static tevd.nbapp.vide.downl.utils.JsonUtils.getJsonData;
import static tevd.nbapp.vide.downl.utils.Utils.EMPTY_STRING;
import static tevd.nbapp.vide.downl.utils.Utils.HTTPS;

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

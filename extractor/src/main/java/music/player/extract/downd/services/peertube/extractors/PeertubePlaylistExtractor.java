package music.player.extract.downd.services.peertube.extractors;

import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import music.player.extract.downd.ListExtractor;
import music.player.extract.downd.Page;
import music.player.extract.downd.StreamingService;
import music.player.extract.downd.downloader.Downloader;
import music.player.extract.downd.downloader.Response;
import music.player.extract.downd.exceptions.ExtractionException;
import music.player.extract.downd.exceptions.ParsingException;
import music.player.extract.downd.linkhandler.ListLinkHandler;
import music.player.extract.downd.playlist.PlaylistExtractor;
import music.player.extract.downd.services.peertube.PeertubeParsingHelper;
import music.player.extract.downd.stream.StreamInfoItem;
import music.player.extract.downd.stream.StreamInfoItemsCollector;
import music.player.extract.downd.utils.Utils;

import javax.annotation.Nonnull;
import java.io.IOException;

import static music.player.extract.downd.services.peertube.PeertubeParsingHelper.collectStreamsFrom;
import static music.player.extract.downd.utils.Utils.isNullOrEmpty;

public class PeertubePlaylistExtractor extends PlaylistExtractor {
    private JsonObject playlistInfo;

    public PeertubePlaylistExtractor(final StreamingService service,
                                     final ListLinkHandler linkHandler) {
        super(service, linkHandler);
    }

    @Nonnull
    @Override
    public String getThumbnailUrl() throws ParsingException {
        return getBaseUrl() + playlistInfo.getString("thumbnailPath");
    }

    @Override
    public String getUploaderUrl() {
        return playlistInfo.getObject("ownerAccount").getString("url");
    }

    @Override
    public String getUploaderName() {
        return playlistInfo.getObject("ownerAccount").getString("displayName");
    }

    @Override
    public String getUploaderAvatarUrl() throws ParsingException {
        return getBaseUrl()
                + playlistInfo.getObject("ownerAccount").getObject("avatar").getString("path");
    }

    @Override
    public boolean isUploaderVerified() throws ParsingException {
        return false;
    }

    @Override
    public long getStreamCount() {
        return playlistInfo.getLong("videosLength");
    }

    @Nonnull
    @Override
    public String getSubChannelName() {
        return playlistInfo.getObject("videoChannel").getString("displayName");
    }

    @Nonnull
    @Override
    public String getSubChannelUrl() {
        return playlistInfo.getObject("videoChannel").getString("url");
    }

    @Nonnull
    @Override
    public String getSubChannelAvatarUrl() throws ParsingException {
        return getBaseUrl()
                + playlistInfo.getObject("videoChannel").getObject("avatar").getString("path");
    }

    @Nonnull
    @Override
    public ListExtractor.InfoItemsPage<StreamInfoItem> getInitialPage() throws IOException, ExtractionException {
        return getPage(new Page(getUrl() + "/videos?" + PeertubeParsingHelper.START_KEY + "=0&"
                + PeertubeParsingHelper.COUNT_KEY + "=" + PeertubeParsingHelper.ITEMS_PER_PAGE));
    }

    @Override
    public ListExtractor.InfoItemsPage<StreamInfoItem> getPage(final Page page)
            throws IOException, ExtractionException {
        if (page == null || Utils.isNullOrEmpty(page.getUrl())) {
            throw new IllegalArgumentException("Page doesn't contain an URL");
        }

        final Response response = getDownloader().get(page.getUrl());

        JsonObject json = null;
        if (response != null && !Utils.isBlank(response.responseBody())) {
            try {
                json = JsonParser.object().from(response.responseBody());
            } catch (final Exception e) {
                throw new ParsingException("Could not parse json data for playlist info", e);
            }
        }

        if (json != null) {
            PeertubeParsingHelper.validate(json);
            final long total = json.getLong("total");

            final StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
            PeertubeParsingHelper.collectStreamsFrom(collector, json, getBaseUrl());

            return new ListExtractor.InfoItemsPage<>(collector,
                    PeertubeParsingHelper.getNextPage(page.getUrl(), total));
        } else {
            throw new ExtractionException("Unable to get PeerTube playlist info");
        }
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader)
            throws IOException, ExtractionException {
        final Response response = downloader.get(getUrl());
        try {
            playlistInfo = JsonParser.object().from(response.responseBody());
        } catch (final JsonParserException jpe) {
            throw new ExtractionException("Could not parse json", jpe);
        }
        PeertubeParsingHelper.validate(playlistInfo);
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        return playlistInfo.getString("displayName");
    }
}

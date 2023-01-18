package vthirtylib.second.third.downdir.services.peertube.extractors;

import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import vthirtylib.second.third.downdir.ListExtractor;
import vthirtylib.second.third.downdir.Page;
import vthirtylib.second.third.downdir.StreamingService;
import vthirtylib.second.third.downdir.channel.ChannelExtractor;
import vthirtylib.second.third.downdir.downloader.Downloader;
import vthirtylib.second.third.downdir.downloader.Response;
import vthirtylib.second.third.downdir.exceptions.ExtractionException;
import vthirtylib.second.third.downdir.exceptions.ParsingException;
import vthirtylib.second.third.downdir.linkhandler.ListLinkHandler;
import vthirtylib.second.third.downdir.services.peertube.PeertubeParsingHelper;
import vthirtylib.second.third.downdir.services.peertube.linkHandler.PeertubeChannelLinkHandlerFactory;
import vthirtylib.second.third.downdir.stream.StreamInfoItem;
import vthirtylib.second.third.downdir.stream.StreamInfoItemsCollector;
import vthirtylib.second.third.downdir.utils.JsonUtils;
import vthirtylib.second.third.downdir.Utils;

import javax.annotation.Nonnull;
import java.io.IOException;

import static vthirtylib.second.third.downdir.services.peertube.PeertubeParsingHelper.collectStreamsFrom;
import static vthirtylib.second.third.downdir.Utils.isNullOrEmpty;

public class PeertubeChannelExtractor extends ChannelExtractor {
    private JsonObject json;
    private final String baseUrl;

    public PeertubeChannelExtractor(final StreamingService service,
                                    final ListLinkHandler linkHandler) throws ParsingException {
        super(service, linkHandler);
        this.baseUrl = getBaseUrl();
    }

    @Override
    public String getAvatarUrl() {
        String value;
        try {
            value = JsonUtils.getString(json, "avatar.path");
        } catch (final Exception e) {
            value = "/client/assets/images/default-avatar.png";
        }
        return baseUrl + value;
    }

    @Override
    public String getBannerUrl() {
        return null;
    }

    @Override
    public String getFeedUrl() throws ParsingException {
        return getBaseUrl() + "/feeds/videos.xml?videoChannelId=" + json.get("id");
    }

    @Override
    public long getSubscriberCount() {
        return json.getLong("followersCount");
    }

    @Override
    public String getDescription() {
        try {
            return JsonUtils.getString(json, "description");
        } catch (final ParsingException e) {
            return "No description";
        }
    }

    @Override
    public String getParentChannelName() throws ParsingException {
        return JsonUtils.getString(json, "ownerAccount.name");
    }

    @Override
    public String getParentChannelUrl() throws ParsingException {
        return JsonUtils.getString(json, "ownerAccount.url");
    }

    @Override
    public String getParentChannelAvatarUrl() {
        String value;
        try {
            value = JsonUtils.getString(json, "ownerAccount.avatar.path");
        } catch (final Exception e) {
            value = "/client/assets/images/default-avatar.png";
        }
        return baseUrl + value;
    }

    @Override
    public boolean isVerified() throws ParsingException {
        return false;
    }

    @Nonnull
    @Override
    public ListExtractor.InfoItemsPage<StreamInfoItem> getInitialPage() throws IOException, ExtractionException {
        return getPage(new Page(baseUrl + "/api/v1/" + getId() + "/videos?" + PeertubeParsingHelper.START_KEY + "=0&"
                + PeertubeParsingHelper.COUNT_KEY + "=" + PeertubeParsingHelper.ITEMS_PER_PAGE));
    }

    @Override
    public ListExtractor.InfoItemsPage<StreamInfoItem> getPage(final Page page)
            throws IOException, ExtractionException {
        if (page == null || Utils.isNullOrEmpty(page.getUrl())) {
            throw new IllegalArgumentException("Page doesn't contain an URL");
        }

        final Response response = getDownloader().get(page.getUrl());

        JsonObject pageJson = null;
        if (response != null && !Utils.isBlank(response.responseBody())) {
            try {
                pageJson = JsonParser.object().from(response.responseBody());
            } catch (final Exception e) {
                throw new ParsingException("Could not parse json data for channel info", e);
            }
        }

        if (pageJson != null) {
            PeertubeParsingHelper.validate(pageJson);
            final long total = pageJson.getLong("total");

            final StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
            PeertubeParsingHelper.collectStreamsFrom(collector, pageJson, getBaseUrl());

            return new ListExtractor.InfoItemsPage<>(collector,
                    PeertubeParsingHelper.getNextPage(page.getUrl(), total));
        } else {
            throw new ExtractionException("Unable to get PeerTube channel info");
        }
    }

    @Override
    public void onFetchPage(final Downloader downloader)
            throws IOException, ExtractionException {
        final Response response = downloader.get(
                baseUrl + PeertubeChannelLinkHandlerFactory.API_ENDPOINT + getId());
        if (response != null) {
            setInitialData(response.responseBody());
        } else {
            throw new ExtractionException("Unable to extract PeerTube channel data");
        }
    }

    private void setInitialData(final String responseBody) throws ExtractionException {
        try {
            json = JsonParser.object().from(responseBody);
        } catch (final JsonParserException e) {
            throw new ExtractionException("Unable to extract PeerTube channel data", e);
        }
        if (json == null) {
            throw new ExtractionException("Unable to extract PeerTube channel data");
        }
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        return JsonUtils.getString(json, "displayName");
    }
}

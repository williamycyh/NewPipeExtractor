package vmeno.yyml.nnbersi.downd.services.soundcloud.extractors;

import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import vmeno.yyml.nnbersi.downd.ListExtractor;
import vmeno.yyml.nnbersi.downd.Page;
import vmeno.yyml.nnbersi.downd.StreamingService;
import vmeno.yyml.nnbersi.downd.channel.ChannelExtractor;
import vmeno.yyml.nnbersi.downd.downloader.Downloader;
import vmeno.yyml.nnbersi.downd.linkhandler.ListLinkHandler;
import vmeno.yyml.nnbersi.downd.stream.StreamInfoItem;
import vmeno.yyml.nnbersi.downd.stream.StreamInfoItemsCollector;
import vmeno.yyml.nnbersi.downd.utils.Utils;
import vmeno.yyml.nnbersi.downd.exceptions.ExtractionException;
import vmeno.yyml.nnbersi.downd.exceptions.ParsingException;
import vmeno.yyml.nnbersi.downd.services.soundcloud.SoundcloudParsingHelper;

import javax.annotation.Nonnull;
import java.io.IOException;

import static vmeno.yyml.nnbersi.downd.services.soundcloud.SoundcloudParsingHelper.SOUNDCLOUD_API_V2_URL;
import static vmeno.yyml.nnbersi.downd.utils.Utils.isNullOrEmpty;

public class SoundcloudChannelExtractor extends ChannelExtractor {
    private String userId;
    private JsonObject user;
    private static final String USERS_ENDPOINT = SOUNDCLOUD_API_V2_URL + "users/";

    public SoundcloudChannelExtractor(final StreamingService service,
                                      final ListLinkHandler linkHandler) {
        super(service, linkHandler);
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader) throws IOException,
            ExtractionException {

        userId = getLinkHandler().getId();
        final String apiUrl = USERS_ENDPOINT + userId + "?client_id="
                + SoundcloudParsingHelper.clientId();

        final String response = downloader.get(apiUrl, getExtractorLocalization()).responseBody();
        try {
            user = JsonParser.object().from(response);
        } catch (final JsonParserException e) {
            throw new ParsingException("Could not parse json response", e);
        }
    }

    @Nonnull
    @Override
    public String getId() {
        return userId;
    }

    @Nonnull
    @Override
    public String getName() {
        return user.getString("username");
    }

    @Override
    public String getAvatarUrl() {
        return user.getString("avatar_url");
    }

    @Override
    public String getBannerUrl() {
        return user.getObject("visuals").getArray("visuals").getObject(0)
                .getString("visual_url");
    }

    @Override
    public String getFeedUrl() {
        return null;
    }

    @Override
    public long getSubscriberCount() {
        return user.getLong("followers_count", 0);
    }

    @Override
    public String getDescription() {
        return user.getString("description", Utils.EMPTY_STRING);
    }

    @Override
    public String getParentChannelName() {
        return "";
    }

    @Override
    public String getParentChannelUrl() {
        return "";
    }

    @Override
    public String getParentChannelAvatarUrl() {
        return "";
    }

    @Override
    public boolean isVerified() throws ParsingException {
        return user.getBoolean("verified");
    }

    @Nonnull
    @Override
    public ListExtractor.InfoItemsPage<StreamInfoItem> getInitialPage() throws ExtractionException {
        try {
            final StreamInfoItemsCollector streamInfoItemsCollector =
                    new StreamInfoItemsCollector(getServiceId());

            final String apiUrl = USERS_ENDPOINT + getId() + "/tracks" + "?client_id="
                    + SoundcloudParsingHelper.clientId() + "&limit=20" + "&linked_partitioning=1";

            final String nextPageUrl = SoundcloudParsingHelper.getStreamsFromApiMinItems(15,
                    streamInfoItemsCollector, apiUrl);

            return new ListExtractor.InfoItemsPage<>(streamInfoItemsCollector, new Page(nextPageUrl));
        } catch (final Exception e) {
            throw new ExtractionException("Could not get next page", e);
        }
    }

    @Override
    public ListExtractor.InfoItemsPage<StreamInfoItem> getPage(final Page page) throws IOException,
            ExtractionException {
        if (page == null || Utils.isNullOrEmpty(page.getUrl())) {
            throw new IllegalArgumentException("Page doesn't contain an URL");
        }

        final StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
        final String nextPageUrl = SoundcloudParsingHelper.getStreamsFromApiMinItems(15, collector,
                page.getUrl());

        return new ListExtractor.InfoItemsPage<>(collector, new Page(nextPageUrl));
    }
}

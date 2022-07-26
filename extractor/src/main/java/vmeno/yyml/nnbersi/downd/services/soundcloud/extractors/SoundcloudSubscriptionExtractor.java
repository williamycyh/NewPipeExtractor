package vmeno.yyml.nnbersi.downd.services.soundcloud.extractors;

import vmeno.yyml.nnbersi.downd.channel.ChannelInfoItem;
import vmeno.yyml.nnbersi.downd.channel.ChannelInfoItemsCollector;
import vmeno.yyml.nnbersi.downd.exceptions.ExtractionException;
import vmeno.yyml.nnbersi.downd.services.soundcloud.SoundcloudParsingHelper;
import vmeno.yyml.nnbersi.downd.services.soundcloud.SoundcloudService;
import vmeno.yyml.nnbersi.downd.subscription.SubscriptionExtractor;
import vmeno.yyml.nnbersi.downd.subscription.SubscriptionItem;
import vmeno.yyml.nnbersi.downd.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Extract the "followings" from a user in SoundCloud.
 */
public class SoundcloudSubscriptionExtractor extends SubscriptionExtractor {

    public SoundcloudSubscriptionExtractor(final SoundcloudService service) {
        super(service, Collections.singletonList(ContentSource.CHANNEL_URL));
    }

    @Override
    public String getRelatedUrl() {
        return "https://soundcloud.com/you";
    }

    @Override
    public List<SubscriptionItem> fromChannelUrl(final String channelUrl) throws IOException,
            ExtractionException {
        if (channelUrl == null) {
            throw new InvalidSourceException("Channel url is null");
        }

        final String id;
        try {
            id = service.getChannelLHFactory().fromUrl(getUrlFrom(channelUrl)).getId();
        } catch (final ExtractionException e) {
            throw new InvalidSourceException(e);
        }

        final String apiUrl = SoundcloudParsingHelper.SOUNDCLOUD_API_V2_URL + "users/" + id + "/followings" + "?client_id="
                + SoundcloudParsingHelper.clientId() + "&limit=200";
        final ChannelInfoItemsCollector collector = new ChannelInfoItemsCollector(service
                .getServiceId());
        // ± 2000 is the limit of followings on SoundCloud, so this minimum should be enough
        SoundcloudParsingHelper.getUsersFromApiMinItems(2500, collector, apiUrl);

        return toSubscriptionItems(collector.getItems());
    }

    private String getUrlFrom(final String channelUrl) {
        final String fixedUrl = Utils.replaceHttpWithHttps(channelUrl);
        if (fixedUrl.startsWith(Utils.HTTPS)) {
            return channelUrl;
        } else if (!fixedUrl.contains("soundcloud.com/")) {
            return "https://soundcloud.com/" + fixedUrl;
        } else {
            return Utils.HTTPS + fixedUrl;
        }
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Utils
    //////////////////////////////////////////////////////////////////////////*/

    private List<SubscriptionItem> toSubscriptionItems(final List<ChannelInfoItem> items) {
        final List<SubscriptionItem> result = new ArrayList<>(items.size());
        for (final ChannelInfoItem item : items) {
            result.add(new SubscriptionItem(item.getServiceId(), item.getUrl(), item.getName()));
        }
        return result;
    }
}

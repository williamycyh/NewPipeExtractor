package com.downloader.twotwo.video.services.soundcloud.extractors;

import com.downloader.twotwo.video.channel.ChannelInfoItem;
import com.downloader.twotwo.video.channel.ChannelInfoItemsCollector;
import com.downloader.twotwo.video.exceptions.ExtractionException;
import com.downloader.twotwo.video.services.soundcloud.SoundcloudParsingHelper;
import com.downloader.twotwo.video.services.soundcloud.SoundcloudService;
import com.downloader.twotwo.video.subscription.SubscriptionExtractor;
import com.downloader.twotwo.video.subscription.SubscriptionItem;
import com.downloader.twotwo.video.utils.Utils;

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
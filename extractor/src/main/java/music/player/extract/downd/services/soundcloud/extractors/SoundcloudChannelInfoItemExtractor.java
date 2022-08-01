package music.player.extract.downd.services.soundcloud.extractors;

import com.grack.nanojson.JsonObject;
import music.player.extract.downd.channel.ChannelInfoItemExtractor;
import music.player.extract.downd.utils.Utils;

public class SoundcloudChannelInfoItemExtractor implements ChannelInfoItemExtractor {
    private final JsonObject itemObject;

    public SoundcloudChannelInfoItemExtractor(final JsonObject itemObject) {
        this.itemObject = itemObject;
    }

    @Override
    public String getName() {
        return itemObject.getString("username");
    }

    @Override
    public String getUrl() {
        return Utils.replaceHttpWithHttps(itemObject.getString("permalink_url"));
    }

    @Override
    public String getThumbnailUrl() {
        // An avatar URL with a better resolution
        return itemObject.getString("avatar_url", Utils.EMPTY_STRING).replace("large.jpg", "crop.jpg");
    }

    @Override
    public long getSubscriberCount() {
        return itemObject.getLong("followers_count");
    }

    @Override
    public long getStreamCount() {
        return itemObject.getLong("track_count");
    }

    @Override
    public boolean isVerified() {
        return itemObject.getBoolean("verified");
    }

    @Override
    public String getDescription() {
        return itemObject.getString("description", Utils.EMPTY_STRING);
    }
}

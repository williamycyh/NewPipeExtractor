// Created by Fynn Godau 2019, licensed GNU GPL version 3 or later

package com.downloader.twotwo.video.services.bandcamp.extractors;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.downloader.twotwo.video.ListExtractor;
import com.downloader.twotwo.video.Page;
import com.downloader.twotwo.video.StreamingService;
import com.downloader.twotwo.video.channel.ChannelExtractor;
import com.downloader.twotwo.video.downloader.Downloader;
import com.downloader.twotwo.video.exceptions.ExtractionException;
import com.downloader.twotwo.video.exceptions.ParsingException;
import com.downloader.twotwo.video.exceptions.ReCaptchaException;
import com.downloader.twotwo.video.linkhandler.ListLinkHandler;
import com.downloader.twotwo.video.stream.StreamInfoItem;
import com.downloader.twotwo.video.stream.StreamInfoItemsCollector;

import org.jsoup.Jsoup;

import com.downloader.twotwo.video.services.bandcamp.extractors.streaminfoitem.BandcampDiscographStreamInfoItemExtractor;

import java.io.IOException;

import javax.annotation.Nonnull;

public class BandcampChannelExtractor extends ChannelExtractor {

    private JsonObject channelInfo;

    public BandcampChannelExtractor(final StreamingService service,
                                    final ListLinkHandler linkHandler) {
        super(service, linkHandler);
    }

    @Override
    public String getAvatarUrl() {
        if (channelInfo.getLong("bio_image_id") == 0) {
            return "";
        }

        return BandcampExtractorHelper.getImageUrl(channelInfo.getLong("bio_image_id"), false);
    }

    @Override
    public String getBannerUrl() throws ParsingException {
        /*
         * Mobile API does not return the header or not the correct header.
         * Therefore, we need to query the website
         */
        try {
            final String html = getDownloader()
                            .get(channelInfo.getString("bandcamp_url")
                                    .replace("http://", "https://"))
                            .responseBody();

            return Jsoup.parse(html)
                    .getElementById("customHeader")
                    .getElementsByTag("img")
                    .first()
                    .attr("src");

        } catch (final IOException | ReCaptchaException e) {
            throw new ParsingException("Could not download artist web site", e);
        } catch (final NullPointerException e) {
            // No banner available
            return "";
        }
    }

    /**
     * Bandcamp discontinued their RSS feeds because it hadn't been used enough.
     */
    @Override
    public String getFeedUrl() {
        return null;
    }

    @Override
    public long getSubscriberCount() {
        return -1;
    }

    @Override
    public String getDescription() {
        return channelInfo.getString("bio");
    }

    @Override
    public String getParentChannelName() {
        return null;
    }

    @Override
    public String getParentChannelUrl() {
        return null;
    }

    @Override
    public String getParentChannelAvatarUrl() {
        return null;
    }

    @Override
    public boolean isVerified() throws ParsingException {
        return false;
    }

    @Nonnull
    @Override
    public ListExtractor.InfoItemsPage<StreamInfoItem> getInitialPage() throws ParsingException {

        final StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());

        final JsonArray discography = channelInfo.getArray("discography");

        for (int i = 0; i < discography.size(); i++) {
            // A discograph is as an item appears in a discography
            final JsonObject discograph = discography.getObject(i);

            if (!discograph.getString("item_type").equals("track")) {
                continue;
            }

            collector.commit(new BandcampDiscographStreamInfoItemExtractor(discograph, getUrl()));
        }

        return new ListExtractor.InfoItemsPage<>(collector, null);
    }

    @Override
    public ListExtractor.InfoItemsPage<StreamInfoItem> getPage(final Page page) {
        return null;
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader)
            throws IOException, ExtractionException {
        channelInfo = BandcampExtractorHelper.getArtistDetails(getId());
    }

    @Nonnull
    @Override
    public String getName() {
        return channelInfo.getString("name");
    }
}

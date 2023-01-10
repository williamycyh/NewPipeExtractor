package org.schabi.newpipe.extractor.services.soundcloud;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.schabi.newpipe.downloader.DownloaderTestImpl;
import tndown.tndir.simplevd.vdwload.NewPipe;
import tndown.tndir.simplevd.vdwload.ServiceList;
import tndown.tndir.simplevd.vdwload.linkhandler.LinkHandlerFactory;
import tndown.tndir.simplevd.vdwload.services.soundcloud.extractors.SoundcloudSubscriptionExtractor;
import tndown.tndir.simplevd.vdwload.subscription.SubscriptionExtractor;
import tndown.tndir.simplevd.vdwload.subscription.SubscriptionItem;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link SoundcloudSubscriptionExtractor}
 */
public class SoundcloudSubscriptionExtractorTest {
    private static SoundcloudSubscriptionExtractor subscriptionExtractor;
    private static LinkHandlerFactory urlHandler;

    @BeforeAll
    public static void setupClass() {
        NewPipe.init(DownloaderTestImpl.getInstance());
        subscriptionExtractor = new SoundcloudSubscriptionExtractor(ServiceList.SoundCloud);
        urlHandler = ServiceList.SoundCloud.getChannelLHFactory();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "https://soundcloud.com/monstercat",
            "http://soundcloud.com/monstercat",
            "soundcloud.com/monstercat",
            "monstercat",
            // Empty followings user
            "some-random-user-184047028"
    })
    void testFromChannelUrl(final String channelUrl) throws Exception {
        for (SubscriptionItem item : subscriptionExtractor.fromChannelUrl(channelUrl)) {
            assertNotNull(item.getName());
            assertNotNull(item.getUrl());
            assertTrue(urlHandler.acceptUrl(item.getUrl()));
            assertNotEquals(-1, item.getServiceId());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "httttps://invalid.com/user",
            ".com/monstercat",
            "ithinkthatthisuserdontexist",
            ""
    })
    void testInvalidSourceException(final String invalidUser) {
        assertThrows(
                SubscriptionExtractor.InvalidSourceException.class,
                () -> subscriptionExtractor.fromChannelUrl(invalidUser));
    }

    // null can't be added to the above value source because it's not a constant
    @Test
    void testInvalidSourceExceptionWhenUrlIsNull() {
        assertThrows(
                SubscriptionExtractor.InvalidSourceException.class,
                () -> subscriptionExtractor.fromChannelUrl(null));
    }
}

package org.schabi.newpipe.extractor.services.soundcloud.search;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.schabi.newpipe.downloader.DownloaderTestImpl;
import com.github.video.downloader.InfoItem;
import com.github.video.downloader.ListExtractor.InfoItemsPage;
import com.github.video.downloader.NewPipe;
import com.github.video.downloader.StreamingService;
import com.github.video.downloader.channel.ChannelInfoItem;
import com.github.video.downloader.exceptions.ExtractionException;
import com.github.video.downloader.search.SearchExtractor;
import org.schabi.newpipe.extractor.services.DefaultSearchExtractorTest;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static com.github.video.downloader.ServiceList.SoundCloud;
import static org.schabi.newpipe.extractor.services.DefaultTests.assertNoDuplicatedItems;
import static com.github.video.downloader.utils.Utils.UTF_8;

public class SoundcloudSearchExtractorTest {

    public static class All extends DefaultSearchExtractorTest {
        private static SearchExtractor extractor;
        private static final String QUERY = "lill uzi vert";

        @BeforeAll
        public static void setUp() throws Exception {
            NewPipe.init(DownloaderTestImpl.getInstance());
            extractor = SoundCloud.getSearchExtractor(QUERY);
            extractor.fetchPage();
        }

        // @formatter:off
        @Override public SearchExtractor extractor() { return extractor; }
        @Override public StreamingService expectedService() { return SoundCloud; }
        @Override public String expectedName() { return QUERY; }
        @Override public String expectedId() { return QUERY; }
        @Override public String expectedUrlContains() { return "soundcloud.com/search?q=" + urlEncode(QUERY); }
        @Override public String expectedOriginalUrlContains() { return "soundcloud.com/search?q=" + urlEncode(QUERY); }
        @Override public String expectedSearchString() { return QUERY; }
        @Nullable @Override public String expectedSearchSuggestion() { return null; }
        // @formatter:on
    }

    public static class Tracks extends DefaultSearchExtractorTest {
        private static SearchExtractor extractor;
        private static final String QUERY = "lill uzi vert";

        @BeforeAll
        public static void setUp() throws Exception {
            NewPipe.init(DownloaderTestImpl.getInstance());
            extractor = SoundCloud.getSearchExtractor(QUERY, singletonList(TRACKS), "");
            extractor.fetchPage();
        }

        // @formatter:off
        @Override public SearchExtractor extractor() { return extractor; }
        @Override public StreamingService expectedService() { return SoundCloud; }
        @Override public String expectedName() { return QUERY; }
        @Override public String expectedId() { return QUERY; }
        @Override public String expectedUrlContains() { return "soundcloud.com/search/tracks?q=" + urlEncode(QUERY); }
        @Override public String expectedOriginalUrlContains() { return "soundcloud.com/search/tracks?q=" + urlEncode(QUERY); }
        @Override public String expectedSearchString() { return QUERY; }
        @Nullable @Override public String expectedSearchSuggestion() { return null; }
        @Override public InfoItem.InfoType expectedInfoItemType() { return InfoItem.InfoType.STREAM; }
        // @formatter:on
    }

    public static class Users extends DefaultSearchExtractorTest {
        private static SearchExtractor extractor;
        private static final String QUERY = "lill uzi vert";

        @BeforeAll
        public static void setUp() throws Exception {
            NewPipe.init(DownloaderTestImpl.getInstance());
            extractor = SoundCloud.getSearchExtractor(QUERY, singletonList(USERS), "");
            extractor.fetchPage();
        }

        // @formatter:off
        @Override public SearchExtractor extractor() { return extractor; }
        @Override public StreamingService expectedService() { return SoundCloud; }
        @Override public String expectedName() { return QUERY; }
        @Override public String expectedId() { return QUERY; }
        @Override public String expectedUrlContains() { return "soundcloud.com/search/users?q=" + urlEncode(QUERY); }
        @Override public String expectedOriginalUrlContains() { return "soundcloud.com/search/users?q=" + urlEncode(QUERY); }
        @Override public String expectedSearchString() { return QUERY; }
        @Nullable @Override public String expectedSearchSuggestion() { return null; }
        @Override public InfoItem.InfoType expectedInfoItemType() { return InfoItem.InfoType.CHANNEL; }
        // @formatter:on
    }

    public static class Playlists extends DefaultSearchExtractorTest {
        private static SearchExtractor extractor;
        private static final String QUERY = "lill uzi vert";

        @BeforeAll
        public static void setUp() throws Exception {
            NewPipe.init(DownloaderTestImpl.getInstance());
            extractor = SoundCloud.getSearchExtractor(QUERY, singletonList(PLAYLISTS), "");
            extractor.fetchPage();
        }

        // @formatter:off
        @Override public SearchExtractor extractor() { return extractor; }
        @Override public StreamingService expectedService() { return SoundCloud; }
        @Override public String expectedName() { return QUERY; }
        @Override public String expectedId() { return QUERY; }
        @Override public String expectedUrlContains() { return "soundcloud.com/search/playlists?q=" + urlEncode(QUERY); }
        @Override public String expectedOriginalUrlContains() { return "soundcloud.com/search/playlists?q=" + urlEncode(QUERY); }
        @Override public String expectedSearchString() { return QUERY; }
        @Nullable @Override public String expectedSearchSuggestion() { return null; }
        @Override public InfoItem.InfoType expectedInfoItemType() { return InfoItem.InfoType.PLAYLIST; }
        // @formatter:on
    }

    public static class PagingTest {
        @Test
        public void duplicatedItemsCheck() throws Exception {
            NewPipe.init(DownloaderTestImpl.getInstance());
            final SearchExtractor extractor = SoundCloud.getSearchExtractor("cirque du soleil", singletonList(TRACKS), "");
            extractor.fetchPage();

            final InfoItemsPage<InfoItem> page1 = extractor.getInitialPage();
            final InfoItemsPage<InfoItem> page2 = extractor.getPage(page1.getNextPage());

            assertNoDuplicatedItems(SoundCloud, page1, page2);
        }
    }

    private static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static class UserVerified extends DefaultSearchExtractorTest {
        private static SearchExtractor extractor;
        private static final String QUERY = "David Guetta";

        @BeforeAll
        public static void setUp() throws Exception {
            NewPipe.init(DownloaderTestImpl.getInstance());
            extractor = SoundCloud.getSearchExtractor(QUERY, singletonList(USERS), "");
            extractor.fetchPage();
        }

        @Override public SearchExtractor extractor() { return extractor; }
        @Override public StreamingService expectedService() { return SoundCloud; }
        @Override public String expectedName() { return QUERY; }
        @Override public String expectedId() { return QUERY; }
        @Override public String expectedUrlContains() { return "soundcloud.com/search/users?q=" + urlEncode(QUERY); }
        @Override public String expectedOriginalUrlContains() { return "soundcloud.com/search/users?q=" + urlEncode(QUERY); }
        @Override public String expectedSearchString() { return QUERY; }
        @Nullable @Override public String expectedSearchSuggestion() { return null; }

        @Override public InfoItem.InfoType expectedInfoItemType() { return InfoItem.InfoType.CHANNEL; }

        @Test
        void testIsVerified() throws IOException, ExtractionException {
            final List<InfoItem> items = extractor.getInitialPage().getItems();
            boolean verified = false;
            for (InfoItem item : items) {
                if (item.getUrl().equals("https://soundcloud.com/davidguetta")) {
                    verified = ((ChannelInfoItem) item).isVerified();
                    break;
                }
            }
            assertTrue(verified);
        }
    }
}

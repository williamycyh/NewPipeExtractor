package org.schabi.newpipe.extractor.services.media_ccc.search;

import org.junit.jupiter.api.BeforeAll;
import org.schabi.newpipe.downloader.DownloaderTestImpl;
import com.ppd.ersan.video.InfoItem;
import com.ppd.ersan.video.NewPipe;
import com.ppd.ersan.video.StreamingService;
import com.ppd.ersan.video.search.SearchExtractor;
import org.schabi.newpipe.extractor.services.DefaultSearchExtractorTest;

import javax.annotation.Nullable;

import static java.util.Collections.singletonList;
import static com.ppd.ersan.video.ServiceList.MediaCCC;
import static com.ppd.ersan.video.services.media_ccc.linkHandler.MediaCCCSearchQueryHandlerFactory.CONFERENCES;
import static com.ppd.ersan.video.services.media_ccc.linkHandler.MediaCCCSearchQueryHandlerFactory.EVENTS;

public class MediaCCCSearchExtractorTest {
    public static class All extends DefaultSearchExtractorTest {
        private static SearchExtractor extractor;
        private static final String QUERY = "kde";

        @BeforeAll
        public static void setUp() throws Exception {
            NewPipe.init(DownloaderTestImpl.getInstance());
            extractor = MediaCCC.getSearchExtractor(QUERY);
            extractor.fetchPage();
        }

        @Override public SearchExtractor extractor() { return extractor; }
        @Override public StreamingService expectedService() { return MediaCCC; }
        @Override public String expectedName() { return QUERY; }
        @Override public String expectedId() { return QUERY; }
        @Override public String expectedUrlContains() { return "media.ccc.de/public/events/search?q=" + QUERY; }
        @Override public String expectedOriginalUrlContains() { return "media.ccc.de/public/events/search?q=" + QUERY; }
        @Override public String expectedSearchString() { return QUERY; }
        @Nullable @Override public String expectedSearchSuggestion() { return null; }

        @Override public boolean expectedHasMoreItems() { return false; }
    }

    public static class Conferences extends DefaultSearchExtractorTest {
        private static SearchExtractor extractor;
        private static final String QUERY = "c3";

        @BeforeAll
        public static void setUp() throws Exception {
            NewPipe.init(DownloaderTestImpl.getInstance());
            extractor = MediaCCC.getSearchExtractor(QUERY, singletonList(CONFERENCES), "");
            extractor.fetchPage();
        }

        @Override public SearchExtractor extractor() { return extractor; }
        @Override public StreamingService expectedService() { return MediaCCC; }
        @Override public String expectedName() { return QUERY; }
        @Override public String expectedId() { return QUERY; }
        @Override public String expectedUrlContains() { return "media.ccc.de/public/events/search?q=" + QUERY; }
        @Override public String expectedOriginalUrlContains() { return "media.ccc.de/public/events/search?q=" + QUERY; }
        @Override public String expectedSearchString() { return QUERY; }
        @Nullable @Override public String expectedSearchSuggestion() { return null; }

        @Nullable @Override public InfoItem.InfoType expectedInfoItemType() { return InfoItem.InfoType.CHANNEL; }
        @Override public boolean expectedHasMoreItems() { return false; }
    }

    public static class Events extends DefaultSearchExtractorTest {
        private static SearchExtractor extractor;
        private static final String QUERY = "linux";

        @BeforeAll
        public static void setUp() throws Exception {
            NewPipe.init(DownloaderTestImpl.getInstance());
            extractor = MediaCCC.getSearchExtractor(QUERY, singletonList(EVENTS), "");
            extractor.fetchPage();
        }

        @Override public SearchExtractor extractor() { return extractor; }
        @Override public StreamingService expectedService() { return MediaCCC; }
        @Override public String expectedName() { return QUERY; }
        @Override public String expectedId() { return QUERY; }
        @Override public String expectedUrlContains() { return "media.ccc.de/public/events/search?q=" + QUERY; }
        @Override public String expectedOriginalUrlContains() { return "media.ccc.de/public/events/search?q=" + QUERY; }
        @Override public String expectedSearchString() { return QUERY; }
        @Nullable @Override public String expectedSearchSuggestion() { return null; }

        @Nullable @Override public InfoItem.InfoType expectedInfoItemType() { return InfoItem.InfoType.STREAM; }
        @Override public boolean expectedHasMoreItems() { return false; }
    }
}

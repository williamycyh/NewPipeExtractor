package org.schabi.newpipe.extractor.services.peertube.search;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.schabi.newpipe.downloader.DownloaderTestImpl;
import vthirtylib.second.third.downdir.InfoItem;
import vthirtylib.second.third.downdir.ListExtractor.InfoItemsPage;
import vthirtylib.second.third.downdir.NewPipe;
import vthirtylib.second.third.downdir.StreamingService;
import vthirtylib.second.third.downdir.search.SearchExtractor;
import org.schabi.newpipe.extractor.services.DefaultSearchExtractorTest;
import vthirtylib.second.third.downdir.services.peertube.PeertubeInstance;
import vthirtylib.second.third.downdir.services.peertube.linkHandler.PeertubeSearchQueryHandlerFactory;

import javax.annotation.Nullable;

import static java.util.Collections.singletonList;
import static vthirtylib.second.third.downdir.ServiceList.PeerTube;
import static org.schabi.newpipe.extractor.services.DefaultTests.assertNoDuplicatedItems;
import static vthirtylib.second.third.downdir.services.peertube.linkHandler.PeertubeSearchQueryHandlerFactory.VIDEOS;

public class PeertubeSearchExtractorTest {

    public static class All extends DefaultSearchExtractorTest {
        private static SearchExtractor extractor;
        private static final String QUERY = "fsf";

        @BeforeAll
        public static void setUp() throws Exception {
            NewPipe.init(DownloaderTestImpl.getInstance());
            // setting instance might break test when running in parallel
            PeerTube.setInstance(new PeertubeInstance("https://framatube.org", "Framatube"));
            extractor = PeerTube.getSearchExtractor(QUERY);
            extractor.fetchPage();
        }

        @Override public SearchExtractor extractor() { return extractor; }
        @Override public StreamingService expectedService() { return PeerTube; }
        @Override public String expectedName() { return QUERY; }
        @Override public String expectedId() { return QUERY; }
        @Override public String expectedUrlContains() { return "/search/videos?search=" + QUERY; }
        @Override public String expectedOriginalUrlContains() { return "/search/videos?search=" + QUERY; }
        @Override public String expectedSearchString() { return QUERY; }
        @Nullable @Override public String expectedSearchSuggestion() { return null; }
    }

    public static class SepiaSearch extends DefaultSearchExtractorTest {
        private static SearchExtractor extractor;
        private static final String QUERY = "kde";

        @BeforeAll
        public static void setUp() throws Exception {
            NewPipe.init(DownloaderTestImpl.getInstance());
            // setting instance might break test when running in parallel
            PeerTube.setInstance(new PeertubeInstance("https://framatube.org", "Framatube"));
            extractor = PeerTube.getSearchExtractor(QUERY, singletonList(PeertubeSearchQueryHandlerFactory.SEPIA_VIDEOS), "");
            extractor.fetchPage();
        }

        @Override public SearchExtractor extractor() { return extractor; }
        @Override public StreamingService expectedService() { return PeerTube; }
        @Override public String expectedName() { return QUERY; }
        @Override public String expectedId() { return QUERY; }
        @Override public String expectedUrlContains() { return "/search/videos?search=" + QUERY; }
        @Override public String expectedOriginalUrlContains() { return "/search/videos?search=" + QUERY; }
        @Override public String expectedSearchString() { return QUERY; }
        @Nullable @Override public String expectedSearchSuggestion() { return null; }
    }

    public static class PagingTest {
        @Test
        @Disabled("Exception in CI: javax.net.ssl.SSLHandshakeException: PKIX path validation failed: java.security.cert.CertPathValidatorException: validity check failed")
        public void duplicatedItemsCheck() throws Exception {
            NewPipe.init(DownloaderTestImpl.getInstance());
            final SearchExtractor extractor = PeerTube.getSearchExtractor("internet", singletonList(VIDEOS), "");
            extractor.fetchPage();

            final InfoItemsPage<InfoItem> page1 = extractor.getInitialPage();
            final InfoItemsPage<InfoItem> page2 = extractor.getPage(page1.getNextPage());

            assertNoDuplicatedItems(PeerTube, page1, page2);
        }
    }
}

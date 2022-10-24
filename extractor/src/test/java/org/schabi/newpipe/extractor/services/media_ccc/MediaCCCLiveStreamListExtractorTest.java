package org.schabi.newpipe.extractor.services.media_ccc;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.schabi.newpipe.downloader.DownloaderTestImpl;
import com.github.video.downloader.InfoItem;
import com.github.video.downloader.NewPipe;
import com.github.video.downloader.kis.KioskExtractor;

import java.util.List;

import static com.github.video.downloader.ServiceList.MediaCCC;

public class MediaCCCLiveStreamListExtractorTest {
    private static KioskExtractor extractor;

    @BeforeAll
    public static void setUpClass() throws Exception {
        NewPipe.init(DownloaderTestImpl.getInstance());
        extractor = MediaCCC.getKioskList().getExtractorById("live", null);
        extractor.fetchPage();
    }

    @Test
    public void getConferencesListTest() throws Exception {
        final List<InfoItem> items = extractor.getInitialPage().getItems();
        // just test if there is an exception thrown
    }

}

package org.schabi.newpipe.extractor.services.media_ccc;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.schabi.newpipe.downloader.DownloaderTestImpl;
import vmeno.yyml.nnbersi.downd.InfoItem;
import vmeno.yyml.nnbersi.downd.NewPipe;
import vmeno.yyml.nnbersi.downd.kis.KioskExtractor;

import java.util.List;

import static vmeno.yyml.nnbersi.downd.ServiceList.MediaCCC;

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

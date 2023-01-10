package org.schabi.newpipe.extractor.services.media_ccc;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.schabi.newpipe.downloader.DownloaderTestImpl;
import org.schabi.newpipe.extractor.ExtractorAsserts;

import tndown.tndir.simplevd.vdwload.InfoItem;
import tndown.tndir.simplevd.vdwload.NewPipe;
import tndown.tndir.simplevd.vdwload.kis.KioskExtractor;
import tndown.tndir.simplevd.vdwload.services.media_ccc.extractors.MediaCCCConferenceKiosk;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static tndown.tndir.simplevd.vdwload.ServiceList.MediaCCC;


/**
 * Test {@link MediaCCCConferenceKiosk}
 */
public class MediaCCCConferenceListExtractorTest {

    private static KioskExtractor extractor;

    @BeforeAll
    public static void setUpClass() throws Exception {
        NewPipe.init(DownloaderTestImpl.getInstance());
        extractor = MediaCCC.getKioskList().getExtractorById("conferences", null);
        extractor.fetchPage();
    }

    @Test
    void getConferencesListTest() throws Exception {
        ExtractorAsserts.assertGreaterOrEqual(174, extractor.getInitialPage().getItems().size());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "FrOSCon 2016",
            "ChaosWest @ 35c3",
            "CTreffOS chaOStalks",
            "Datenspuren 2015",
            "Chaos Singularity 2017",
            "SIGINT10",
            "Vintage Computing Festival Berlin 2015",
            "FIfFKon 2015",
            "33C3: trailers",
            "Blinkenlights"
    })
    void conferenceTypeTest(final String name) throws Exception {
        final List<InfoItem> itemList = extractor.getInitialPage().getItems();
        assertTrue(itemList.stream().anyMatch(item -> name.equals(item.getName())));
    }
}

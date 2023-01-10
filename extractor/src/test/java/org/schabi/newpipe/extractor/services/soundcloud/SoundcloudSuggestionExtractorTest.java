package org.schabi.newpipe.extractor.services.soundcloud;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.schabi.newpipe.downloader.DownloaderTestImpl;
import tndown.tndir.simplevd.vdwload.NewPipe;
import tndown.tndir.simplevd.vdwload.exceptions.ExtractionException;
import tndown.tndir.simplevd.vdwload.suggestion.SuggestionExtractor;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static tndown.tndir.simplevd.vdwload.ServiceList.SoundCloud;

/**
 * Test for {@link SuggestionExtractor}
 */
public class SoundcloudSuggestionExtractorTest {
    private static SuggestionExtractor suggestionExtractor;

    @BeforeAll
    public static void setUp() {
        NewPipe.init(DownloaderTestImpl.getInstance());
        suggestionExtractor = SoundCloud.getSuggestionExtractor();
    }

    @Test
    public void testIfSuggestions() throws IOException, ExtractionException {
        assertFalse(suggestionExtractor.suggestionList("lil uzi vert").isEmpty());
    }
}

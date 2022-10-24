// Created by Fynn Godau 2019, licensed GNU GPL version 3 or later

package org.schabi.newpipe.extractor.services.bandcamp;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.schabi.newpipe.downloader.DownloaderTestImpl;
import com.github.video.downloader.NewPipe;
import com.github.video.downloader.exceptions.ExtractionException;
import com.github.video.downloader.services.bandcamp.extractors.BandcampSuggestionExtractor;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static com.github.video.downloader.ServiceList.Bandcamp;

/**
 * Tests for {@link BandcampSuggestionExtractor}
 */
public class BandcampSuggestionExtractorTest {

    private static BandcampSuggestionExtractor extractor;

    @BeforeAll
    public static void setUp() {
        NewPipe.init(DownloaderTestImpl.getInstance());
        extractor = (BandcampSuggestionExtractor) Bandcamp.getSuggestionExtractor();
    }

    @Test
    public void testSearchExample() throws IOException, ExtractionException {
        final List<String> c418 = extractor.suggestionList("c418");

        assertTrue(c418.contains("C418"));

        // There should be five results, but we can't be sure of that forever
        assertTrue(c418.size() > 2);
    }
}

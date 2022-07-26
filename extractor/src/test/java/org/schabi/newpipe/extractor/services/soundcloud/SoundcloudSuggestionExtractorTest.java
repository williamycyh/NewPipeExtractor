package org.schabi.newpipe.extractor.services.soundcloud;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.schabi.newpipe.downloader.DownloaderTestImpl;
import vmeno.yyml.nnbersi.downd.NewPipe;
import vmeno.yyml.nnbersi.downd.exceptions.ExtractionException;
import vmeno.yyml.nnbersi.downd.suggestion.SuggestionExtractor;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static vmeno.yyml.nnbersi.downd.ServiceList.SoundCloud;

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

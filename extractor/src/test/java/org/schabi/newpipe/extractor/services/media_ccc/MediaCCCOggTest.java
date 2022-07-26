package org.schabi.newpipe.extractor.services.media_ccc;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.schabi.newpipe.downloader.DownloaderTestImpl;
import vmeno.yyml.nnbersi.downd.NewPipe;
import vmeno.yyml.nnbersi.downd.services.media_ccc.extractors.MediaCCCStreamExtractor;
import vmeno.yyml.nnbersi.downd.stream.AudioStream;
import vmeno.yyml.nnbersi.downd.stream.StreamExtractor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static vmeno.yyml.nnbersi.downd.ServiceList.MediaCCC;

/**
 * Test {@link MediaCCCStreamExtractor}
 */
public class MediaCCCOggTest {
    // test against https://media.ccc.de/public/events/1317
    private static StreamExtractor extractor;

    @BeforeAll
    public static void setUpClass() throws Exception {
        NewPipe.init(DownloaderTestImpl.getInstance());

        extractor = MediaCCC.getStreamExtractor("https://media.ccc.de/public/events/1317");
        extractor.fetchPage();
    }

    @Test
    public void getAudioStreamsCount() throws Exception {
        assertEquals(1, extractor.getAudioStreams().size());
    }

    @Test
    public void getAudioStreamsContainOgg() throws Exception {
        for (AudioStream stream : extractor.getAudioStreams()) {
            assertEquals("OGG", stream.getFormat().toString());
        }
    }
}

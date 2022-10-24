package org.schabi.newpipe.extractor.services.media_ccc;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.schabi.newpipe.downloader.DownloaderTestImpl;
import com.github.video.downloader.NewPipe;
import com.github.video.downloader.services.media_ccc.extractors.MediaCCCStreamExtractor;
import com.github.video.downloader.stream.AudioStream;
import com.github.video.downloader.stream.StreamExtractor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.github.video.downloader.ServiceList.MediaCCC;

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

package org.schabi.newpipe.extractor.services.peertube;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.schabi.newpipe.downloader.DownloaderTestImpl;
import org.schabi.newpipe.extractor.ExtractorAsserts;
import tndown.tndir.simplevd.vdwload.NewPipe;
import tndown.tndir.simplevd.vdwload.exceptions.ParsingException;
import tndown.tndir.simplevd.vdwload.services.peertube.extractors.PeertubePlaylistExtractor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tndown.tndir.simplevd.vdwload.ServiceList.PeerTube;

public class PeertubePlaylistExtractorTest {

    public static class Shocking {
        private static PeertubePlaylistExtractor extractor;

        @BeforeAll
        public static void setUp() throws Exception {
            NewPipe.init(DownloaderTestImpl.getInstance());
            extractor = (PeertubePlaylistExtractor) PeerTube
                    .getPlaylistExtractor("https://framatube.org/videos/watch/playlist/96b0ee2b-a5a7-4794-8769-58d8ccb79ab7");
            extractor.fetchPage();
        }

        @Test
        public void testGetName() throws ParsingException {
            assertEquals("Shocking !", extractor.getName());
        }

        @Test
        @Disabled("URL changes with every request")
        public void testGetThumbnailUrl() throws ParsingException {
            assertEquals("https://framatube.org/static/thumbnails/playlist-96b0ee2b-a5a7-4794-8769-58d8ccb79ab7.jpg", extractor.getThumbnailUrl());
        }

        @Test
        public void testGetUploaderUrl() throws ParsingException {
            assertEquals("https://skeptikon.fr/accounts/metadechoc", extractor.getUploaderUrl());
        }

        @Test
        public void testGetUploaderAvatarUrl() throws ParsingException {
            assertEquals("https://framatube.org/lazy-static/avatars/cd0f781d-0287-4be2-94f1-24cd732337b2.jpg", extractor.getUploaderAvatarUrl());
        }

        @Test
        public void testGetUploaderName() throws ParsingException {
            assertEquals("Méta de Choc", extractor.getUploaderName());
        }

        @Test
        public void testGetStreamCount() throws ParsingException {
            ExtractorAsserts.assertGreaterOrEqual(39, extractor.getStreamCount());
        }

        @Test
        public void testGetSubChannelUrl() throws ParsingException {
            assertEquals("https://skeptikon.fr/video-channels/metadechoc_channel", extractor.getSubChannelUrl());
        }

        @Test
        public void testGetSubChannelName() throws ParsingException {
            assertEquals("SHOCKING !", extractor.getSubChannelName());
        }

        @Test
        public void testGetSubChannelAvatarUrl() throws ParsingException {
            assertEquals("https://framatube.org/lazy-static/avatars/f1dcd0e8-e651-42ed-ae81-bb3bd4aff2bc.png",
                    extractor.getSubChannelAvatarUrl());
        }
    }
}

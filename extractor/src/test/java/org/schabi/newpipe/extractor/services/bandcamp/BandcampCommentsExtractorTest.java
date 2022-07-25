package org.schabi.newpipe.extractor.services.bandcamp;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.schabi.newpipe.downloader.DownloaderTestImpl;
import com.downloader.twotwo.video.ListExtractor;
import com.downloader.twotwo.video.NewPipe;
import com.downloader.twotwo.video.comments.CommentsExtractor;
import com.downloader.twotwo.video.comments.CommentsInfoItem;
import com.downloader.twotwo.video.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.services.DefaultTests;
import com.downloader.twotwo.video.utils.Utils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static com.downloader.twotwo.video.ServiceList.Bandcamp;

public class BandcampCommentsExtractorTest {

    private static CommentsExtractor extractor;

    @BeforeAll
    public static void setUp() throws ExtractionException, IOException {
        NewPipe.init(DownloaderTestImpl.getInstance());
        extractor = Bandcamp.getCommentsExtractor("https://floatingpoints.bandcamp.com/album/promises");
        extractor.fetchPage();
    }

    @Test
    public void hasComments() throws IOException, ExtractionException {
        assertTrue(extractor.getInitialPage().getItems().size() >= 3);
    }

    @Test
    public void testGetCommentsAllData() throws IOException, ExtractionException {
        ListExtractor.InfoItemsPage<CommentsInfoItem> comments = extractor.getInitialPage();

        DefaultTests.defaultTestListOfItems(Bandcamp, comments.getItems(), comments.getErrors());
        for (CommentsInfoItem c : comments.getItems()) {
            assertFalse(Utils.isBlank(c.getUploaderName()));
            assertFalse(Utils.isBlank(c.getUploaderAvatarUrl()));
            assertFalse(Utils.isBlank(c.getCommentText()));
            assertFalse(Utils.isBlank(c.getName()));
            assertFalse(Utils.isBlank(c.getThumbnailUrl()));
            assertFalse(Utils.isBlank(c.getUrl()));
            assertEquals(-1, c.getLikeCount());
            assertTrue(Utils.isBlank(c.getTextualLikeCount()));
        }
    }
}

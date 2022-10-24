package org.schabi.newpipe.extractor.services.bandcamp;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.schabi.newpipe.downloader.DownloaderTestImpl;
import com.github.video.downloader.ListExtractor;
import com.github.video.downloader.NewPipe;
import com.github.video.downloader.comments.CommentsExtractor;
import com.github.video.downloader.comments.CommentsInfoItem;
import com.github.video.downloader.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.services.DefaultTests;
import com.github.video.downloader.utils.Utils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static com.github.video.downloader.ServiceList.Bandcamp;

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

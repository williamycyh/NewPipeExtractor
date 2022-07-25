package com.ppd.ersan.video.utils;

import com.ppd.ersan.video.stream.StreamExtractor;
import com.ppd.ersan.video.stream.StreamInfo;
import com.ppd.ersan.video.Info;
import com.ppd.ersan.video.InfoItem;
import com.ppd.ersan.video.InfoItemsCollector;
import com.ppd.ersan.video.ListExtractor;
import com.ppd.ersan.video.ListExtractor.InfoItemsPage;

import java.util.Collections;
import java.util.List;

public final class ExtractorHelper {
    private ExtractorHelper() {
    }

    public static <T extends InfoItem> InfoItemsPage<T> getItemsPageOrLogError(
            final Info info, final ListExtractor<T> extractor) {
        try {
            final InfoItemsPage<T> page = extractor.getInitialPage();
            info.addAllErrors(page.getErrors());

            return page;
        } catch (final Exception e) {
            info.addError(e);
            return InfoItemsPage.emptyPage();
        }
    }


    public static List<InfoItem> getRelatedItemsOrLogError(final StreamInfo info,
                                                           final StreamExtractor extractor) {
        try {
            final InfoItemsCollector<? extends InfoItem, ?> collector = extractor.getRelatedItems();
            if (collector == null) {
                return Collections.emptyList();
            }
            info.addAllErrors(collector.getErrors());

            //noinspection unchecked
            return (List<InfoItem>) collector.getItems();
        } catch (final Exception e) {
            info.addError(e);
            return Collections.emptyList();
        }
    }

    /**
     * @deprecated Use {@link #getRelatedItemsOrLogError(StreamInfo, StreamExtractor)}
     */
    @Deprecated
    public static List<InfoItem> getRelatedVideosOrLogError(final StreamInfo info,
                                                            final StreamExtractor extractor) {
        return getRelatedItemsOrLogError(info, extractor);
    }

}

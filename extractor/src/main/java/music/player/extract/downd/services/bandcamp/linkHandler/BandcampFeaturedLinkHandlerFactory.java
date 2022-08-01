// Created by Fynn Godau 2019, licensed GNU GPL version 3 or later

package music.player.extract.downd.services.bandcamp.linkHandler;

import music.player.extract.downd.linkhandler.ListLinkHandlerFactory;
import music.player.extract.downd.services.bandcamp.extractors.BandcampExtractorHelper;
import music.player.extract.downd.services.bandcamp.extractors.BandcampFeaturedExtractor;
import music.player.extract.downd.services.bandcamp.extractors.BandcampRadioExtractor;
import music.player.extract.downd.utils.Utils;

import java.util.List;

public class BandcampFeaturedLinkHandlerFactory extends ListLinkHandlerFactory {

    @Override
    public String getUrl(final String id,
                         final List<String> contentFilter,
                         final String sortFilter) {
        if (id.equals(BandcampFeaturedExtractor.KIOSK_FEATURED)) {
            return BandcampFeaturedExtractor.FEATURED_API_URL; // doesn't have a website
        } else if (id.equals(BandcampRadioExtractor.KIOSK_RADIO)) {
            return BandcampRadioExtractor.RADIO_API_URL; // doesn't have its own website
        } else {
            return null;
        }
    }

    @Override
    public String getId(final String url) {
        final String fixedUrl = Utils.replaceHttpWithHttps(url);
        if (BandcampExtractorHelper.isRadioUrl(fixedUrl) || fixedUrl.equals(BandcampRadioExtractor.RADIO_API_URL)) {
            return BandcampRadioExtractor.KIOSK_RADIO;
        } else if (fixedUrl.equals(BandcampFeaturedExtractor.FEATURED_API_URL)) {
            return BandcampFeaturedExtractor.KIOSK_FEATURED;
        } else {
            return null;
        }
    }

    @Override
    public boolean onAcceptUrl(final String url) {
        final String fixedUrl = Utils.replaceHttpWithHttps(url);
        return fixedUrl.equals(BandcampFeaturedExtractor.FEATURED_API_URL)
                || fixedUrl.equals(BandcampRadioExtractor.RADIO_API_URL)
                || BandcampExtractorHelper.isRadioUrl(fixedUrl);
    }
}

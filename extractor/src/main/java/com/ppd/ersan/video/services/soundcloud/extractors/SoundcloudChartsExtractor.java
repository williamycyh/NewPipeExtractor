package com.ppd.ersan.video.services.soundcloud.extractors;

import com.ppd.ersan.video.Page;
import com.ppd.ersan.video.ServiceList;
import com.ppd.ersan.video.StreamingService;
import com.ppd.ersan.video.downloader.Downloader;
import com.ppd.ersan.video.kiosk.KioskExtractor;
import com.ppd.ersan.video.linkhandler.ListLinkHandler;
import com.ppd.ersan.video.localization.ContentCountry;
import com.ppd.ersan.video.stream.StreamInfoItem;
import com.ppd.ersan.video.stream.StreamInfoItemsCollector;
import com.ppd.ersan.video.utils.Utils;
import com.ppd.ersan.video.exceptions.ExtractionException;
import com.ppd.ersan.video.services.soundcloud.SoundcloudParsingHelper;

import javax.annotation.Nonnull;
import java.io.IOException;

import static com.ppd.ersan.video.services.soundcloud.SoundcloudParsingHelper.SOUNDCLOUD_API_V2_URL;
import static com.ppd.ersan.video.utils.Utils.isNullOrEmpty;

public class SoundcloudChartsExtractor extends KioskExtractor<StreamInfoItem> {
    public SoundcloudChartsExtractor(final StreamingService service,
                                     final ListLinkHandler linkHandler,
                                     final String kioskId) {
        super(service, linkHandler, kioskId);
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader) {
    }

    @Nonnull
    @Override
    public String getName() {
        return getId();
    }

    @Override
    public InfoItemsPage<StreamInfoItem> getPage(final Page page) throws IOException,
            ExtractionException {
        if (page == null || Utils.isNullOrEmpty(page.getUrl())) {
            throw new IllegalArgumentException("Page doesn't contain an URL");
        }

        final StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
        final String nextPageUrl = SoundcloudParsingHelper.getStreamsFromApi(collector,
                page.getUrl(), true);

        return new InfoItemsPage<>(collector, new Page(nextPageUrl));
    }

    @Nonnull
    @Override
    public InfoItemsPage<StreamInfoItem> getInitialPage() throws IOException, ExtractionException {
        final StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());

        String apiUrl = SOUNDCLOUD_API_V2_URL + "charts" + "?genre=soundcloud:genres:all-music"
                + "&client_id=" + SoundcloudParsingHelper.clientId();

        if (getId().equals("Top 50")) {
            apiUrl += "&kind=top";
        } else {
            apiUrl += "&kind=trending";
        }

        final ContentCountry contentCountry = ServiceList.SoundCloud.getContentCountry();
        String apiUrlWithRegion = null;
        if (getService().getSupportedCountries().contains(contentCountry)) {
            apiUrlWithRegion = apiUrl + "&region=soundcloud:regions:"
                    + contentCountry.getCountryCode();
        }

        String nextPageUrl;
        try {
            nextPageUrl = SoundcloudParsingHelper.getStreamsFromApi(collector,
                    apiUrlWithRegion == null ? apiUrl : apiUrlWithRegion, true);
        } catch (final IOException e) {
            // Request to other region may be geo-restricted.
            // See https://github.com/TeamNewPipe/NewPipeExtractor/issues/537.
            // We retry without the specified region.
            nextPageUrl = SoundcloudParsingHelper.getStreamsFromApi(collector, apiUrl, true);
        }

        return new InfoItemsPage<>(collector, new Page(nextPageUrl));
    }
}

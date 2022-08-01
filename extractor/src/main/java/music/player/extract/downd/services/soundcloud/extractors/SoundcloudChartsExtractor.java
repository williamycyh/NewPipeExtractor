package music.player.extract.downd.services.soundcloud.extractors;

import music.player.extract.downd.Page;
import music.player.extract.downd.ServiceList;
import music.player.extract.downd.StreamingService;
import music.player.extract.downd.downloader.Downloader;
import music.player.extract.downd.kis.KioskExtractor;
import music.player.extract.downd.linkhandler.ListLinkHandler;
import music.player.extract.downd.localization.ContentCountry;
import music.player.extract.downd.stream.StreamInfoItem;
import music.player.extract.downd.stream.StreamInfoItemsCollector;
import music.player.extract.downd.utils.Utils;
import music.player.extract.downd.exceptions.ExtractionException;
import music.player.extract.downd.services.soundcloud.SoundcloudParsingHelper;

import javax.annotation.Nonnull;
import java.io.IOException;

import music.player.extract.downd.ListExtractor;

import static music.player.extract.downd.services.soundcloud.SoundcloudParsingHelper.SOUNDCLOUD_API_V2_URL;
import static music.player.extract.downd.utils.Utils.isNullOrEmpty;

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
    public ListExtractor.InfoItemsPage<StreamInfoItem> getPage(final Page page) throws IOException,
            ExtractionException {
        if (page == null || Utils.isNullOrEmpty(page.getUrl())) {
            throw new IllegalArgumentException("Page doesn't contain an URL");
        }

        final StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
        final String nextPageUrl = SoundcloudParsingHelper.getStreamsFromApi(collector,
                page.getUrl(), true);

        return new ListExtractor.InfoItemsPage<>(collector, new Page(nextPageUrl));
    }

    @Nonnull
    @Override
    public ListExtractor.InfoItemsPage<StreamInfoItem> getInitialPage() throws IOException, ExtractionException {
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

        return new ListExtractor.InfoItemsPage<>(collector, new Page(nextPageUrl));
    }
}

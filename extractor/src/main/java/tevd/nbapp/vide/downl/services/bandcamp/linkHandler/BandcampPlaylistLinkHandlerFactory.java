// Created by Fynn Godau 2019, licensed GNU GPL version 3 or later

package tevd.nbapp.vide.downl.services.bandcamp.linkHandler;

import tevd.nbapp.vide.downl.exceptions.ParsingException;
import tevd.nbapp.vide.downl.linkhandler.ListLinkHandlerFactory;
import tevd.nbapp.vide.downl.services.bandcamp.extractors.BandcampExtractorHelper;

import java.util.List;

/**
 * Just as with streams, the album ids are essentially useless for us.
 */
public class BandcampPlaylistLinkHandlerFactory extends ListLinkHandlerFactory {
    @Override
    public String getId(final String url) throws ParsingException {
        return getUrl(url);
    }

    @Override
    public String getUrl(final String url,
                         final List<String> contentFilter,
                         final String sortFilter) throws ParsingException {
        return url;
    }

    /**
     * Accepts all bandcamp URLs that contain /album/ behind their domain name.
     */
    @Override
    public boolean onAcceptUrl(final String url) throws ParsingException {

        // Exclude URLs which do not lead to an album
        if (!url.toLowerCase().matches("https?://.+\\..+/album/.+")) {
            return false;
        }

        // Test whether domain is supported
        return BandcampExtractorHelper.isSupportedDomain(url);
    }
}

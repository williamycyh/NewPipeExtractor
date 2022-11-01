package tevd.nbapp.vide.downl.services.bandcamp.linkHandler;

import tevd.nbapp.vide.downl.exceptions.ParsingException;
import tevd.nbapp.vide.downl.linkhandler.ListLinkHandlerFactory;
import tevd.nbapp.vide.downl.services.bandcamp.extractors.BandcampExtractorHelper;

import java.util.List;

/**
 * Like in {@link BandcampStreamLinkHandlerFactory}, tracks have no meaningful IDs except for
 * their URLs
 */
public class BandcampCommentsLinkHandlerFactory extends ListLinkHandlerFactory {

    @Override
    public String getId(final String url) throws ParsingException {
        return url;
    }

    @Override
    public boolean onAcceptUrl(final String url) throws ParsingException {
        // Don't accept URLs that don't point to a track
        if (!url.toLowerCase().matches("https?://.+\\..+/(track|album)/.+")) {
            return false;
        }

        // Test whether domain is supported
        return BandcampExtractorHelper.isSupportedDomain(url);
    }

    @Override
    public String getUrl(final String id,
                         final List<String> contentFilter,
                         final String sortFilter) throws ParsingException {
        return id;
    }
}

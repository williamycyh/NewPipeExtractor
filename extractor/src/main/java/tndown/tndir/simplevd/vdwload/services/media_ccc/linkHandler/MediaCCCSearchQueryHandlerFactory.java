package tndown.tndir.simplevd.vdwload.services.media_ccc.linkHandler;

import tndown.tndir.simplevd.vdwload.exceptions.ParsingException;
import tndown.tndir.simplevd.vdwload.linkhandler.SearchQueryHandlerFactory;
import tndown.tndir.simplevd.vdwload.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class MediaCCCSearchQueryHandlerFactory extends SearchQueryHandlerFactory {
    public static final String ALL = "all";
    public static final String CONFERENCES = "conferences";
    public static final String EVENTS = "events";

    @Override
    public String[] getAvailableContentFilter() {
        return new String[]{
                ALL,
                CONFERENCES,
                EVENTS
        };
    }

    @Override
    public String[] getAvailableSortFilter() {
        return new String[0];
    }

    @Override
    public String getUrl(final String query, final List<String> contentFilter,
                         final String sortFilter) throws ParsingException {
        try {
            return "https://media.ccc.de/public/events/search?q="
                    + URLEncoder.encode(query, Utils.UTF_8);
        } catch (final UnsupportedEncodingException e) {
            throw new ParsingException("Could not create search string with query: " + query, e);
        }
    }
}

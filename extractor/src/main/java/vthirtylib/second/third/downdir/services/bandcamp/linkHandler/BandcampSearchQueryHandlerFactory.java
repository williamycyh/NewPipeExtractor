// Created by Fynn Godau 2019, licensed GNU GPL version 3 or later

package vthirtylib.second.third.downdir.services.bandcamp.linkHandler;

import vthirtylib.second.third.downdir.exceptions.ParsingException;
import vthirtylib.second.third.downdir.linkhandler.SearchQueryHandlerFactory;
import vthirtylib.second.third.downdir.services.bandcamp.extractors.BandcampExtractorHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class BandcampSearchQueryHandlerFactory extends SearchQueryHandlerFactory {


    @Override
    public String getUrl(final String query,
                         final List<String> contentFilter,
                         final String sortFilter) throws ParsingException {
        try {
            return BandcampExtractorHelper.BASE_URL + "/search?q=" + URLEncoder.encode(query, "UTF-8") + "&page=1";
        } catch (final UnsupportedEncodingException e) {
            throw new ParsingException("query \"" + query + "\" could not be encoded", e);
        }
    }
}

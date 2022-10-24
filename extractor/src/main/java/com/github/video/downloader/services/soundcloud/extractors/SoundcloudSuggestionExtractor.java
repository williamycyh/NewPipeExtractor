package com.github.video.downloader.services.soundcloud.extractors;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import com.github.video.downloader.NewPipe;
import com.github.video.downloader.StreamingService;
import com.github.video.downloader.downloader.Downloader;
import com.github.video.downloader.utils.Utils;
import com.github.video.downloader.exceptions.ExtractionException;
import com.github.video.downloader.exceptions.ParsingException;
import com.github.video.downloader.services.soundcloud.SoundcloudParsingHelper;
import com.github.video.downloader.suggestion.SuggestionExtractor;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static com.github.video.downloader.services.soundcloud.SoundcloudParsingHelper.SOUNDCLOUD_API_V2_URL;

public class SoundcloudSuggestionExtractor extends SuggestionExtractor {

    public SoundcloudSuggestionExtractor(final StreamingService service) {
        super(service);
    }

    @Override
    public List<String> suggestionList(final String query) throws IOException,
            ExtractionException {
        final List<String> suggestions = new ArrayList<>();
        final Downloader dl = NewPipe.getDownloader();
        final String url = SOUNDCLOUD_API_V2_URL + "search/queries" + "?q="
                + URLEncoder.encode(query, Utils.UTF_8) + "&client_id="
                + SoundcloudParsingHelper.clientId() + "&limit=10";
        final String response = dl.get(url, getExtractorLocalization()).responseBody();

        try {
            final JsonArray collection = JsonParser.object().from(response).getArray("collection");
            for (final Object suggestion : collection) {
                if (suggestion instanceof JsonObject) {
                    suggestions.add(((JsonObject) suggestion).getString("query"));
                }
            }

            return suggestions;
        } catch (final JsonParserException e) {
            throw new ParsingException("Could not parse json response", e);
        }
    }
}

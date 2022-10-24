// Created by Fynn Godau 2019, licensed GNU GPL version 3 or later

package com.github.video.downloader.services.bandcamp.extractors;

import static com.github.video.downloader.services.bandcamp.extractors.BandcampExtractorHelper.BASE_API_URL;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;

import com.github.video.downloader.NewPipe;
import com.github.video.downloader.StreamingService;
import com.github.video.downloader.downloader.Downloader;
import com.github.video.downloader.exceptions.ExtractionException;
import com.github.video.downloader.suggestion.SuggestionExtractor;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BandcampSuggestionExtractor extends SuggestionExtractor {

    private static final String AUTOCOMPLETE_URL = BASE_API_URL + "/fuzzysearch/1/autocomplete?q=";
    public BandcampSuggestionExtractor(final StreamingService service) {
        super(service);
    }

    @Override
    public List<String> suggestionList(final String query) throws IOException, ExtractionException {
        final Downloader downloader = NewPipe.getDownloader();

        try {
            final JsonObject fuzzyResults = JsonParser.object().from(downloader
                    .get(AUTOCOMPLETE_URL + URLEncoder.encode(query, "UTF-8")).responseBody());

            final JsonArray jsonArray = fuzzyResults.getObject("auto")
                    .getArray("results");

            final List<String> suggestions = new ArrayList<>();

            for (final Object fuzzyResult : jsonArray) {
                final String res = ((JsonObject) fuzzyResult).getString("name");

                if (!suggestions.contains(res)) {
                    suggestions.add(res);
                }
            }

            return suggestions;
        } catch (final JsonParserException e) {
            return Collections.emptyList();
        }

    }
}

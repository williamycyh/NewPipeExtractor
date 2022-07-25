package com.downloader.twotwo.video.services.peertube.extractors;

import com.downloader.twotwo.video.StreamingService;
import com.downloader.twotwo.video.suggestion.SuggestionExtractor;

import java.util.Collections;
import java.util.List;

public class PeertubeSuggestionExtractor extends SuggestionExtractor {
    public PeertubeSuggestionExtractor(final StreamingService service) {
        super(service);
    }

    @Override
    public List<String> suggestionList(final String query) {
        return Collections.emptyList();
    }
}

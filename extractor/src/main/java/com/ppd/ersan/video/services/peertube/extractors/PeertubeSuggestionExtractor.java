package com.ppd.ersan.video.services.peertube.extractors;

import com.ppd.ersan.video.StreamingService;
import com.ppd.ersan.video.suggestion.SuggestionExtractor;

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

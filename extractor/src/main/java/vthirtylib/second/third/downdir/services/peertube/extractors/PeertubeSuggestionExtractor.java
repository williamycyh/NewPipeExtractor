package vthirtylib.second.third.downdir.services.peertube.extractors;

import vthirtylib.second.third.downdir.StreamingService;
import vthirtylib.second.third.downdir.suggestion.SuggestionExtractor;

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

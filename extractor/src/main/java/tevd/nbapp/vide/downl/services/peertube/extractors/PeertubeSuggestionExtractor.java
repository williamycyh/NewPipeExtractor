package tevd.nbapp.vide.downl.services.peertube.extractors;

import tevd.nbapp.vide.downl.StreamingService;
import tevd.nbapp.vide.downl.suggestion.SuggestionExtractor;

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

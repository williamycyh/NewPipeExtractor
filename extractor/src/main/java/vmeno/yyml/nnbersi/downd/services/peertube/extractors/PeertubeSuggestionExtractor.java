package vmeno.yyml.nnbersi.downd.services.peertube.extractors;

import vmeno.yyml.nnbersi.downd.StreamingService;
import vmeno.yyml.nnbersi.downd.suggestion.SuggestionExtractor;

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

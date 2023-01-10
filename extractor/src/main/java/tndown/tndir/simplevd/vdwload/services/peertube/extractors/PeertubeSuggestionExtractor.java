package tndown.tndir.simplevd.vdwload.services.peertube.extractors;

import tndown.tndir.simplevd.vdwload.StreamingService;
import tndown.tndir.simplevd.vdwload.suggestion.SuggestionExtractor;

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

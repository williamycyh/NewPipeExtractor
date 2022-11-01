package tevd.nbapp.vide.downl.search;

import tevd.nbapp.vide.downl.InfoItem;
import tevd.nbapp.vide.downl.ListExtractor;
import tevd.nbapp.vide.downl.ListInfo;
import tevd.nbapp.vide.downl.MetaInfo;
import tevd.nbapp.vide.downl.Page;
import tevd.nbapp.vide.downl.StreamingService;
import tevd.nbapp.vide.downl.exceptions.ExtractionException;
import tevd.nbapp.vide.downl.linkhandler.SearchQueryHandler;
import tevd.nbapp.vide.downl.utils.ExtractorHelper;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;

public class SearchInfo extends ListInfo<InfoItem> {
    private final String searchString;
    private String searchSuggestion;
    private boolean isCorrectedSearch;
    private List<MetaInfo> metaInfo;

    public SearchInfo(final int serviceId,
                      final SearchQueryHandler qIHandler,
                      final String searchString) {
        super(serviceId, qIHandler, "Search");
        this.searchString = searchString;
    }


    public static SearchInfo getInfo(final StreamingService service,
                                     final SearchQueryHandler searchQuery)
            throws ExtractionException, IOException {
        final SearchExtractor extractor = service.getSearchExtractor(searchQuery);
        extractor.fetchPage();
        return getInfo(extractor);
    }

    public static SearchInfo getInfo(final SearchExtractor extractor)
            throws ExtractionException, IOException {
        final SearchInfo info = new SearchInfo(
                extractor.getServiceId(),
                extractor.getLinkHandler(),
                extractor.getSearchString());

        try {
            info.setOriginalUrl(extractor.getOriginalUrl());
        } catch (final Exception e) {
            info.addError(e);
        }
        try {
            info.setSearchSuggestion(extractor.getSearchSuggestion());
        } catch (final Exception e) {
            info.addError(e);
        }
        try {
            info.setIsCorrectedSearch(extractor.isCorrectedSearch());
        } catch (final Exception e) {
            info.addError(e);
        }
        try {
            info.setMetaInfo(extractor.getMetaInfo());
        } catch (final Exception e) {
            info.addError(e);
        }

        final ListExtractor.InfoItemsPage<InfoItem> page
                = ExtractorHelper.getItemsPageOrLogError(info, extractor);
        info.setRelatedItems(page.getItems());
        info.setNextPage(page.getNextPage());

        return info;
    }


    public static ListExtractor.InfoItemsPage<InfoItem> getMoreItems(final StreamingService service,
                                                                     final SearchQueryHandler query,
                                                                     final Page page)
            throws IOException, ExtractionException {
        return service.getSearchExtractor(query).getPage(page);
    }

    // Getter
    public String getSearchString() {
        return this.searchString;
    }

    public String getSearchSuggestion() {
        return this.searchSuggestion;
    }

    public boolean isCorrectedSearch() {
        return this.isCorrectedSearch;
    }

    public void setIsCorrectedSearch(final boolean isCorrectedSearch) {
        this.isCorrectedSearch = isCorrectedSearch;
    }

    public void setSearchSuggestion(final String searchSuggestion) {
        this.searchSuggestion = searchSuggestion;
    }

    @Nonnull
    public List<MetaInfo> getMetaInfo() {
        return metaInfo;
    }

    public void setMetaInfo(@Nonnull final List<MetaInfo> metaInfo) {
        this.metaInfo = metaInfo;
    }
}

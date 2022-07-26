package vmeno.yyml.nnbersi.downd.services.peertube.linkHandler;

import vmeno.yyml.nnbersi.downd.ServiceList;
import vmeno.yyml.nnbersi.downd.exceptions.FoundAdException;
import vmeno.yyml.nnbersi.downd.exceptions.ParsingException;
import vmeno.yyml.nnbersi.downd.linkhandler.ListLinkHandlerFactory;

import java.util.List;

public final class PeertubeCommentsLinkHandlerFactory extends ListLinkHandlerFactory {

    private static final PeertubeCommentsLinkHandlerFactory INSTANCE
            = new PeertubeCommentsLinkHandlerFactory();
    private static final String COMMENTS_ENDPOINT = "/api/v1/videos/%s/comment-threads";

    private PeertubeCommentsLinkHandlerFactory() {
    }

    public static PeertubeCommentsLinkHandlerFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public String getId(final String url) throws ParsingException, IllegalArgumentException {
        return PeertubeStreamLinkHandlerFactory.getInstance().getId(url); // the same id is needed
    }

    @Override
    public boolean onAcceptUrl(final String url) throws FoundAdException {
        return url.contains("/videos/") || url.contains("/w/");
    }

    @Override
    public String getUrl(final String id,
                         final List<String> contentFilter,
                         final String sortFilter) throws ParsingException {
        return getUrl(id, contentFilter, sortFilter, ServiceList.PeerTube.getBaseUrl());
    }

    @Override
    public String getUrl(final String id,
                         final List<String> contentFilter,
                         final String sortFilter,
                         final String baseUrl) throws ParsingException {
        return baseUrl + String.format(COMMENTS_ENDPOINT, id);
    }

}

package tevd.nbapp.vide.downl.playlist;

import tevd.nbapp.vide.downl.InfoItemsCollector;
import tevd.nbapp.vide.downl.exceptions.ParsingException;

public class PlaylistInfoItemsCollector
        extends InfoItemsCollector<PlaylistInfoItem, PlaylistInfoItemExtractor> {

    public PlaylistInfoItemsCollector(final int serviceId) {
        super(serviceId);
    }

    @Override
    public PlaylistInfoItem extract(final PlaylistInfoItemExtractor extractor)
            throws ParsingException {
        final PlaylistInfoItem resultItem = new PlaylistInfoItem(
                getServiceId(), extractor.getUrl(), extractor.getName());

        try {
            resultItem.setUploaderName(extractor.getUploaderName());
        } catch (final Exception e) {
            addError(e);
        }
        try {
            resultItem.setThumbnailUrl(extractor.getThumbnailUrl());
        } catch (final Exception e) {
            addError(e);
        }
        try {
            resultItem.setStreamCount(extractor.getStreamCount());
        } catch (final Exception e) {
            addError(e);
        }
        try {
            resultItem.setPlaylistType(extractor.getPlaylistType());
        } catch (final Exception e) {
            addError(e);
        }
        return resultItem;
    }
}

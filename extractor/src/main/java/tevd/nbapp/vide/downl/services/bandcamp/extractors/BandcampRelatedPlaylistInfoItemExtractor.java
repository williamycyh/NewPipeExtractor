// Created by Fynn Godau 2021, licensed GNU GPL version 3 or later

package tevd.nbapp.vide.downl.services.bandcamp.extractors;

import org.jsoup.nodes.Element;
import tevd.nbapp.vide.downl.exceptions.ParsingException;
import tevd.nbapp.vide.downl.playlist.PlaylistInfoItemExtractor;

import javax.annotation.Nonnull;

/**
 * Extracts recommended albums from tracks' website
 */
public class BandcampRelatedPlaylistInfoItemExtractor implements PlaylistInfoItemExtractor {
    private final Element relatedAlbum;

    public BandcampRelatedPlaylistInfoItemExtractor(@Nonnull final Element relatedAlbum) {
        this.relatedAlbum = relatedAlbum;
    }

    @Override
    public String getName() throws ParsingException {
        return relatedAlbum.getElementsByClass("release-title").text();
    }

    @Override
    public String getUrl() throws ParsingException {
        return relatedAlbum.getElementsByClass("title-and-artist").attr("abs:href");
    }

    @Override
    public String getThumbnailUrl() throws ParsingException {
        return relatedAlbum.getElementsByClass("album-art").attr("src");
    }

    @Override
    public String getUploaderName() throws ParsingException {
        return relatedAlbum.getElementsByClass("by-artist").text().replace("by ", "");
    }

    @Override
    public long getStreamCount() throws ParsingException {
        return -1;
    }
}

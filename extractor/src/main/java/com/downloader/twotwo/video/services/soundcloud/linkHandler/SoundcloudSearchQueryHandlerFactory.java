package com.downloader.twotwo.video.services.soundcloud.linkHandler;

import com.downloader.twotwo.video.linkhandler.SearchQueryHandlerFactory;
import com.downloader.twotwo.video.utils.Utils;
import com.downloader.twotwo.video.exceptions.ExtractionException;
import com.downloader.twotwo.video.exceptions.ParsingException;
import com.downloader.twotwo.video.exceptions.ReCaptchaException;
import com.downloader.twotwo.video.services.soundcloud.SoundcloudParsingHelper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import static com.downloader.twotwo.video.services.soundcloud.SoundcloudParsingHelper.SOUNDCLOUD_API_V2_URL;

public class SoundcloudSearchQueryHandlerFactory extends SearchQueryHandlerFactory {

    public static final String TRACKS = "tracks";
    public static final String USERS = "users";
    public static final String PLAYLISTS = "playlists";
    public static final String ALL = "all";

    public static final int ITEMS_PER_PAGE = 10;

    @Override
    public String getUrl(final String id,
                         final List<String> contentFilter,
                         final String sortFilter)
            throws ParsingException {
        try {
            String url = SOUNDCLOUD_API_V2_URL + "search";

            if (!contentFilter.isEmpty()) {
                switch (contentFilter.get(0)) {
                    case TRACKS:
                        url += "/tracks";
                        break;
                    case USERS:
                        url += "/users";
                        break;
                    case PLAYLISTS:
                        url += "/playlists";
                        break;
                    case ALL:
                    default:
                        break;
                }
            }

            return url + "?q=" + URLEncoder.encode(id, Utils.UTF_8) + "&client_id="
                    + SoundcloudParsingHelper.clientId() + "&limit=" + ITEMS_PER_PAGE
                    + "&offset=0";

        } catch (final UnsupportedEncodingException e) {
            throw new ParsingException("Could not encode query", e);
        } catch (final ReCaptchaException e) {
            throw new ParsingException("ReCaptcha required", e);
        } catch (final IOException | ExtractionException e) {
            throw new ParsingException("Could not get client id", e);
        }
    }

    @Override
    public String[] getAvailableContentFilter() {
        return new String[]{
                ALL,
                TRACKS,
                USERS,
                PLAYLISTS};
    }
}
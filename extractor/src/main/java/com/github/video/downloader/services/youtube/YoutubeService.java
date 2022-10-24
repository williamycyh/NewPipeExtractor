package com.github.video.downloader.services.youtube;

import static com.github.video.downloader.StreamingService.ServiceInfo.MediaCapability.AUDIO;
import static com.github.video.downloader.StreamingService.ServiceInfo.MediaCapability.COMMENTS;
import static com.github.video.downloader.StreamingService.ServiceInfo.MediaCapability.LIVE;
import static com.github.video.downloader.StreamingService.ServiceInfo.MediaCapability.VIDEO;
import static java.util.Arrays.asList;

import com.github.video.downloader.StreamingService;
import com.github.video.downloader.channel.ChannelExtractor;
import com.github.video.downloader.comments.CommentsExtractor;
import com.github.video.downloader.exceptions.ExtractionException;
import com.github.video.downloader.feed.FeedExtractor;
import com.github.video.downloader.kis.KioskList;
import com.github.video.downloader.linkhandler.LinkHandler;
import com.github.video.downloader.linkhandler.LinkHandlerFactory;
import com.github.video.downloader.linkhandler.ListLinkHandler;
import com.github.video.downloader.linkhandler.ListLinkHandlerFactory;
import com.github.video.downloader.linkhandler.SearchQueryHandler;
import com.github.video.downloader.linkhandler.SearchQueryHandlerFactory;
import com.github.video.downloader.localization.ContentCountry;
import com.github.video.downloader.localization.Localization;
import com.github.video.downloader.playlist.PlaylistExtractor;
import com.github.video.downloader.search.SearchExtractor;
import com.github.video.downloader.services.youtube.extractors.YoutubeChannelExtractor;
import com.github.video.downloader.services.youtube.extractors.YoutubeCommentsExtractor;
import com.github.video.downloader.services.youtube.extractors.YoutubeFeedExtractor;
import com.github.video.downloader.services.youtube.extractors.YoutubeMixPlaylistExtractor;
import com.github.video.downloader.services.youtube.extractors.YoutubeMusicSearchExtractor;
import com.github.video.downloader.services.youtube.extractors.YoutubePlaylistExtractor;
import com.github.video.downloader.services.youtube.extractors.YoutubeSearchExtractor;
import com.github.video.downloader.services.youtube.extractors.YoutubeStreamExtractor;
import com.github.video.downloader.services.youtube.extractors.YoutubeSubscriptionExtractor;
import com.github.video.downloader.services.youtube.extractors.YoutubeSuggestionExtractor;
import com.github.video.downloader.services.youtube.extractors.YoutubeTrendingExtractor;
import com.github.video.downloader.services.youtube.linkHandler.YoutubeChannelLinkHandlerFactory;
import com.github.video.downloader.services.youtube.linkHandler.YoutubeCommentsLinkHandlerFactory;
import com.github.video.downloader.services.youtube.linkHandler.YoutubePlaylistLinkHandlerFactory;
import com.github.video.downloader.services.youtube.linkHandler.YoutubeSearchQueryHandlerFactory;
import com.github.video.downloader.services.youtube.linkHandler.YoutubeStreamLinkHandlerFactory;
import com.github.video.downloader.services.youtube.linkHandler.YoutubeTrendingLinkHandlerFactory;
import com.github.video.downloader.stream.StreamExtractor;
import com.github.video.downloader.subscription.SubscriptionExtractor;
import com.github.video.downloader.suggestion.SuggestionExtractor;

import java.util.List;

import javax.annotation.Nonnull;

/*
 * Created by Christian Schabesberger on 23.08.15.
 *
 * Copyright (C) Christian Schabesberger 2018 <chris.schabesberger@mailbox.org>
 * YoutubeService.java is part of NewPipe.
 *
 * NewPipe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NewPipe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NewPipe.  If not, see <http://www.gnu.org/licenses/>.
 */

public class YoutubeService extends StreamingService {

    public YoutubeService(final int id) {
        super(id, "YouTube", asList(AUDIO, VIDEO, LIVE, COMMENTS));
    }

    @Override
    public String getBaseUrl() {
        return "https://youtube.com";
    }

    @Override
    public LinkHandlerFactory getStreamLHFactory() {
        return YoutubeStreamLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getChannelLHFactory() {
        return YoutubeChannelLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getPlaylistLHFactory() {
        return YoutubePlaylistLinkHandlerFactory.getInstance();
    }

    @Override
    public SearchQueryHandlerFactory getSearchQHFactory() {
        return YoutubeSearchQueryHandlerFactory.getInstance();
    }

    @Override
    public StreamExtractor getStreamExtractor(final LinkHandler linkHandler) {
        return new YoutubeStreamExtractor(this, linkHandler);
    }

    @Override
    public ChannelExtractor getChannelExtractor(final ListLinkHandler linkHandler) {
        return new YoutubeChannelExtractor(this, linkHandler);
    }

    @Override
    public PlaylistExtractor getPlaylistExtractor(final ListLinkHandler linkHandler) {
        if (YoutubeParsingHelper.isYoutubeMixId(linkHandler.getId())
                && !YoutubeParsingHelper.isYoutubeMusicMixId(linkHandler.getId())) {
            return new YoutubeMixPlaylistExtractor(this, linkHandler);
        } else {
            return new YoutubePlaylistExtractor(this, linkHandler);
        }
    }

    @Override
    public SearchExtractor getSearchExtractor(final SearchQueryHandler query) {
        final List<String> contentFilters = query.getContentFilters();

        if (!contentFilters.isEmpty() && contentFilters.get(0).startsWith("music_")) {
            return new YoutubeMusicSearchExtractor(this, query);
        } else {
            return new YoutubeSearchExtractor(this, query);
        }
    }

    @Override
    public SuggestionExtractor getSuggestionExtractor() {
        return new YoutubeSuggestionExtractor(this);
    }

    @Override
    public KioskList getKioskList() throws ExtractionException {
        final KioskList list = new KioskList(this);

        // add kiosks here e.g.:
        try {
            list.addKioskEntry(
                    (streamingService, url, id) -> new YoutubeTrendingExtractor(
                            YoutubeService.this,
                            new YoutubeTrendingLinkHandlerFactory().fromUrl(url),
                            id
                    ),
                    new YoutubeTrendingLinkHandlerFactory(),
                    "Trending"
            );
            list.setDefaultKiosk("Trending");
        } catch (final Exception e) {
            throw new ExtractionException(e);
        }

        return list;
    }

    @Override
    public SubscriptionExtractor getSubscriptionExtractor() {
        return new YoutubeSubscriptionExtractor(this);
    }

    @Nonnull
    @Override
    public FeedExtractor getFeedExtractor(final String channelUrl) throws ExtractionException {
        return new YoutubeFeedExtractor(this, getChannelLHFactory().fromUrl(channelUrl));
    }

    @Override
    public ListLinkHandlerFactory getCommentsLHFactory() {
        return YoutubeCommentsLinkHandlerFactory.getInstance();
    }

    @Override
    public CommentsExtractor getCommentsExtractor(final ListLinkHandler urlIdHandler)
            throws ExtractionException {
        return new YoutubeCommentsExtractor(this, urlIdHandler);
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Localization
    //////////////////////////////////////////////////////////////////////////*/

    // https://www.youtube.com/picker_ajax?action_language_json=1
    private static final List<Localization> SUPPORTED_LANGUAGES = Localization.listFrom(
            "en-GB"
            /*"af", "am", "ar", "az", "be", "bg", "bn", "bs", "ca", "cs", "da", "de",
            "el", "en", "en-GB", "es", "es-419", "es-US", "et", "eu", "fa", "fi", "fil", "fr",
            "fr-CA", "gl", "gu", "hi", "hr", "hu", "hy", "id", "is", "it", "iw", "ja",
            "ka", "kk", "km", "kn", "ko", "ky", "lo", "lt", "lv", "mk", "ml", "mn",
            "mr", "ms", "my", "ne", "nl", "no", "pa", "pl", "pt", "pt-PT", "ro", "ru",
            "si", "sk", "sl", "sq", "sr", "sr-Latn", "sv", "sw", "ta", "te", "th", "tr",
            "uk", "ur", "uz", "vi", "zh-CN", "zh-HK", "zh-TW", "zu"*/
    );

    // https://www.youtube.com/picker_ajax?action_country_json=1
    private static final List<ContentCountry> SUPPORTED_COUNTRIES = ContentCountry.listFrom(
            "DZ", "AR", "AU", "AT", "AZ", "BH", "BD", "BY", "BE", "BO", "BA", "BR", "BG", "CA",
            "CL", "CO", "CR", "HR", "CY", "CZ", "DK", "DO", "EC", "EG", "SV", "EE", "FI", "FR",
            "GE", "DE", "GH", "GR", "GT", "HN", "HK", "HU", "IS", "IN", "ID", "IQ", "IE", "IL",
            "IT", "JM", "JP", "JO", "KZ", "KE", "KW", "LV", "LB", "LY", "LI", "LT", "LU", "MY",
            "MT", "MX", "ME", "MA", "NP", "NL", "NZ", "NI", "NG", "MK", "NO", "OM", "PK", "PA",
            "PG", "PY", "PE", "PH", "PL", "PT", "PR", "QA", "RO", "RU", "SA", "SN", "RS", "SG",
            "SK", "SI", "ZA", "KR", "ES", "LK", "SE", "CH", "TW", "TZ", "TH", "TN", "TR", "UG",
            "UA", "AE", "GB", "US", "UY", "VE", "VN", "YE", "ZW"
    );

    @Override
    public List<Localization> getSupportedLocalizations() {
        return SUPPORTED_LANGUAGES;
    }

    @Override
    public List<ContentCountry> getSupportedCountries() {
        return SUPPORTED_COUNTRIES;
    }
}

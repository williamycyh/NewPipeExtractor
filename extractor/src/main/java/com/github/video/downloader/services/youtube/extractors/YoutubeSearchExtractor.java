package com.github.video.downloader.services.youtube.extractors;

import static com.github.video.downloader.services.youtube.YoutubeParsingHelper.getTextFromObject;
import static com.github.video.downloader.utils.Utils.UTF_8;
import static com.github.video.downloader.utils.Utils.isNullOrEmpty;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonBuilder;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import com.grack.nanojson.JsonWriter;
import com.github.video.downloader.services.youtube.YoutubeParsingHelper;
import com.github.video.downloader.services.youtube.linkHandler.YoutubeSearchQueryHandlerFactory;

import com.github.video.downloader.InfoItem;
import com.github.video.downloader.MetaInfo;
import com.github.video.downloader.Page;
import com.github.video.downloader.StreamingService;
import com.github.video.downloader.downloader.Downloader;
import com.github.video.downloader.exceptions.ExtractionException;
import com.github.video.downloader.exceptions.ParsingException;
import com.github.video.downloader.linkhandler.SearchQueryHandler;
import com.github.video.downloader.localization.Localization;
import com.github.video.downloader.localization.TimeAgoParser;
import com.github.video.downloader.MultiInfoItemsCollector;
import com.github.video.downloader.search.SearchExtractor;
import com.github.video.downloader.utils.JsonUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nonnull;

/*
 * Created by Christian Schabesberger on 22.07.2018
 *
 * Copyright (C) Christian Schabesberger 2018 <chris.schabesberger@mailbox.org>
 * YoutubeSearchExtractor.java is part of NewPipe.
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

public class YoutubeSearchExtractor extends SearchExtractor {
    private JsonObject initialData;

    public YoutubeSearchExtractor(final StreamingService service,
                                  final SearchQueryHandler linkHandler) {
        super(service, linkHandler);
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader) throws IOException,
            ExtractionException {
        final String query = super.getSearchString();
        final Localization localization = getExtractorLocalization();

        // Get the search parameter of the request
        final List<String> contentFilters = super.getLinkHandler().getContentFilters();
        final String params;
        if (!isNullOrEmpty(contentFilters)) {
            final String searchType = contentFilters.get(0);
            params = YoutubeSearchQueryHandlerFactory.getSearchParameter(searchType);
        } else {
            params = "";
        }

        final JsonBuilder<JsonObject> jsonBody = YoutubeParsingHelper.prepareDesktopJsonBuilder(localization,
                getExtractorContentCountry())
                .value("query", query);
        if (!isNullOrEmpty(params)) {
            jsonBody.value("params", params);
        }

        final byte[] body = JsonWriter.string(jsonBody.done()).getBytes(UTF_8);

        initialData = YoutubeParsingHelper.getJsonPostResponse("search", body, localization);
    }

    @Nonnull
    @Override
    public String getUrl() throws ParsingException {
        return super.getUrl() + "&gl=" + getExtractorContentCountry().getCountryCode();
    }

    @Nonnull
    @Override
    public String getSearchSuggestion() throws ParsingException {
        final JsonObject itemSectionRenderer = initialData.getObject("contents")
                .getObject("twoColumnSearchResultsRenderer").getObject("primaryContents")
                .getObject("sectionListRenderer").getArray("contents").getObject(0)
                .getObject("itemSectionRenderer");
        final JsonObject didYouMeanRenderer = itemSectionRenderer.getArray("contents").getObject(0)
                .getObject("didYouMeanRenderer");
        final JsonObject showingResultsForRenderer = itemSectionRenderer.getArray("contents")
                .getObject(0)
                .getObject("showingResultsForRenderer");

        if (!didYouMeanRenderer.isEmpty()) {
            return JsonUtils.getString(didYouMeanRenderer,
                    "correctedQueryEndpoint.searchEndpoint.query");
        } else if (showingResultsForRenderer != null) {
            return YoutubeParsingHelper.getTextFromObject(showingResultsForRenderer.getObject("correctedQuery"));
        } else {
            return "";
        }
    }

    @Override
    public boolean isCorrectedSearch() {
        final JsonObject showingResultsForRenderer = initialData.getObject("contents")
                .getObject("twoColumnSearchResultsRenderer").getObject("primaryContents")
                .getObject("sectionListRenderer").getArray("contents").getObject(0)
                .getObject("itemSectionRenderer").getArray("contents").getObject(0)
                .getObject("showingResultsForRenderer");
        return !showingResultsForRenderer.isEmpty();
    }

    @Override
    public List<MetaInfo> getMetaInfo() throws ParsingException {
        return YoutubeParsingHelper.getMetaInfo(
                initialData.getObject("contents").getObject("twoColumnSearchResultsRenderer")
                        .getObject("primaryContents").getObject("sectionListRenderer")
                        .getArray("contents"));
    }

    @Nonnull
    @Override
    public InfoItemsPage<InfoItem> getInitialPage() throws IOException, ExtractionException {
        final MultiInfoItemsCollector collector = new MultiInfoItemsCollector(getServiceId());

        final JsonArray sections = initialData.getObject("contents")
                .getObject("twoColumnSearchResultsRenderer").getObject("primaryContents")
                .getObject("sectionListRenderer").getArray("contents");

        Page nextPage = null;

        for (final Object section : sections) {
            if (((JsonObject) section).has("itemSectionRenderer")) {
                final JsonObject itemSectionRenderer = ((JsonObject) section)
                        .getObject("itemSectionRenderer");

                collectStreamsFrom(collector, itemSectionRenderer.getArray("contents"));
            } else if (((JsonObject) section).has("continuationItemRenderer")) {
                nextPage = getNextPageFrom(((JsonObject) section)
                        .getObject("continuationItemRenderer"));
            }
        }

        return new InfoItemsPage<>(collector, nextPage);
    }

    @Override
    public InfoItemsPage<InfoItem> getPage(final Page page) throws IOException,
            ExtractionException {
        if (page == null || isNullOrEmpty(page.getUrl())) {
            throw new IllegalArgumentException("Page doesn't contain an URL");
        }

        final Localization localization = getExtractorLocalization();
        final MultiInfoItemsCollector collector = new MultiInfoItemsCollector(getServiceId());

        // @formatter:off
        final byte[] json = JsonWriter.string(YoutubeParsingHelper.prepareDesktopJsonBuilder(localization,
                getExtractorContentCountry())
                .value("continuation", page.getId())
                .done())
                .getBytes(UTF_8);
        // @formatter:on

        final String responseBody = YoutubeParsingHelper.getValidJsonResponseBody(getDownloader().post(
                page.getUrl(), new HashMap<>(), json));

        final JsonObject ajaxJson;
        try {
            ajaxJson = JsonParser.object().from(responseBody);
        } catch (final JsonParserException e) {
            throw new ParsingException("Could not parse JSON", e);
        }

        final JsonArray continuationItems = ajaxJson.getArray("onResponseReceivedCommands")
                .getObject(0).getObject("appendContinuationItemsAction")
                .getArray("continuationItems");

        final JsonArray contents = continuationItems.getObject(0)
                .getObject("itemSectionRenderer").getArray("contents");
        collectStreamsFrom(collector, contents);

        return new InfoItemsPage<>(collector, getNextPageFrom(continuationItems.getObject(1)
                .getObject("continuationItemRenderer")));
    }

    private void collectStreamsFrom(final MultiInfoItemsCollector collector,
                                    final JsonArray contents) throws NothingFoundException,
            ParsingException {
        final TimeAgoParser timeAgoParser = getTimeAgoParser();

        for (final Object content : contents) {
            final JsonObject item = (JsonObject) content;
            if (item.has("backgroundPromoRenderer")) {
                throw new NothingFoundException(YoutubeParsingHelper.getTextFromObject(
                        item.getObject("backgroundPromoRenderer").getObject("bodyText")));
            } else if (item.has("videoRenderer")) {
                collector.commit(new YoutubeStreamInfoItemExtractor(item
                        .getObject("videoRenderer"), timeAgoParser));
            } else if (item.has("channelRenderer")) {
                collector.commit(new YoutubeChannelInfoItemExtractor(item
                        .getObject("channelRenderer")));
            } else if (item.has("playlistRenderer")) {
                collector.commit(new YoutubePlaylistInfoItemExtractor(item
                        .getObject("playlistRenderer")));
            }
        }
    }

    private Page getNextPageFrom(final JsonObject continuationItemRenderer) throws IOException,
            ExtractionException {
        if (isNullOrEmpty(continuationItemRenderer)) {
            return null;
        }

        final String token = continuationItemRenderer.getObject("continuationEndpoint")
                .getObject("continuationCommand").getString("token");

        final String url = YoutubeParsingHelper.YOUTUBEI_V1_URL + "search?key=" + YoutubeParsingHelper.getKey()
                + YoutubeParsingHelper.DISABLE_PRETTY_PRINT_PARAMETER;

        return new Page(url, token);
    }
}

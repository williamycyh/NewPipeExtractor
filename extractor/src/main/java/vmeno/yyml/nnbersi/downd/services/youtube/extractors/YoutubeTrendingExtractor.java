package vmeno.yyml.nnbersi.downd.services.youtube.extractors;

/*
 * Created by Christian Schabesberger on 12.08.17.
 *
 * Copyright (C) Christian Schabesberger 2018 <chris.schabesberger@mailbox.org>
 * YoutubeTrendingExtractor.java is part of NewPipe.
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

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonWriter;
import vmeno.yyml.nnbersi.downd.services.youtube.YoutubeParsingHelper;

import vmeno.yyml.nnbersi.downd.Page;
import vmeno.yyml.nnbersi.downd.StreamingService;
import vmeno.yyml.nnbersi.downd.downloader.Downloader;
import vmeno.yyml.nnbersi.downd.exceptions.ExtractionException;
import vmeno.yyml.nnbersi.downd.exceptions.ParsingException;
import vmeno.yyml.nnbersi.downd.kis.KioskExtractor;
import vmeno.yyml.nnbersi.downd.linkhandler.ListLinkHandler;
import vmeno.yyml.nnbersi.downd.localization.TimeAgoParser;
import vmeno.yyml.nnbersi.downd.stream.StreamInfoItem;
import vmeno.yyml.nnbersi.downd.stream.StreamInfoItemsCollector;

import java.io.IOException;

import javax.annotation.Nonnull;

import static vmeno.yyml.nnbersi.downd.utils.Utils.UTF_8;
import static vmeno.yyml.nnbersi.downd.utils.Utils.isNullOrEmpty;

public class YoutubeTrendingExtractor extends KioskExtractor<StreamInfoItem> {
    private JsonObject initialData;

    public YoutubeTrendingExtractor(final StreamingService service,
                                    final ListLinkHandler linkHandler,
                                    final String kioskId) {
        super(service, linkHandler, kioskId);
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader)
            throws IOException, ExtractionException {
        // @formatter:off
        final byte[] body = JsonWriter.string(YoutubeParsingHelper.prepareDesktopJsonBuilder(getExtractorLocalization(),
                getExtractorContentCountry())
                .value("browseId", "FEtrending")
                .done())
                .getBytes(UTF_8);
        // @formatter:on

        initialData = YoutubeParsingHelper.getJsonPostResponse("browse", body, getExtractorLocalization());
    }

    @Override
    public InfoItemsPage<StreamInfoItem> getPage(final Page page) {
        return InfoItemsPage.emptyPage();
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        final JsonObject header = initialData.getObject("header");
        String name = null;
        if (header.has("feedTabbedHeaderRenderer")) {
            name = YoutubeParsingHelper.getTextAtKey(header.getObject("feedTabbedHeaderRenderer"), "title");
        } else if (header.has("c4TabbedHeaderRenderer")) {
            name = YoutubeParsingHelper.getTextAtKey(header.getObject("c4TabbedHeaderRenderer"), "title");
        }

        if (isNullOrEmpty(name)) {
            throw new ParsingException("Could not get Trending name");
        }
        return name;
    }

    @Nonnull
    @Override
    public InfoItemsPage<StreamInfoItem> getInitialPage() {
        final StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
        final TimeAgoParser timeAgoParser = getTimeAgoParser();
        final JsonArray itemSectionRenderers = initialData.getObject("contents")
                .getObject("twoColumnBrowseResultsRenderer").getArray("tabs").getObject(0)
                .getObject("tabRenderer").getObject("content").getObject("sectionListRenderer")
                .getArray("contents");

        for (final Object itemSectionRenderer : itemSectionRenderers) {
            final JsonObject expandedShelfContentsRenderer = ((JsonObject) itemSectionRenderer)
                    .getObject("itemSectionRenderer").getArray("contents").getObject(0)
                    .getObject("shelfRenderer").getObject("content")
                    .getObject("expandedShelfContentsRenderer");
            for (final Object ul : expandedShelfContentsRenderer.getArray("items")) {
                final JsonObject videoInfo = ((JsonObject) ul).getObject("videoRenderer");
                collector.commit(new YoutubeStreamInfoItemExtractor(videoInfo, timeAgoParser));
            }
        }

        return new InfoItemsPage<>(collector, null);
    }
}

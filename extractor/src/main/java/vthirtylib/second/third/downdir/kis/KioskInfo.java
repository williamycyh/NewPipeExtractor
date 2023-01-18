package vthirtylib.second.third.downdir.kis;

/*
 * Created by Christian Schabesberger on 12.08.17.
 *
 * Copyright (C) Christian Schabesberger 2017 <chris.schabesberger@mailbox.org>
 * KioskInfo.java is part of NewPipe.
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

import vthirtylib.second.third.downdir.ListInfo;
import vthirtylib.second.third.downdir.NewPipe;
import vthirtylib.second.third.downdir.StreamingService;
import vthirtylib.second.third.downdir.stream.StreamInfoItem;
import vthirtylib.second.third.downdir.ListExtractor;
import vthirtylib.second.third.downdir.Page;
import vthirtylib.second.third.downdir.exceptions.ExtractionException;
import vthirtylib.second.third.downdir.linkhandler.ListLinkHandler;
import vthirtylib.second.third.downdir.utils.ExtractorHelper;

import java.io.IOException;

public final class KioskInfo extends ListInfo<StreamInfoItem> {
    private KioskInfo(final int serviceId, final ListLinkHandler linkHandler, final String name) {
        super(serviceId, linkHandler, name);
    }

    public static ListExtractor.InfoItemsPage<StreamInfoItem> getMoreItems(
            final StreamingService service, final String url, final Page page)
            throws IOException, ExtractionException {
        return service.getKioskList().getExtractorByUrl(url, page).getPage(page);
    }

    public static KioskInfo getInfo(final String url) throws IOException, ExtractionException {
        return getInfo(NewPipe.getServiceByUrl(url), url);
    }

    public static KioskInfo getInfo(final StreamingService service, final String url)
            throws IOException, ExtractionException {
        final KioskExtractor extractor = service.getKioskList().getExtractorByUrl(url, null);
        extractor.fetchPage();
        return getInfo(extractor);
    }

    /**
     * Get KioskInfo from KioskExtractor
     *
     * @param extractor an extractor where fetchPage() was already got called on.
     */
    public static KioskInfo getInfo(final KioskExtractor extractor) throws ExtractionException {

        final KioskInfo info = new KioskInfo(extractor.getServiceId(),
                extractor.getLinkHandler(),
                extractor.getName());

        final ListExtractor.InfoItemsPage<StreamInfoItem> itemsPage
                = ExtractorHelper.getItemsPageOrLogError(info, extractor);
        info.setRelatedItems(itemsPage.getItems());
        info.setNextPage(itemsPage.getNextPage());

        return info;
    }
}
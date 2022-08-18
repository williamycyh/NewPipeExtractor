package com.downloader.twotwo.video.services.peertube.extractors;

import com.grack.nanojson.JsonObject;
import com.downloader.twotwo.video.ServiceList;
import com.downloader.twotwo.video.comments.CommentsInfoItemExtractor;
import com.downloader.twotwo.video.exceptions.ParsingException;
import com.downloader.twotwo.video.localization.DateWrapper;
import com.downloader.twotwo.video.services.peertube.PeertubeParsingHelper;
import com.downloader.twotwo.video.utils.JsonUtils;
import com.downloader.twotwo.video.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Objects;

public class PeertubeCommentsInfoItemExtractor implements CommentsInfoItemExtractor {
    private final JsonObject item;
    private final String url;
    private final String baseUrl;

    public PeertubeCommentsInfoItemExtractor(final JsonObject item,
                                             final PeertubeCommentsExtractor extractor)
            throws ParsingException {
        this.item = item;
        this.url = extractor.getUrl();
        this.baseUrl = extractor.getBaseUrl();
    }

    @Override
    public String getUrl() throws ParsingException {
        return url;
    }

    @Override
    public String getThumbnailUrl() {
        String value;
        try {
            value = JsonUtils.getString(item, "account.avatar.path");
        } catch (final Exception e) {
            value = "/client/assets/images/default-avatar.png";
        }
        return baseUrl + value;
    }

    @Override
    public String getName() throws ParsingException {
        return JsonUtils.getString(item, "account.displayName");
    }

    @Override
    public String getTextualUploadDate() throws ParsingException {
        return JsonUtils.getString(item, "createdAt");
    }

    @Override
    public DateWrapper getUploadDate() throws ParsingException {
        final String textualUploadDate = getTextualUploadDate();
        return new DateWrapper(PeertubeParsingHelper.parseDateFrom(textualUploadDate));
    }

    @Override
    public String getCommentText() throws ParsingException {
        final String htmlText = JsonUtils.getString(item, "text");
        try {
            final Document doc = Jsoup.parse(htmlText);
            return doc.body().text();
        } catch (final Exception e) {
            return htmlText.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", Utils.EMPTY_STRING);
        }
    }

    @Override
    public String getCommentId() {
        return Objects.toString(item.getLong("id"), null);
    }

    @Override
    public String getUploaderAvatarUrl() {
        String value;
        try {
            value = JsonUtils.getString(item, "account.avatar.path");
        } catch (final Exception e) {
            value = "/client/assets/images/default-avatar.png";
        }
        return baseUrl + value;
    }

    @Override
    public String getUploaderName() throws ParsingException {
        return JsonUtils.getString(item, "account.name") + "@"
                + JsonUtils.getString(item, "account.host");
    }

    @Override
    public String getUploaderUrl() throws ParsingException {
        final String name = JsonUtils.getString(item, "account.name");
        final String host = JsonUtils.getString(item, "account.host");
        return ServiceList.PeerTube.getChannelLHFactory()
                .fromId("accounts/" + name + "@" + host, baseUrl).getUrl();
    }
}
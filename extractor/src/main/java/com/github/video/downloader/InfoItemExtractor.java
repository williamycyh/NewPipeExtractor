package com.github.video.downloader;

import com.github.video.downloader.exceptions.ParsingException;

public interface InfoItemExtractor {
    String getName() throws ParsingException;
    String getUrl() throws ParsingException;
    String getThumbnailUrl() throws ParsingException;
}

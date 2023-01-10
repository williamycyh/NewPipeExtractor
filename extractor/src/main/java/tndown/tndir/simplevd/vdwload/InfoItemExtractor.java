package tndown.tndir.simplevd.vdwload;

import tndown.tndir.simplevd.vdwload.exceptions.ParsingException;

public interface InfoItemExtractor {
    String getName() throws ParsingException;
    String getUrl() throws ParsingException;
    String getThumbnailUrl() throws ParsingException;
}

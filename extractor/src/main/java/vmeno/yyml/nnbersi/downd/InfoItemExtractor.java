package vmeno.yyml.nnbersi.downd;

import vmeno.yyml.nnbersi.downd.exceptions.ParsingException;

public interface InfoItemExtractor {
    String getName() throws ParsingException;
    String getUrl() throws ParsingException;
    String getThumbnailUrl() throws ParsingException;
}

package vmeno.yyml.nnbersi.downd.exceptions;

public class GeographicRestrictionException extends ContentNotAvailableException {
    public GeographicRestrictionException(final String message) {
        super(message);
    }

    public GeographicRestrictionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

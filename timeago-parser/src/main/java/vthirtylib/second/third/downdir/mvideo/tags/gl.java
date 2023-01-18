/**/// DO NOT MODIFY THIS FILE MANUALLY
/**/// This class was automatically generated by "GeneratePatternClasses.java",
/**/// modify the "unique_patterns.json" and re-generate instead.

package vthirtylib.second.third.downdir.mvideo.tags;

import vthirtylib.second.third.downdir.mvideo.PatternsHolder;

public class gl extends PatternsHolder {
    private static final String WORD_SEPARATOR = " ";
    private static final String[]
            SECONDS  /**/ = {"segundo", "segundos"},
            MINUTES  /**/ = {"minuto", "minutos"},
            HOURS    /**/ = {"hora", "horas"},
            DAYS     /**/ = {"día", "días"},
            WEEKS    /**/ = {"semana", "semanas"},
            MONTHS   /**/ = {"mes", "meses"},
            YEARS    /**/ = {"ano", "anos"};

    private static final gl INSTANCE = new gl();

    public static gl getInstance() {
        return INSTANCE;
    }

    private gl() {
        super(WORD_SEPARATOR, SECONDS, MINUTES, HOURS, DAYS, WEEKS, MONTHS, YEARS);
    }
}
/**/// DO NOT MODIFY THIS FILE MANUALLY
/**/// This class was automatically generated by "GeneratePatternClasses.java",
/**/// modify the "unique_patterns.json" and re-generate instead.

package vthirtylib.second.third.downdir.mvideo.tags;

import vthirtylib.second.third.downdir.mvideo.PatternsHolder;

public class sk extends PatternsHolder {
    private static final String WORD_SEPARATOR = " ";
    private static final String[]
            SECONDS  /**/ = {"sekundami", "sekundou"},
            MINUTES  /**/ = {"minútami", "minútou"},
            HOURS    /**/ = {"hodinami", "hodinou"},
            DAYS     /**/ = {"dňami", "dňom"},
            WEEKS    /**/ = {"týždňami", "týždňom"},
            MONTHS   /**/ = {"mesiacmi", "mesiacom"},
            YEARS    /**/ = {"rokmi", "rokom"};

    private static final sk INSTANCE = new sk();

    public static sk getInstance() {
        return INSTANCE;
    }

    private sk() {
        super(WORD_SEPARATOR, SECONDS, MINUTES, HOURS, DAYS, WEEKS, MONTHS, YEARS);
    }
}
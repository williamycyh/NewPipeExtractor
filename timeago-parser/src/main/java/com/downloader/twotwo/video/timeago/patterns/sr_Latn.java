/**/// DO NOT MODIFY THIS FILE MANUALLY
/**/// This class was automatically generated by "GeneratePatternClasses.java",
/**/// modify the "unique_patterns.json" and re-generate instead.

package com.downloader.twotwo.video.timeago.patterns;

import com.downloader.twotwo.video.timeago.PatternsHolder;

public class sr_Latn extends PatternsHolder {
    private static final String WORD_SEPARATOR = " ";
    private static final String[]
            SECONDS  /**/ = {"sekunde", "sekundi"},
            MINUTES  /**/ = {"minuta"},
            HOURS    /**/ = {"sat", "sati", "sata"},
            DAYS     /**/ = {"Pre 1 dan", "Pre 2 dana", "Pre 3 dana", "Pre 4 dana", "Pre 5 dana", "Pre 6 dana"},
            WEEKS    /**/ = {"nedelja", "nedelje", "nedelju"},
            MONTHS   /**/ = {"mesec", "meseci", "meseca"},
            YEARS    /**/ = {"godine", "godina", "godinu"};

    private static final sr_Latn INSTANCE = new sr_Latn();

    public static sr_Latn getInstance() {
        return INSTANCE;
    }

    private sr_Latn() {
        super(WORD_SEPARATOR, SECONDS, MINUTES, HOURS, DAYS, WEEKS, MONTHS, YEARS);
    }
}
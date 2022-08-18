/**/// DO NOT MODIFY THIS FILE MANUALLY
/**/// This class was automatically generated by "GeneratePatternClasses.java",
/**/// modify the "unique_patterns.json" and re-generate instead.

package com.downloader.twotwo.video.timeago.patterns;

import com.downloader.twotwo.video.timeago.PatternsHolder;

public class id extends PatternsHolder {
    private static final String WORD_SEPARATOR = " ";
    private static final String[]
            SECONDS  /**/ = {"detik"},
            MINUTES  /**/ = {"menit"},
            HOURS    /**/ = {"jam"},
            DAYS     /**/ = {"hari"},
            WEEKS    /**/ = {"minggu"},
            MONTHS   /**/ = {"bulan"},
            YEARS    /**/ = {"tahun"};

    private static final id INSTANCE = new id();

    public static id getInstance() {
        return INSTANCE;
    }

    private id() {
        super(WORD_SEPARATOR, SECONDS, MINUTES, HOURS, DAYS, WEEKS, MONTHS, YEARS);
    }
}
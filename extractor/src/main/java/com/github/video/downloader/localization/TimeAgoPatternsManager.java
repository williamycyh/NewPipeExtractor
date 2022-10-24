package com.github.video.downloader.localization;

import com.github.video.downloader.vdtag.PatternsHolder;
import com.github.video.downloader.vdtag.PatternsManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class TimeAgoPatternsManager {
    private TimeAgoPatternsManager() {
    }

    @Nullable
    private static PatternsHolder getPatternsFor(@Nonnull final Localization localization) {
        return PatternsManager.getPatterns(localization.getLanguageCode(),
                localization.getCountryCode());
    }

    @Nullable
    public static TimeAgoParser getTimeAgoParserFor(@Nonnull final Localization localization) {
        final PatternsHolder holder = getPatternsFor(localization);

        if (holder == null) {
            return null;
        }

        return new TimeAgoParser(holder);
    }
}

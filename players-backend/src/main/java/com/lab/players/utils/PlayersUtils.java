package com.lab.players.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

public abstract class PlayersUtils {
    private PlayersUtils() {
    }

    public static Date parseDate(String date, String format) throws ParseException {
        return StringUtils.isNotEmpty(date) ? DateUtils.parseDate(date, format) : null;
    }

    public static Optional<Integer> toOptionalInt(String value) {
        return Optional.ofNullable(value).filter(StringUtils::isNotEmpty).map(Integer::parseInt);
    }

    public static LocalDate parseStringToLocalDate(String dateString, String format) {
        if (StringUtils.isNotEmpty(dateString)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return LocalDate.parse(dateString, formatter);
        } else
            return null;
    }
}

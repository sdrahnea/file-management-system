package com.fms.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DateUtils class contains common methods which are used to reduce code duplication
 */
public class DateUtils {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String YEAR_DATE_FORMAT = "yyyy";
    private static final String MONTH_DATE_FORMAT = "MM";
    private static final String DAY_DATE_FORMAT = "dd";

    /**
     * @return current date into DATE_FORMAT value
     */
    public static String getCurrentDateAsString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        return simpleDateFormat.format(new Date());
    }

    public static String formatDate(final Date date){
        return new SimpleDateFormat(DATE_FORMAT).format(getCurrentDateIfNull(date));
    }

    public static String getYear(final Date date) {
        return new SimpleDateFormat(YEAR_DATE_FORMAT).format(getCurrentDateIfNull(date));
    }

    public static String getMonth(final Date date) {
        return new SimpleDateFormat(MONTH_DATE_FORMAT).format(getCurrentDateIfNull(date));
    }

    public static String getDay(final Date date) {
        return new SimpleDateFormat(DAY_DATE_FORMAT).format(getCurrentDateIfNull(date));
    }

    private static Date getCurrentDateIfNull(final Date date) {
        return date == null ? new Date() : date;
    }

}

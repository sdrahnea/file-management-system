package com.fms.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String YEAR_DATE_FORMAT = "yyyy";

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

    private static Date getCurrentDateIfNull(final Date date) {
        return date == null ? new Date() : date;
    }

}

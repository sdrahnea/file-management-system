package com.fms.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * @return current date into DATE_FORMAT value
     */
    public static String getCurrentDateAsString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        return simpleDateFormat.format(new Date());
    }

}

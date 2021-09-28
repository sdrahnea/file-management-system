package com.fms.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

public class DateUtilsTest {

    private static final int YEAR = 2021;
    private static final int MONTH = 8;
    private static final int DAY = 11;

    static Calendar calendar;

    DateUtils dateUtils;

    @BeforeAll
    static void init(){
        calendar = Calendar.getInstance();
        calendar.set(YEAR, MONTH, DAY);
    }

    @Test
    public void should_return_correct_day(){
       Assertions.assertEquals("" + DAY, dateUtils.getDay(calendar.getTime()));
    }

    @Test
    public void should_return_correct_month(){
        Assertions.assertEquals("09", dateUtils.getMonth(calendar.getTime()));
    }

    @Test
    public void should_return_correct_year(){
        Assertions.assertEquals("" + YEAR, dateUtils.getYear(calendar.getTime()));
    }

}

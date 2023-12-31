package com.railway.railwayAPI.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Utils {
    public static String getNextDayDate (String inputDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate currentDate = LocalDate.parse(inputDate, formatter);
        LocalDate nextDate = currentDate.plusDays(1);
        return nextDate.format(formatter);
    }

    public static String addDate(String inputDate, int i) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate currentDate = LocalDate.parse(inputDate, formatter);
        LocalDate nextDate = currentDate.plusDays(i);
        return nextDate.format(formatter);
    }
}

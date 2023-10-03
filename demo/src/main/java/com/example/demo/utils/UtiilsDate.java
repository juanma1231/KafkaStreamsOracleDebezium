package com.example.demo.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class UtiilsDate {

    public static Long changeToEpoch(String strDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date  = LocalDate.parse(strDate,formatter);
        Instant instant = date.atStartOfDay(ZoneId.of("UTC")).toInstant();
        Long epch = instant.getEpochSecond();
        epch = epch/ (24 * 60 * 60);
        return  epch;
    }
    public static String changeDateToString(LocalDate date){
        return "";
    }
}

package com.handballleague.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashSet;
import java.util.Set;

public abstract class DateManager {
    public static boolean isDateValid(LocalDateTime date) {
        LocalDate localDate = date.toLocalDate();
        Set<LocalDate> holidays = getPolishBankHolidays(localDate.getYear());
        return !holidays.contains(localDate);
    }

    private static Set<LocalDate> getPolishBankHolidays(int year) {
        Set<LocalDate> holidays = new HashSet<>();
        // Fixed-date holidays
        holidays.add(LocalDate.of(year, Month.JANUARY, 1));
        holidays.add(LocalDate.of(year, Month.JANUARY, 2));
        holidays.add(LocalDate.of(year, Month.JANUARY, 6));
        holidays.add(LocalDate.of(year, Month.MAY, 1));
        holidays.add(LocalDate.of(year, Month.MAY, 2));
        holidays.add(LocalDate.of(year, Month.MAY, 3));
        holidays.add(LocalDate.of(year, Month.AUGUST, 15));
        holidays.add(LocalDate.of(year, Month.NOVEMBER, 1));
        holidays.add(LocalDate.of(year, Month.NOVEMBER, 11));
        holidays.add(LocalDate.of(year, Month.DECEMBER, 25));
        holidays.add(LocalDate.of(year, Month.DECEMBER, 26));

        // Moveable holidays
        LocalDate easterSunday = calculateEasterSunday(year);
        holidays.add(easterSunday); // Easter Sunday
        holidays.add(easterSunday.plusDays(1));
        holidays.add(easterSunday.plusDays(49));
        holidays.add(easterSunday.plusDays(60));

        return holidays;
    }

    private static LocalDate calculateEasterSunday(int year) {
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int n = h + l - 7 * m + 114;
        int month = n / 31;
        int day = (n % 31) + 1;
        return LocalDate.of(year, month, day);
    }
}

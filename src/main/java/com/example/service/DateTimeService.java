package com.example.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.util.Set;

public class DateTimeService {

    private static final Set<LocalDate> HOLIDAYS = Set.of(
            LocalDate.of(LocalDate.now().getYear(), 1, 1),
            LocalDate.of(LocalDate.now().getYear(), 7, 4),
            LocalDate.of(LocalDate.now().getYear(), 12, 25)
    );

    public boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    public boolean isBusinessDay(LocalDate date) {
        return !isWeekend(date) && !HOLIDAYS.contains(date);
    }

    public LocalDate addBusinessDays(LocalDate date, int days) {
        LocalDate result = date;
        int added = 0;
        while (added < days) {
            result = result.plusDays(1);
            if (isBusinessDay(result)) added++;
        }
        return result;
    }

    public int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public String formatDuration(long seconds) {
        if (seconds < 60) return seconds + " seconds";
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        if (minutes < 60) {
            return minutes + " minutes " + remainingSeconds + " seconds";
        }
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;
        if (hours < 24) {
            return hours + " hours " + remainingMinutes + " minutes";
        }
        long days = hours / 24;
        long remainingHours = hours % 24;
        return days + " days " + remainingHours + " hours";
    }
}

package edu.ucsd.cse110.successorator.lib.domain;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class SuccessDate {
    static public LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    static public LocalDate stringToDate(String s) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("MMM-d-yyyy", Locale.US);
        return LocalDate.parse(s, f);
    }

    static public String dateToString(LocalDate d) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("MMM-d-yyyy", Locale.US);
        return d.format(f);
    }

}

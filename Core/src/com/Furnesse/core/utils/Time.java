package com.Furnesse.core.utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;

public class Time {

	public static String convertSecondsToCooldown(int seconds) {
		String format = "";

		if (seconds >= 86400) {
			int day = seconds / 86400;
			seconds -= day * 86400;
			format += day + "d ";
		}

		if (seconds >= 3600) {
			int hour = seconds / 3600;
			seconds -= hour * 3600;
			format += hour + "h ";
		}

		if (seconds >= 60) {
			int minute = seconds / 60;
			seconds -= minute * 60;
			format += minute + "m ";
		}

		if (seconds <= 59) {
			format += seconds + "s";
		}

		return format;
	}

	public static long convertCooldownToSeconds(String str) {

		DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern(
                "[d'd'][ ][h'h'][ ][m'm'][ ][s's']").appendPattern("[d'd'][ ][hh'h'][ ][mm'm'][ ][ss's']").toFormatter();

        TemporalAccessor temporalAccessor = formatter.parse(str);

        LocalDateTime time = LocalDateTime.now();
        LocalDateTime t2 = time
                .minus(ChronoField.DAY_OF_MONTH.isSupportedBy(temporalAccessor) ? ChronoField.DAY_OF_MONTH.getFrom(temporalAccessor) : 0, ChronoUnit.DAYS)
                .minus(ChronoField.HOUR_OF_AMPM.isSupportedBy(temporalAccessor) ? ChronoField.HOUR_OF_AMPM.getFrom(temporalAccessor) : 0, ChronoUnit.HOURS)
                .minus(ChronoField.MINUTE_OF_HOUR.isSupportedBy(temporalAccessor) ? ChronoField.MINUTE_OF_HOUR.getFrom(temporalAccessor) : 0, ChronoUnit.MINUTES)
                .minus(ChronoField.SECOND_OF_MINUTE.isSupportedBy(temporalAccessor) ? ChronoField.SECOND_OF_MINUTE.getFrom(temporalAccessor) : 0, ChronoUnit.SECONDS);


        long coolDownTimeSeconds = time.toEpochSecond(ZoneOffset.UTC) - t2.toEpochSecond(ZoneOffset.UTC);

//        System.out.println("Seconds : " + coolDownTimeSeconds);

        return coolDownTimeSeconds;
	}
}

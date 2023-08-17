package com.example.trackr.shared.utils;

import android.content.res.Resources;


import com.example.trackr.shared.R;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Rameel Hassan
 * Created 01/03/2023 at 4:27 PM
 */
public class DateTimeUtils {
    static DateTimeFormatter DATE_TIME_FORMATTER_PATTERN = DateTimeFormatter.ofPattern("MMM d, yyyy");
    static final int MAX_NUM_DAYS_FOR_CUSTOM_MESSAGE = 5;

    public static String formattedDate(Resources resources, Instant dueDate, Clock clock) {
        return resources.getString(
                R.string.due_date_generic,
                ZonedDateTime
                        .ofInstant(dueDate, clock.getZone())
                        .format(DATE_TIME_FORMATTER_PATTERN)
        );
    }

    public static String durationMessageOrDueDate(Resources resources, Instant dueDate, Clock clock) {
        String message = durationMessage(resources, dueDate, clock);
        if(!message.isEmpty())
            return message;
        return formattedDate(resources, dueDate, clock);
    }

    static String durationMessage(Resources resources, Instant dueDate, Clock clock) {
        int daysTillDue = ((int) Duration.between(Instant.now(clock), dueDate).toDays());
        if (daysTillDue < 0) {
            int daysOverdue = daysTillDue * -1;
            return resources.getQuantityString(R.plurals.due_date_overdue_x_days, daysOverdue, daysOverdue);
        }else if (daysTillDue == 0) {
            return  resources.getString(R.string.due_date_today) ;
        }else if (daysTillDue < MAX_NUM_DAYS_FOR_CUSTOM_MESSAGE) {
            return  resources.getQuantityString(R.plurals.due_date_days, daysTillDue, daysTillDue);
        }else {
            return "";
        }
    }


}

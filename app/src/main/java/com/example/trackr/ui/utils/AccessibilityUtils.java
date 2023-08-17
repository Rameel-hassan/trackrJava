package com.example.trackr.ui.utils;

import android.content.Context;
import android.text.TextUtils;

import com.example.trackr.R;
import com.example.trackr.shared.utils.DateTimeUtils;
import com.example.trackr.shared.db.views.TaskSummary;

import java.time.Clock;
import java.util.stream.Collectors;

/**
 * @author Rameel Hassan
 * Created 15/03/2023 at 12:48 PM
 */
public class AccessibilityUtils {

    private static final String COLON_SEPARATOR = ": ";
    private static final String PERIOD_SEPARATOR = ".";
    private static final String PERIOD_SEPARATOR_AND_SPACE = "$PERIOD_SEPARATOR ";

   public static String taskSummaryLabel(Context context, TaskSummary taskSummary, Clock clock) {

       StringBuffer sb = new StringBuffer();

        sb.append(taskSummary.title).append(PERIOD_SEPARATOR_AND_SPACE);

        sb.append(context.getString(com.example.trackr.shared.R.string.owner)).append(COLON_SEPARATOR);
        sb.append(taskSummary.owner.username).append(PERIOD_SEPARATOR_AND_SPACE);

        sb.append(
                DateTimeUtils.durationMessageOrDueDate(
                        context.getResources(),
                        taskSummary.dueAt,
                        clock
                )
        );
        sb.append(taskSummary.tags.isEmpty() ? PERIOD_SEPARATOR : PERIOD_SEPARATOR_AND_SPACE);

        sb.append(
                TextUtils.join(PERIOD_SEPARATOR_AND_SPACE,
                        taskSummary.tags.stream().map(tag -> context.getString(R.string.tag_label,tag.getLabel())).collect(Collectors.toList())
                )
        );

        return sb.toString();
    }



}

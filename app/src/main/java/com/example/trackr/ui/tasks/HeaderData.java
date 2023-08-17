package com.example.trackr.ui.tasks;

import android.content.Context;

import com.example.trackr.R;
import com.example.trackr.shared.db.tables.TaskStatus;

/**
 * @author Rameel Hassan
 * Created 14/06/2023 at 12:52 PM
 */
public class HeaderData {
    private int count;
    private TaskStatus taskStatus;
    private boolean expanded;

    public HeaderData(int count, TaskStatus taskStatus, boolean expanded) {
        this.count = count;
        this.taskStatus = taskStatus;
        this.expanded = expanded;
    }

    public int getCount() {
        return count;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public String stateDescription(Context context) {
        return context.getString(expanded ? R.string.expanded : R.string.collapsed);
    }

    public String label(Context context) {
        return context.getString(R.string.header_label_with_count, context.getString(taskStatus.getStringResId()), count);
    }
}

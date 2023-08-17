package com.example.trackr.ui.tasks;

import com.example.trackr.shared.db.views.TaskSummary;

/**
 * @author Rameel Hassan
 * Created 14/06/2023 at 12:51 PM
 */
public abstract class ListItem {
    public abstract long getId();

    public static final class TypeTask extends ListItem {
        private TaskSummary taskSummary;

        public TypeTask(TaskSummary taskSummary) {
            this.taskSummary = taskSummary;
        }

        @Override
        public long getId() {
            return taskSummary.getId();
        }

        public TaskSummary getTaskSummary() {
            return taskSummary;
        }
    }

    public static final class TypeHeader extends ListItem {
        private HeaderData headerData;

        public TypeHeader(HeaderData headerData) {
            this.headerData = headerData;
        }

        @Override
        public long getId() {
            return Long.MIN_VALUE;
        }

        public HeaderData getHeaderData() {
            return headerData;
        }
    }
}


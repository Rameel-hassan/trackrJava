package com.example.trackr.ui.tasks;

/**
 * @author Rameel Hassan
 * Created 14/06/2023 at 1:50 PM
 */
import com.example.trackr.shared.db.tables.TaskStatus;
import com.example.trackr.shared.db.views.TaskSummary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListItemsCreator {
    private List<TaskSummary> taskSummaries;
    private Map<TaskStatus, Boolean> expandedStatesMap;

    public ListItemsCreator(List<TaskSummary> taskSummaries, Map<TaskStatus, Boolean> expandedStatesMap) {
        this.taskSummaries = taskSummaries;
        this.expandedStatesMap = expandedStatesMap;
    }

    public List<ListItem> execute() {
        List<ListItem> itemsToSubmit = new ArrayList<>();
        Map<TaskStatus, List<TaskSummary>> statusToItemsMap = new HashMap<>();

        // Group taskSummaries by status
        for (TaskSummary taskSummary : taskSummaries) {
            List<TaskSummary> sublist = statusToItemsMap.getOrDefault(taskSummary.getStatus(), new ArrayList<>());
            sublist.add(taskSummary);
            statusToItemsMap.put(taskSummary.getStatus(), sublist);
        }

        for (Map.Entry<TaskStatus, Boolean> entry : expandedStatesMap.entrySet()) {
            TaskStatus status = entry.getKey();
            boolean isExpanded = entry.getValue();

            List<TaskSummary> sublist = statusToItemsMap.get(status);

            int count = sublist != null ? sublist.size() : 0;

            itemsToSubmit.add(new ListItem.TypeHeader(new HeaderData(count, status, isExpanded)));

            if (isExpanded && sublist != null) {
                for (TaskSummary taskSummary : sublist) {
                    itemsToSubmit.add(new ListItem.TypeTask(taskSummary));
                }
            }
        }

        return itemsToSubmit;
    }
}

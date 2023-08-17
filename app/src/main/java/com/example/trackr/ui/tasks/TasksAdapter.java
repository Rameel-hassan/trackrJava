package com.example.trackr.ui.tasks;

/**
 * @author Rameel Hassan
 * Created 14/06/2023 at 12:16 PM
 */


import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackr.R;
import com.example.trackr.databinding.HeaderItemBinding;
import com.example.trackr.databinding.TaskSummaryBinding;
import com.example.trackr.shared.db.views.TaskSummary;
import com.example.trackr.ui.utils.AccessibilityUtils;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TasksAdapter extends ListAdapter<ListItem, RecyclerView.ViewHolder> {
    private ItemListener itemListener;
    private Clock clock;
    private DragAndDropActionsHelper dragAndDropActionsHelper;

    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_TASK = 1;

    public TasksAdapter(ItemListener itemListener, Clock clock) {
        super(new ListItemDiffCallback());
        this.itemListener = itemListener;
        this.clock = clock;
    }

    @Override
    public void onCurrentListChanged(List<ListItem> previousList, List<ListItem> currentList) {
        super.onCurrentListChanged(previousList, currentList);
        dragAndDropActionsHelper = new DragAndDropActionsHelper(currentList);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_VIEW_TYPE_HEADER:
                return HeaderViewHolder.from(parent, itemListener);
            case ITEM_VIEW_TYPE_TASK:
                return TaskViewHolder.from(parent, itemListener, clock);
            default:
                throw new IllegalArgumentException("Unknown viewType " + viewType);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ListItem item = getItem(position);
        if (item instanceof ListItem.TypeHeader) {
            return ITEM_VIEW_TYPE_HEADER;
        } else if (item instanceof ListItem.TypeTask) {
            return ITEM_VIEW_TYPE_TASK;
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM_VIEW_TYPE_HEADER:
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                headerViewHolder.bind(((ListItem.TypeHeader) getItem(position)).getHeaderData());
                break;
            case ITEM_VIEW_TYPE_TASK:
                TaskViewHolder taskViewHolder = (TaskViewHolder) holder;
                taskViewHolder.bind(((ListItem.TypeTask) getItem(position)).getTaskSummary(), dragAndDropActionsHelper);
                break;
        }
    }

    public ListItem.TypeHeader findHeaderItem(int position) {
        int p = position;
        while (p >= 0 && p < getItemCount()) {
            ListItem item = getItem(p);
            if (item instanceof ListItem.TypeHeader) {
                return (ListItem.TypeHeader) item;
            }
            p--;
        }
        return null;
    }

    public void changeTaskPosition(int fromPosition, int toPosition) {
        // TODO: persist new order in the db instead of calling submitList()
        ListItem fromItem = getItem(fromPosition);
        ListItem toItem = getItem(toPosition);
        if (fromItem instanceof ListItem.TypeTask && toItem instanceof ListItem.TypeTask) {
            TaskSummary fromTask = ((ListItem.TypeTask) fromItem).getTaskSummary();
            TaskSummary toTask = ((ListItem.TypeTask) toItem).getTaskSummary();
            if (fromTask.getStatus() != toTask.getStatus()) {
                return;
            }
            List<ListItem> newList = new ArrayList<>(getCurrentList());
            Collections.swap(newList, fromPosition, toPosition);
            submitList(newList);
        }
    }

    public interface ItemListener {
        void onHeaderClicked(HeaderData headerData);
        void onStarClicked(TaskSummary taskSummary);
        void onTaskClicked(TaskSummary taskSummary);
        void onTaskArchived(TaskSummary taskSummary);
        void onTaskDragged(int fromPosition, int toPosition);
        void onDragStarted();
        void onDragCompleted(int fromPosition, int toPosition, boolean usingDragAndDropCustomActions);
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private HeaderItemBinding binding;
        private ItemListener itemListener;

        private HeaderViewHolder(HeaderItemBinding binding, ItemListener itemListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.itemListener = itemListener;
        }

        public void bind(HeaderData headerData) {
            binding.setHeaderData(headerData);
            binding.setListener(itemListener);
            ViewCompat.setStateDescription(binding.getRoot(), headerData.stateDescription(binding.getRoot().getContext()));
            ViewCompat.setAccessibilityHeading(binding.getRoot(), true);
        }

        public static HeaderViewHolder from(ViewGroup parent, ItemListener itemListener) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            HeaderItemBinding binding = HeaderItemBinding.inflate(layoutInflater, parent, false);
            return new HeaderViewHolder(binding, itemListener);
        }
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder implements SwipeAndDragCallback.ItemTouchListener {
        private TaskSummaryBinding binding;
        private ItemListener itemListener;
        private Clock clock;

        private ArrayList<Integer> accessibilityActionIds = new ArrayList<>();

        private TaskViewHolder(TaskSummaryBinding binding, ItemListener itemListener, Clock clock) {
            super(binding.getRoot());
            this.binding = binding;
            this.itemListener = itemListener;
            this.clock = clock;
        }

        public void bind(TaskSummary taskSummary, DragAndDropActionsHelper dragAndDropActionsHelper) {
            Resources resources = binding.getRoot().getResources();
            binding.setTaskSummary(taskSummary);
            binding.card.setOnClickListener(v -> itemListener.onTaskClicked(taskSummary));
            binding.star.setOnClickListener(v -> itemListener.onStarClicked(taskSummary));
            binding.setClock(clock);
            binding.getRoot().setContentDescription(AccessibilityUtils.taskSummaryLabel(binding.getRoot().getContext(), taskSummary, clock));

            ViewCompat.setStateDescription(
                    binding.getRoot(),
                    resources.getString(taskSummary.getStatus().getStringResId()) + ", " +
                            resources.getString(taskSummary.getStarred() ? R.string.starred : R.string.unstarred)
            );

            binding.chipGroup.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);

            removeAccessibilityActions(binding.getRoot());

            addArchiveAccessibilityAction(taskSummary);
            addStarAccessibilityAction(taskSummary);

            List<DragAndDropActionsHelper.DragAndDropActionInfo> actionParams = dragAndDropActionsHelper.execute(getAdapterPosition());
            for (DragAndDropActionsHelper.DragAndDropActionInfo actionParam : actionParams) {
                addDragAndDropAction(actionParam);
            }

            binding.executePendingBindings();
        }

        @Override
        public void onItemSwiped() {
            TaskSummary taskSummary = binding.getTaskSummary();
            if (taskSummary != null) {
                itemListener.onTaskArchived(taskSummary);
            }
        }

        @Override
        public void onItemMoved(int fromPosition, int toPosition) {
            itemListener.onTaskDragged(fromPosition, toPosition);
        }

        @Override
        public void onItemMoveStarted() {
            itemListener.onDragStarted();
        }

        @Override
        public void onItemMoveCompleted(int fromPosition, int toPosition) {
            itemListener.onDragCompleted(fromPosition, toPosition,true);
        }

        private void addArchiveAccessibilityAction(TaskSummary taskSummary) {
            accessibilityActionIds.add(ViewCompat.addAccessibilityAction(
                    binding.getRoot(),
                    binding.getRoot().getContext().getString(R.string.archive),
                    (v, e) -> {
                        itemListener.onTaskArchived(taskSummary);
                        return true;
                    })
            );
        }

        private void addDragAndDropAction(DragAndDropActionsHelper.DragAndDropActionInfo actionParamAnd) {
            accessibilityActionIds.add(ViewCompat.addAccessibilityAction(
                    binding.getRoot(),
                    binding.getRoot().getContext().getResources().getString(actionParamAnd.getLabel()),
                    (v, e) -> {
                        doDrag(actionParamAnd.getFromPosition(), actionParamAnd.getToPosition());
                        return true;
                    })
            );
        }

        private void doDrag(int fromPosition, int toPosition) {
            itemListener.onDragStarted();
            itemListener.onTaskDragged(fromPosition, toPosition);
            itemListener.onDragCompleted(fromPosition, toPosition, true);
        }

        private void removeAccessibilityActions(View view) {
            for (int actionId : accessibilityActionIds) {
                ViewCompat.removeAccessibilityAction(view, actionId);
            }
            accessibilityActionIds.clear();
        }

        private void addStarAccessibilityAction(TaskSummary taskSummary) {
            accessibilityActionIds.add(ViewCompat.addAccessibilityAction(
                    binding.getRoot(),
                    taskSummary.getStarred() ? binding.getRoot().getContext().getString(R.string.unstar) : binding.getRoot().getContext().getString(R.string.star),
                    (v, e) -> {
                        itemListener.onStarClicked(taskSummary);
                        return true;
                    })
            );
        }

        public static TaskViewHolder from(ViewGroup parent, ItemListener itemListener, Clock clock) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            TaskSummaryBinding binding = TaskSummaryBinding.inflate(layoutInflater, parent, false);
            return new TaskViewHolder(binding, itemListener, clock);
        }
    }

}


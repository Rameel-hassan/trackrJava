package com.example.trackr.ui.tasks;

/**
 * @author Rameel Hassan
 * Created 14/06/2023 at 12:46 PM
 */
import com.example.trackr.R;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class DragAndDropActionsHelper {
    private List<ListItem> items;
    private List<Integer> headerPositions;
    private int previousHeaderPosition = NO_POSITION;
    private int nextHeaderPosition = NO_POSITION;

    private static final int NO_POSITION = -1;

    public DragAndDropActionsHelper(List<ListItem> items) {
        this.items = items;
        headerPositions = new ArrayList<>();
        for (int index = 0; index < items.size(); index++) {
            ListItem item = items.get(index);
            if (item instanceof ListItem.TypeHeader) {
                headerPositions.add(index);
            }
        }
    }

    public List<DragAndDropActionInfo> execute(int position) {
        try {
            previousHeaderPosition = headerPositions.get(0);
            for (Integer headerPosition : headerPositions) {
                if (headerPosition < position) {
                    previousHeaderPosition = headerPosition;
                }
            }

            nextHeaderPosition = headerPositions.get(headerPositions.size() - 1);
            if (nextHeaderPosition < position) { // there is no next header
                nextHeaderPosition = items.size();
            } else {
                for (int i = headerPositions.size() - 1; i >= 0; i--) {
                    int headerPosition = headerPositions.get(i);
                    if (headerPosition > position) {
                        nextHeaderPosition = headerPosition;
                    }
                }
            }

            return obtainDragAndDropActionInfo(position);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            // We get here if there are no headers. In that case, there are no actions associated
            // with drag and drop.
            return new ArrayList<>();
        }
    }

    private List<DragAndDropActionInfo> obtainDragAndDropActionInfo(int position) {
        List<DragAndDropActionInfo> actionParams = new ArrayList<>();

        if (nextHeaderPosition != NO_POSITION
                && previousHeaderPosition != NO_POSITION
                && nextHeaderPosition - previousHeaderPosition > 2) {
            // Only one item between two headers.
            if (position - previousHeaderPosition > 1) {
                actionParams.add(new DragAndDropActionInfo(
                        position,
                        previousHeaderPosition + 1,
                        R.string.move_to_top
                ));
                if (position - previousHeaderPosition > 2) {
                    actionParams.add(new DragAndDropActionInfo(
                            position,
                            position - 1,
                            R.string.move_up_one
                    ));
                }
            }

            if (nextHeaderPosition - position > 1) {
                actionParams.add(new DragAndDropActionInfo(
                        position,
                        nextHeaderPosition - 1,
                        R.string.move_to_bottom
                ));
                if (nextHeaderPosition - position > 2) {
                    actionParams.add(new DragAndDropActionInfo(
                            position,
                            position + 1,
                            R.string.move_down_one
                    ));
                }
            }
        }

        return actionParams;
    }

    /**
     * Contains data for building a custom accessibility action to enable the dragging and dropping
     * of items.
     */
    public static class DragAndDropActionInfo {
        private int fromPosition;
        private int toPosition;
        private int label;

        public DragAndDropActionInfo(int fromPosition, int toPosition, int label) {
            this.fromPosition = fromPosition;
            this.toPosition = toPosition;
            this.label = label;
        }

        public int getFromPosition() {
            return fromPosition;
        }

        public int getToPosition() {
            return toPosition;
        }

        public int getLabel() {
            return label;
        }
    }
}

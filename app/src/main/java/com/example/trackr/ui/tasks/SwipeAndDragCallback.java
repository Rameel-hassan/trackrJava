package com.example.trackr.ui.tasks;

/**
 * @author Rameel Hassan
 * Created 14/06/2023 at 12:14 PM
 */
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeAndDragCallback extends ItemTouchHelper.SimpleCallback {

    private int initialPosition = NO_POSITION;
    private int fromPosition = NO_POSITION;
    private int toPosition = NO_POSITION;

    public interface ItemTouchListener {
        void onItemSwiped();
        void onItemMoved(int fromPosition, int toPosition);
        void onItemMoveStarted();
        void onItemMoveCompleted(int fromPosition, int toPosition);
    }

    public SwipeAndDragCallback() {
        super(NO_DRAG, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = NO_DRAG;
        int swipeFlags = NO_SWIPE;
        if (viewHolder instanceof ItemTouchListener) {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        }
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // Disable swiping unless the viewHolder explicitly supports it.
        if (viewHolder instanceof ItemTouchListener) {
            return super.getSwipeDirs(recyclerView, viewHolder);
        }
        return NO_SWIPE;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true; // We track if the item has been moved to a target adapter position
    }

    @Override
    public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
        fromPosition = fromPos;
        toPosition = toPos;
        // Store the initial position. This helps check if the item was actually dragged to a new
        // position.
        if (initialPosition == NO_POSITION) {
            initialPosition = viewHolder.getAdapterPosition();
        }

        if (viewHolder instanceof ItemTouchListener) {
            ((ItemTouchListener) viewHolder).onItemMoved(fromPos, toPos);
        }
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        switch (actionState) {
            case ItemTouchHelper.ACTION_STATE_DRAG:
                if (viewHolder instanceof ItemTouchListener) {
                    ((ItemTouchListener) viewHolder).onItemMoveStarted();
                }
                break;
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (viewHolder instanceof ItemTouchListener) {
            // If the drag was abandoned, or if the item was dragged back to the original position,
            // do nothing.
            if (initialPosition != NO_POSITION && viewHolder.getAdapterPosition() != initialPosition) {
                ((ItemTouchListener) viewHolder).onItemMoveCompleted(fromPosition, toPosition);
            }
        }
        initialPosition = NO_POSITION;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (viewHolder instanceof ItemTouchListener) {
            ((ItemTouchListener) viewHolder).onItemSwiped();
        }
    }

    public static final int NO_DRAG = 0;
    public static final int NO_SWIPE = 0;
    public static final int NO_POSITION = -1;
}

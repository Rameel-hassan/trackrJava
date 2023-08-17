package com.example.trackr.ui;

/**
 * @author Rameel Hassan
 * Created 13/06/2023 at 5:39 PM
 */
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.DrawableRes;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.widget.TextViewCompat;
import androidx.databinding.BindingAdapter;

import com.example.trackr.R;
import com.example.trackr.shared.db.tables.Tag;
import com.example.trackr.shared.db.tables.TaskStatus;
import com.example.trackr.shared.utils.DateTimeUtils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.time.Clock;
import java.time.Instant;
import java.util.Collections;
import java.util.List;


public class BindingAdapters {

    /**
     * Sets the visibility of this view to either {@link View#GONE} or {@link View#VISIBLE}.
     */
    @BindingAdapter("isGone")
    public static void setIsGone(View view, boolean isGone) {
        view.setVisibility(isGone ? View.GONE : View.VISIBLE);
    }

    /**
     * Sets the visibility of this view to either {@link View#INVISIBLE} or {@link View#VISIBLE}.
     */
    @BindingAdapter("isInvisible")
    public static void setIsInvisible(View view, boolean isInvisible) {
        view.setVisibility(isInvisible ? View.INVISIBLE : View.VISIBLE);
    }

    /**
     * Sets tags to be shown in this {@link ChipGroup}.
     *
     * @param tags        The list of tags to show.
     * @param showAllTags Whether all the tags should be shown or they should be truncated to the number
     *                    of available {@link Chip} in this {@link ChipGroup}. This should be {@code false}
     *                    when the method should not inflate new views.
     */
    @BindingAdapter({"tags", "showAllTags"})
    public static void setTags(ChipGroup chipGroup, List<Tag> tags, boolean showAllTags) {
        bindTags(chipGroup, tags != null ? tags : Collections.emptyList(), showAllTags);
    }

    private static void bindTags(ChipGroup chipGroup, List<Tag> tags, boolean showAllTags) {
        int index = 0;

        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            View child = chipGroup.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                if (index >= tags.size()) {
                    chip.setVisibility(View.GONE);
                } else {
                    bindTag(chip, tags.get(index));
                    chip.setVisibility(View.VISIBLE);
                }
                index++;
            }
        }

        if (showAllTags) {
            LayoutInflater inflater = LayoutInflater.from(chipGroup.getContext());
            while (index < tags.size()) {
                Chip chip = (Chip) inflater.inflate(R.layout.tag, chipGroup, false);
                bindTag(chip, tags.get(index));
                chipGroup.addView(chip, index);
                index++;
            }
        } else {
            int childCount = chipGroup.getChildCount();
            if (childCount > 0 && chipGroup.getChildAt(childCount - 1) instanceof TextView) {
                TextView label = (TextView) chipGroup.getChildAt(childCount - 1);
                int extraCount = tags.size() - index;
                if (extraCount > 0) {
                    label.setVisibility(View.VISIBLE);
                    label.setText(chipGroup.getResources().getString(R.string.more_tags, extraCount));
                } else {
                    label.setVisibility(View.GONE);
                }
            }
        }
    }

    private static void bindTag(Chip chip, Tag tag) {
        chip.setText(tag.getLabel());
        int textColorAttr = tag.getColor().getTextColor();
        int backgroundColorAttr = tag.getColor().getBackgroundColor();

        TypedValue typedValue = new TypedValue();
        chip.getContext().getTheme().resolveAttribute(textColorAttr, typedValue, true);
        int textColor = typedValue.data;
        chip.setTextColor(textColor);

        chip.getContext().getTheme().resolveAttribute(backgroundColorAttr, typedValue, true);
        int backgroundColor = typedValue.data;
        chip.setChipBackgroundColor(ColorStateList.valueOf(backgroundColor));
    }

    @BindingAdapter({"dueMessageOrDueDate", "clock"})
    public static void showFormattedDueMessageOrDueDate(TextView view, Instant instant, Clock clock) {
        if (instant == null || clock == null) {
            view.setText("");
        } else {
            view.setText(DateTimeUtils.durationMessageOrDueDate(view.getResources(), instant, clock));
        }
    }

    /**
     * Binding adapter to format due date of task to a human-readable format. If the due date is not
     * close, the {@code view} is hidden.
     *
     * @param view     The view to display the formatted due date.
     * @param dueDate  The due date of the task.
     * @param clock    The clock to calculate the duration from the current time.
     */
    @BindingAdapter({"dueMessageOrHide", "clock"})
    public static void showFormattedDueMessageOrHide(TextView view, Instant dueDate, Clock clock) {
        String text = "";
        if (dueDate != null) {
            text = DateTimeUtils.durationMessageOrDueDate(view.getResources(), dueDate, clock);
        }
        view.setVisibility(text.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @BindingAdapter({"formattedDate", "clock"})
    public static void formattedGenericDate(TextView view, Instant instant, Clock clock) {
        if (instant != null) {
            view.setText(DateTimeUtils.formattedDate(view.getResources(), instant, clock));
        }
    }

    /**
     * Replaces the label for the click action associated with {@code view}. The custom
     * label is then passed on to the user of an accessibility service, which can use {@code label}.
     * For example, this replaces Talkback's generic "double tap to activate" announcement with the more
     * descriptive "double tap to {@code label}" action label.
     *
     * @param view  The view to add the click action label.
     * @param label The custom label for the click action.
     */
    @BindingAdapter("clickActionLabel")
    public static void addClickActionLabel(View view, String label) {
        ViewCompat.replaceAccessibilityAction(
                view,
                AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK,
                label,
                null
        );
    }

    @BindingAdapter("android:text")
    public static void setText(TextView textView, TaskStatus status) {
        if (status != null) {
            textView.setText(status.getStringResId());
        } else {
            textView.setText(null);
        }
    }

    @BindingAdapter("drawableStartCompat")
    public static void setDrawableStartCompat(TextView textView, @DrawableRes int drawableResId) {
        if (drawableResId == 0) {
            return;
        }

        ResourcesCompat.getDrawable(textView.getResources(), drawableResId, textView.getContext().getTheme());
        int size = textView.getResources().getDimensionPixelSize(R.dimen.home_task_star_size);

        Drawable drawable = ResourcesCompat.getDrawable(textView.getResources(), drawableResId, textView.getContext().getTheme()) ;
        if (drawable == null)
            return;
        drawable.setBounds(0, 0, size, size);
        TextViewCompat.setCompoundDrawablesRelative(textView, drawable, null, null, null);
    }

    /**
     * Ensures that the touchable area of {@code view} equals {@code minTouchTarget} by expanding the touch area
     * of a view beyond its actual view bounds. This adapter can be used to expand the touchable area of a
     * view when other options (adding padding, for example) may not be available.
     *
     * @param view            The view whose touch area may be expanded.
     * @param minTouchTarget  The minimum touch area expressed as a dimen resource.
     */
    @BindingAdapter("ensureMinTouchArea")
    public static void addTouchDelegate(View view, float minTouchTarget) {
        View parent = (View) view.getParent();
        parent.post(() -> {
            Rect delegate = new Rect();
            view.getHitRect(delegate);

            float density = view.getResources().getDisplayMetrics().density;
            int height = (int) Math.ceil(delegate.height() / density);
            int width = (int) Math.ceil(delegate.width() / density);
            float minTarget = minTouchTarget / density;
            int extraSpace = 0;

            if (height < minTarget) {
                extraSpace = (int) ((minTarget - height) / 2);
                delegate.top -= extraSpace;
                delegate.bottom += extraSpace;
            }

            if (width < minTarget) {
                extraSpace = (int) ((minTarget - width) / 2);
                delegate.left -= extraSpace;
                delegate.right += extraSpace;
            }

            parent.setTouchDelegate(new TouchDelegate(delegate, view));
        });
    }
}

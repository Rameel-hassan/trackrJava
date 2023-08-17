package com.example.trackr.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;

import com.example.trackr.R;


/**
 * @author Rameel Hassan
 * Created 14/03/2023 at 2:15 PM
 */
public class StarButton extends AppCompatImageButton implements Checkable {

    Context mContext;
    AttributeSet mAttrs;
    int mDefStyleAttr = 0;

    private boolean _checked = false;

    @VisibleForTesting
    int drawableResId = R.drawable.ic_star_border;

    public StarButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mAttrs = attrs;
        mDefStyleAttr = defStyleAttr;
    }
    public StarButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mAttrs = attrs;
    }
    public StarButton(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void setChecked(boolean b) {
        _checked = b;
        if (b) {
            drawableResId = R.drawable.ic_star;
            setContentDescription(mContext.getString(R.string.starred));
        } else {
            drawableResId = R.drawable.ic_star_border;
            setContentDescription(mContext.getString(R.string.unstarred));
        }
        setBackground(ContextCompat.getDrawable(mContext, drawableResId));


    }

    @Override
    public boolean isChecked() {
        return _checked;
    }

    @Override
    public void toggle() {
        _checked = !_checked;
    }
}

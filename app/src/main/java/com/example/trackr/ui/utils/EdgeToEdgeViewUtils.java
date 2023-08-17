package com.example.trackr.ui.utils;

/**
 * @author Rameel Hassan
 * Created 13/06/2023 at 3:56 PM
 */
import android.view.View;
import android.view.ViewGroup;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.BindingAdapter;

import org.jetbrains.annotations.NotNull;

public class EdgeToEdgeViewUtils {

    public static void doOnApplyWindowInsets(View view, OnApplyWindowInsetsListener listener) {
        Insets padding = recordPadding(view);
        Insets margins = recordMargins(view);

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) ->
                listener.onApplyWindowInsets(v, insets, padding, margins)
        );

        requestApplyInsetsWhenAttached(view);
    }

    public interface OnApplyWindowInsetsListener {
        WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat insets,
                                               Insets padding, Insets margins);
    }

    private static Insets recordPadding(@NotNull View view) {
        return Insets.of(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
    }

    private static Insets recordMargins(@NotNull View view) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if (layoutParams != null) {
            return Insets.of(
                    layoutParams.leftMargin,
                    layoutParams.topMargin,
                    layoutParams.rightMargin,
                    layoutParams.bottomMargin
            );
        }
        return Insets.NONE;
    }

    private static void requestApplyInsetsWhenAttached(View view) {
        if (view.isAttachedToWindow()) {
            view.requestApplyInsets();
        } else {
            view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    v.removeOnAttachStateChangeListener(this);
                    v.requestApplyInsets();
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                }
            });
        }
    }


    @BindingAdapter(value = {
            "paddingLeftSystemBars",
            "paddingTopSystemBars",
            "paddingRightSystemBars",
            "paddingBottomSystemBars",
            "marginLeftSystemBars",
            "marginTopSystemBars",
            "marginRightSystemBars",
            "marginBottomSystemBars"}, requireAll = false)
    public static void applySystemBars(View view,
                                       Boolean padLeft,
                                       Boolean padTop,
                                       Boolean padRight,
                                       Boolean padBottom,
                                       Boolean marginLeft,
                                       Boolean marginTop,
                                       Boolean marginRight,
                                       Boolean marginBottom)
    {
        boolean adjustPadding = padLeft != null && padLeft
                || padTop != null && padTop
                || padRight != null && padRight
                || padBottom != null && padBottom;

        boolean adjustMargins = marginLeft != null && marginLeft
                || marginTop != null && marginTop
                || marginRight != null && marginRight
                || marginBottom != null && marginBottom;

        if (!(adjustPadding || adjustMargins)) {
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets padding = recordPadding(v);
            Insets margins = recordMargins(v);
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            if (adjustPadding) {
                int systemLeft = padLeft != null && padLeft ? systemBars.left : 0;
                int systemTop = padTop != null && padTop ? systemBars.top : 0;
                int systemRight = padRight != null && padRight ? systemBars.right : 0;
                int systemBottom = padBottom != null && padBottom ? systemBars.bottom : 0;

                v.setPadding(
                        padding.left + systemLeft,
                        padding.top + systemTop,
                        padding.right + systemRight,
                        padding.bottom + systemBottom
                );
            }

            if (adjustMargins) {
                int systemLeft = marginLeft != null && marginLeft ? systemBars.left : 0;
                int systemTop = marginTop != null && marginTop ? systemBars.top : 0;
                int systemRight = marginRight != null && marginRight ? systemBars.right : 0;
                int systemBottom = marginBottom != null && marginBottom ? systemBars.bottom : 0;

                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                layoutParams.leftMargin = margins.left + systemLeft;
                layoutParams.topMargin = margins.top + systemTop;
                layoutParams.rightMargin = margins.right + systemRight;
                layoutParams.bottomMargin = margins.bottom + systemBottom;
                v.setLayoutParams(layoutParams);
            }

            return insets;
        });

        requestApplyInsetsWhenAttached(view);
    }
}

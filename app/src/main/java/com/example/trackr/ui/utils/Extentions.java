package com.example.trackr.ui.utils;

import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import kotlin.jvm.internal.Intrinsics;

/**
 * @author Rameel Hassan
 * Created 17/03/2023 at 4:02 PM
 */
public final class Extentions {



    @NotNull
    public static final Fragment requireFragment(@NotNull Fragment fragment, int id) {
        return Objects.requireNonNull(fragment.getChildFragmentManager().findFragmentById(id));
    }

    public static boolean isRtl(@NotNull View view) {
        return ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }
}

package com.example.trackr.ui.utils;

/**
 * @author Rameel Hassan
 * Created 13/06/2023 at 11:51 AM
 */

import android.content.ClipData;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigationrail.NavigationRailView;
import java.lang.ref.WeakReference;

public class NavigationRailViewUtils {
    public static void setupWithNavController(NavigationRailView navigationRailView, NavController navController) {
        navigationRailView.setOnItemSelectedListener(item -> NavigationUI.onNavDestinationSelected(item, navController));
        WeakReference<NavigationRailView> weakRef = new WeakReference<>(navigationRailView);
        NavController.OnDestinationChangedListener listener = new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(NavController controller, NavDestination destination, Bundle arguments) {
                NavigationRailView view = weakRef.get();
                if (view == null) {
                    navController.removeOnDestinationChangedListener(this);
                } else {
                    int size = view.getMenu().size();
                    for (int i = 0 ; i < size ; i++) {
                        MenuItem item = view.getMenu().getItem(i);
                        if (matchNavDestination(destination, item.getItemId())) {
                            item.setChecked(true);
                        }
                    }
                }
            }
        };
        navController.addOnDestinationChangedListener(listener);
    }

    private static boolean matchNavDestination(NavDestination destination, int id) {
        NavDestination currentDestination = destination;
        while (currentDestination.getId() != id && currentDestination.getParent() != null) {
            currentDestination = currentDestination.getParent();
        }
        return currentDestination.getId() == id;
    }
}

package com.example.trackr;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.trackr.databinding.MainActivityBinding;
import com.example.trackr.ui.utils.Extentions;
import com.example.trackr.ui.utils.EdgeToEdgeViewUtils;
import com.example.trackr.ui.utils.NavigationRailViewUtils;
import com.google.android.material.navigationrail.NavigationRailView;


import dagger.hilt.android.AndroidEntryPoint;

/**
 * @author Rameel Hassan
 * Created 14/03/2023 at 11:34 AM
 */
@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private MainActivityBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = MainActivityBinding.inflate(getLayoutInflater());
        binding.setLifecycleOwner(this);

        setContentView(binding.getRoot());

        EdgeToEdgeViewUtils.doOnApplyWindowInsets(binding.activityRoot, new EdgeToEdgeViewUtils.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat insets, Insets padding, Insets margins) {
                boolean isRtl = Extentions.isRtl(view);
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                binding.navigationRail.setPadding(
                        isRtl ? 0 : systemBars.left,
                        systemBars.top,
                        isRtl ? systemBars.right : 0,
                        systemBars.bottom
                );
                Insets inset = Insets.of(isRtl ? systemBars.left : 0, systemBars.top, isRtl ? 0 : systemBars.right, systemBars.bottom);
                return new WindowInsetsCompat.Builder().setInsets(WindowInsetsCompat.Type.systemBars(),inset).build();
            }
        });

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setNavigationRailProperties(binding.navigationRail);
    }

    private void setNavigationRailProperties(NavigationRailView navigationRail) {

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        NavController navController = navHostFragment.getNavController();

        NavigationRailViewUtils.setupWithNavController(navigationRail,navController);
        navigationRail.setOnItemReselectedListener(item -> {}); // Prevent navigating to the same item.
        navigationRail.setOnApplyWindowInsetsListener(null); // See above about consuming window insets.
        navigationRail.getHeaderView().setOnClickListener(v -> navController.navigate(R.id.nav_task_edit_graph));
    }
}

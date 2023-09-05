package com.example.trackr.ui;

/**
 * @author Rameel Hassan
 * Created 15/08/2023 at 2:45 pm
 */

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.NavController;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;


import com.example.trackr.NavTaskEditGraphArgs;
import com.example.trackr.R;
import com.example.trackr.ui.utils.EdgeToEdgeViewUtils;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;



public abstract class BaseTwoPaneFragment extends Fragment {

    public BaseTwoPaneFragment() {
        super();
    }

    public BaseTwoPaneFragment(@LayoutRes int contentLayoutId) {
        super(contentLayoutId);
    }

    TwoPaneViewModel twoPaneViewModel;
    private SlidingPaneBackPressHandler backPressHandler;

    public abstract SlidingPaneLayout getSlidingPaneLayout();


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SlidingPaneLayout slidingPaneLayout = getSlidingPaneLayout();
        slidingPaneLayout.setLockMode(SlidingPaneLayout.LOCK_MODE_LOCKED);

        twoPaneViewModel = new ViewModelProvider(requireActivity()).get(TwoPaneViewModel.class);

        SlidingPaneBackPressHandler.doOnLayout(view, new Runnable() {
            @Override
            public void run() {
                EdgeToEdgeViewUtils.doOnApplyWindowInsets(slidingPaneLayout, (view1, insets, padding, margins) -> {
                    twoPaneViewModel.setIsTwoPane(!slidingPaneLayout.isSlideable());
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    view1.setPadding(
                            view1.getPaddingLeft() + systemBars.left,
                            view1.getPaddingTop(),
                            view1.getPaddingRight() + systemBars.right,
                            view1.getPaddingBottom()
                    );
                    Insets inset = Insets.of( 0, systemBars.top, 0, systemBars.bottom);
                    return new WindowInsetsCompat.Builder().setInsets(WindowInsetsCompat.Type.systemBars(),inset).build();
                });

            }
        });


        Disposable disposableDetailPaneUpEvents = twoPaneViewModel.detailPaneUpEventSubject
                .subscribe(unit -> {
                    if (backPressHandler.isEnabled()) {
                        backPressHandler.handleOnBackPressed();
                    }
                });

        Disposable disposableEditTaskEvents = twoPaneViewModel.editTaskEventSubject
                .subscribe(taskId -> {
                    NavController navController = NavHostFragment.findNavController(this);

                    NavTaskEditGraphArgs navArgs = new com.example.trackr.NavTaskEditGraphArgs.Builder().setTaskId(taskId).build();
                    navController.navigate(R.id.nav_task_edit_graph, navArgs.toBundle());
                });

        backPressHandler = new SlidingPaneBackPressHandler(slidingPaneLayout);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), backPressHandler);

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(disposableDetailPaneUpEvents);
        compositeDisposable.add(disposableEditTaskEvents);
    }
}

class SlidingPaneBackPressHandler extends OnBackPressedCallback implements SlidingPaneLayout.PanelSlideListener {

    private SlidingPaneLayout slidingPaneLayout;

    public SlidingPaneBackPressHandler(SlidingPaneLayout slidingPaneLayout) {
        super(false);
        this.slidingPaneLayout = slidingPaneLayout;
        slidingPaneLayout.addPanelSlideListener(this);
        doOnLayout(slidingPaneLayout, new Runnable() {
            @Override
            public void run() {
                syncState();
            }
        });
    }

    private void syncState() {
        setEnabled(slidingPaneLayout.isSlideable() && slidingPaneLayout.isOpen());
    }

    @Override
    public void handleOnBackPressed() {
        slidingPaneLayout.closePane();
    }

    @Override
    public void onPanelOpened(View panel) {
        syncState();
    }

    @Override
    public void onPanelClosed(View panel) {
        syncState();
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        // Empty
    }

    /**
     * Performs an action when the provided view is laid out.
     *
     * @param view     the view to listen to for layouts.
     * @param runnable the runnable to run when the view is
     *                 laid out.
     */
    public static void doOnLayout(@NonNull final View view, @NonNull final Runnable runnable) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                runnable.run();
            }
        });
    }

}


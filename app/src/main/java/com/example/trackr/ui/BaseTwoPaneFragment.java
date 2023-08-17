package com.example.trackr.ui;

/**
 * @author Rameel Hassan
 * Created 15/08/2023 at 2:45 pm
 */

import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.LayoutRes;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.NavController;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;

import com.example.trackr.R;
import com.example.trackr.ui.utils.EdgeToEdgeViewUtils;
import com.example.trackr.ui.utils.Extentions;

import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.subjects.BehaviorSubject;



public abstract class BaseTwoPaneFragment extends Fragment {

    public BaseTwoPaneFragment() {
        super();
    }

    public BaseTwoPaneFragment(@LayoutRes int contentLayoutId) {
        super(contentLayoutId);
    }

    @Inject
    private TwoPaneViewModel twoPaneViewModel;
    private SlidingPaneBackPressHandler backPressHandler;

    public abstract SlidingPaneLayout getSlidingPaneLayout();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SlidingPaneLayout slidingPaneLayout = getSlidingPaneLayout();
        slidingPaneLayout.setLockMode(SlidingPaneLayout.LOCK_MODE_LOCKED);


        EdgeToEdgeViewUtils.doOnApplyWindowInsets(slidingPaneLayout, new EdgeToEdgeViewUtils.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat insets, Insets padding, Insets margins) {
                twoPaneViewModel.setIsTwoPane(!slidingPaneLayout.isSlideable());
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                view.setPadding(
                        view.getPaddingLeft() + systemBars.left,
                        view.getPaddingTop(),
                        view.getPaddingRight() + systemBars.right,
                        view.getPaddingBottom()
                );
                Insets inset = Insets.of( 0, systemBars.top, 0, systemBars.bottom);
                return new WindowInsetsCompat.Builder().setInsets(WindowInsetsCompat.Type.systemBars(),inset).build();
            }
        });


        Disposable disposableDetailPaneUpEvents = twoPaneViewModel.detailPaneUpEvents()
                .subscribe(unit -> {
                    if (backPressHandler.isEnabled()) {
                        backPressHandler.handleOnBackPressed();
                    }
                });

        Disposable disposableEditTaskEvents = twoPaneViewModel.editTaskEvents()
                .subscribe(taskId -> {
                    NavController navController = NavHostFragment.findNavController(this);

//                    Bundle args = new NavTaskEditGraphArgs(taskId).toBundle();
//                    navController.navigate(R.id.nav_task_edit_graph, args);
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
    private BehaviorSubject<Boolean> slideableSubject = BehaviorSubject.create();

    public SlidingPaneBackPressHandler(SlidingPaneLayout slidingPaneLayout) {
        super(false);
        this.slidingPaneLayout = slidingPaneLayout;
        slidingPaneLayout.addPanelSlideListener(this);
//        slidingPaneLayout.doOnLayout(v -> syncState());
    }

    private void syncState() {
        setEnabled(slideableSubject.blockingFirst() && slidingPaneLayout.isOpen());
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
}

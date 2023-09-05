package com.example.trackr.ui.tasks;

import static com.example.trackr.ui.utils.Extentions.requireFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;

import com.example.trackr.NavTaskEditGraphArgs;
import com.example.trackr.R;
import com.example.trackr.TaskDetailGraphDirections;
import com.example.trackr.databinding.MainActivityBinding;
import com.example.trackr.databinding.TasksTwoPaneFragmentBinding;
import com.example.trackr.ui.BaseTwoPaneFragment;
import com.example.trackr.ui.edit.TaskEditViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import dagger.hilt.android.internal.lifecycle.HiltViewModelFactory;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author Rameel Hassan
 * Created 17/08/2023 at 12:54 pm
 */
@AndroidEntryPoint
public class TasksTwoPaneFragment extends BaseTwoPaneFragment {
    private TasksTwoPaneFragmentBinding binding;


    TasksViewModel tasksViewModel;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

   public TasksTwoPaneFragment(){
        super(R.layout.tasks_two_pane_fragment);
    }
    @Override
    public SlidingPaneLayout getSlidingPaneLayout() {
        return binding.slidingPaneLayout;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = TasksTwoPaneFragmentBinding.inflate(inflater, container, false);

        binding.setLifecycleOwner(getActivity());

//        NavController navController = NavHostFragment.findNavController(this);
//
//        NavBackStackEntry backStackEntry = navController.getBackStackEntry(R.id.nav_tasks);

//         tasksViewModel = new ViewModelProvider(backStackEntry, HiltViewModelFactory.create(TasksTwoPaneFragment.class, backStackEntry)).get(TasksViewModel.class);

        tasksViewModel = new ViewModelProvider(getActivity()).get(TasksViewModel.class);

//        binding.setViewModel(tasksViewModel);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        NavController detailNavController = NavHostFragment.findNavController(requireFragment(this,R.id.detail_pane));

        compositeDisposable.add(
                tasksViewModel.getShowTaskDetailEvents()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(event -> {
                            if (event.isNewSelection()) {
                                // Change the detail pane contents.
                                detailNavController.navigate(TaskDetailGraphDirections.toTaskDetail(event.getTaskId()));
                            }
                            if (event.isUserSelection()) {
                                // Slide the detail pane into view. If both panes are visible, this has no
                                // visible effect.
                                binding.slidingPaneLayout.openPane();
                            }
                        })
        );


    }
}

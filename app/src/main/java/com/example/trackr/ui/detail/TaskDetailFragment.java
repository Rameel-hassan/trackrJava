package com.example.trackr.ui.detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.trackr.R;
import com.example.trackr.databinding.TaskDetailFragmentBinding;
import com.example.trackr.shared.db.views.TaskDetail;
import com.example.trackr.shared.utils.DateTimeUtils;
import com.example.trackr.ui.DataBindingsUtils;
import com.example.trackr.ui.TwoPaneViewModel;
import com.google.android.material.button.MaterialButton;

import java.time.Clock;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author Rameel Hassan
 * Created 23/08/2023 at 2:14 pm
 */

@AndroidEntryPoint
public class TaskDetailFragment extends Fragment {


    TaskDetailViewModel taskDetailViewModel;


    TwoPaneViewModel twoPaneViewModel;
    private TaskDetailFragmentArgs args;
    private TaskDetailFragmentBinding binding;

    @Inject
    Clock clock;


    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        args = TaskDetailFragmentArgs.fromBundle(getArguments());

        twoPaneViewModel = new ViewModelProvider(requireActivity()).get(TwoPaneViewModel.class);
        taskDetailViewModel = new ViewModelProvider(this).get(TaskDetailViewModel.class);



        binding = TaskDetailFragmentBinding.inflate(inflater, container, false);


        binding.setViewModel(taskDetailViewModel);
        binding.setLifecycleOwner(this);
        binding.setClock(clock);

        binding.toolbar.setNavigationOnClickListener(v -> twoPaneViewModel.onDetailPaneNavigateUp());

        MenuItem item = binding.toolbar.getMenu().findItem(R.id.action_edit);


            MaterialButton button = (MaterialButton) item.getActionView();
            button.setText(item.getTitle());
            button.setIcon(item.getIcon());
            button.setOnClickListener(v -> twoPaneViewModel.onEditTask(args.getTaskId()));



        taskDetailViewModel.getDetailLiveData().observe(getViewLifecycleOwner(), taskDetail -> {
            updateContentDescriptions(taskDetail);
        });

        taskDetailViewModel.setTaskId(args.getTaskId());
        binding.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                twoPaneViewModel.onEditTask(args.getTaskId());
            }
        });
        binding.star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskDetailViewModel.toggleTaskStarState();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateContentDescriptions(TaskDetail taskDetail) {
        if (taskDetail != null) {
            binding.dueAt.setContentDescription(getResources().getString(
                    R.string.due_date_with_value,
                    DateTimeUtils.formattedDate(getResources(), taskDetail.getDueAt(), clock)
            ));

            binding.createdAt.setContentDescription(getResources().getString(
                    R.string.creation_date_with_value,
                    DateTimeUtils.formattedDate(getResources(), taskDetail.getCreatedAt(), clock)
            ));

            binding.owner.setContentDescription(getResources().getString(
                    R.string.owner_with_value, taskDetail.getOwner().getUsername()
            ));

            binding.creator.setContentDescription(getResources().getString(
                    R.string.creator_with_value, taskDetail.getCreator().getUsername()
            ));
        }
    }
}
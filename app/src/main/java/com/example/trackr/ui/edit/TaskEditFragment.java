package com.example.trackr.ui.edit;


/**
 * @author Rameel Hassan
 * Created 17/08/2023 at 12:53 pm
 */


import android.app.Dialog;
import android.os.Bundle;
import android.view.ContextThemeWrapper;


import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.trackr.NavTaskEditGraphArgs;
import com.example.trackr.R;
import com.example.trackr.databinding.TaskEditFragmentBinding;
import com.example.trackr.shared.db.tables.TaskStatus;
import com.example.trackr.shared.utils.DateTimeUtils;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public class TaskEditFragment extends DialogFragment {

    private TaskEditViewModel viewModel;
    @Inject
    Clock clock;
    private NavTaskEditGraphArgs args;
//    private Spinner statusSpinner;
    private CompositeDisposable disposables = new CompositeDisposable();
    TaskEditFragmentBinding binding;

    private static final String FRAGMENT_DATE_PICKER = "date_picker";
    public TaskEditFragment() {
        super(R.layout.task_edit_fragment);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        args = NavTaskEditGraphArgs.fromBundle(requireArguments());
        viewModel = new ViewModelProvider(this).get(TaskEditViewModel.class);

        viewModel.setTaskId(args.getTaskId());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Long taskId = args.getTaskId();
        viewModel.taskId = taskId;


        ContextThemeWrapper themedContext = new ContextThemeWrapper(requireContext(), R.style.ThemeOverlay_Trackr_TaskEdit);
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(themedContext);
        Dialog dialog = dialogBuilder
                .setCancelable(false)
                .setOnKeyListener((dialogInterface, i, keyEvent) -> {
                    if (i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                        close();
                        return true;
                    }
                    return false;
                }).create();


//        dialog.setContentView(R.layout.task_edit_fragment);

        Window window = dialog.getWindow();
        if (window != null) {
            WindowCompat.setDecorFitsSystemWindows(window, false);
        }

        binding = TaskEditFragmentBinding.inflate(dialog.getLayoutInflater());

        binding.setViewModel(viewModel);
        binding.setClock(clock);
        binding.setLifecycleOwner(this);



        binding.toolbar.setTitle(taskId == 0L ? R.string.new_task : R.string.edit_task);
        binding.toolbar.setNavigationOnClickListener(view1 -> close());
        binding.toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId())
            {
                case R.id.action_save:
                    if (binding.content.title.getText().toString().isEmpty()) {
                        binding.content.title.setError(getResources().getString(R.string.missing_title_error));
                    } else {
                        viewModel.save(success -> {
                            if (success) {
                                findNavController().popBackStack();
                            }
                        });
                    }
                    return true;
                default:
                    return false;
            }
        });
//
        MenuItem menuItemSave = binding.toolbar.getMenu().findItem(R.id.action_save);
        binding.content.status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                viewModel.updateState(TaskStatus.values()[i]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.content.tagContainer.setOnClickListener(view12 -> findNavController().navigate(R.id.nav_tag_selection));
        binding.content.owner.setOnClickListener ( view13 -> findNavController().navigate(R.id.nav_user_selection));

        ArrayAdapter adapter= new ArrayAdapter(
                requireContext(),
                R.layout.status_spinner_item,
                R.id.status_text,
                Arrays.stream(TaskStatus.values()).map(TaskStatus::getStringResId).collect(Collectors.toList())
        );
        binding.content.status.setAdapter(adapter);

//
//        TaskStatus initialStatus = viewModel.getStatus().getValue();
//        if (initialStatus != null) {
//            int initialPosition = initialStatus.ordinal();
//            statusSpinner.setSelection(initialPosition);
//        }

        Disposable disposable1 = viewModel.modified
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(modified -> {
                    if (menuItemSave != null) {
                        menuItemSave.setVisible(modified);
                    }
                });
        disposables.add(disposable1);

        Disposable disposable2 = viewModel.getStatus()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(status -> {
                    if (status != null) {
                        int position = status.ordinal();
                        binding.content.status.setSelection(position);
                    }
                });
        disposables.add(disposable2);
        Disposable disposable3 = viewModel.owner
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(owner -> {
                    if (owner != null) {
                        binding.content.owner.setContentDescription(getResources().getString(R.string.owner_with_value, owner.username));
                    }
                });
        disposables.add(disposable3);


        Disposable disposable4 = viewModel.dueAt
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dueAt -> {
                    binding.content.dueAt.setContentDescription(getResources().getString(R.string.due_date_with_value, DateTimeUtils.formattedDate(getResources(), dueAt, clock)));
                    binding.content.dueAt.setOnClickListener(view -> {
                       MaterialDatePicker<Long> m = MaterialDatePicker.Builder.datePicker().build();
                       m.addOnPositiveButtonClickListener(selection -> viewModel.updateDueAt(Instant.ofEpochMilli(selection)));
                        m.show(getChildFragmentManager(), FRAGMENT_DATE_PICKER);
                    });
                });
        disposables.add(disposable4);

        Disposable disposable5 = viewModel.discarded
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(discarded -> {
                    findNavController().popBackStack(R.id.nav_task_edit_graph, true);
                });
        disposables.add(disposable5);



        dialog.setContentView(binding.getRoot());

        return dialog;
    }
    public NavController findNavController(){
        return NavHostFragment.findNavController(this);
    }

    private void close() {
        if (viewModel.modified.getValue()) {
            NavHostFragment.findNavController(this).navigate(R.id.nav_discard_confirmation);
        } else {
            NavHostFragment.findNavController(this).popBackStack(R.id.nav_task_edit_graph, true);
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.dispose();
    }
}

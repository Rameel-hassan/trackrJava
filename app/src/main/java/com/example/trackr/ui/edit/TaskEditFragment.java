package com.example.trackr.ui.edit;

import androidx.fragment.app.Fragment;

/**
 * @author Rameel Hassan
 * Created 17/08/2023 at 12:53 pm
 */
import android.annotation.SuppressLint;
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

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import androidx.navigation.fragment.NavHostFragment;

import com.example.trackr.R;
import com.example.trackr.databinding.TaskEditFragmentBinding;
import com.example.trackr.shared.db.tables.TaskStatus;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.Clock;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public class TaskEditFragment extends DialogFragment {

    private TaskEditViewModel viewModel;
    @Inject
    private Clock clock;
    private NavTaskEditGraphArgs args;
    private Spinner statusSpinner;
    private MenuItem menuItemSave;
    private CompositeDisposable disposables = new CompositeDisposable();
    TaskEditFragmentBinding binding;

    private static final String FRAGMENT_DATE_PICKER = "date_picker";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(TaskEditViewModel.class);
        args = NavTaskEditGraphArgs.fromBundle(requireArguments());
        viewModel.setTaskId(args.getTaskId());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        ContextThemeWrapper themedContext = new ContextThemeWrapper(requireContext(), R.style.ThemeOverlay_Trackr_TaskEdit);
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(themedContext);

        Dialog dialog = dialogBuilder.setCancelable(false).create();
        dialog.setContentView(R.layout.task_edit_fragment);

        Window window = dialog.getWindow();
        if (window != null) {
            WindowCompat.setDecorFitsSystemWindows(window, false);
        }

        initViews(dialog);
        setupObservers();

        return dialog;
    }

    private void initViews(Dialog dialog) {
        View view = dialog.findViewById(android.R.id.content);
        if (view == null) return;
        binding = TaskEditFragmentBinding.inflate(dialog.getLayoutInflater());
        binding.setViewModel(viewModel);
        binding.setClock(clock);
        binding.setLifecycleOwner(this);



//        Toolbar toolbar = view.findViewById(R.id.toolbar);
//        toolbar.setTitle(args.getTaskId() == 0L ? R.string.new_task : R.string.edit_task);
//        toolbar.setNavigationOnClickListener(v -> close());
//
        binding.


        menuItemSave = toolbar.getMenu().findItem(R.id.action_save);
        statusSpinner = view.findViewById(R.id.status_spinner);

        // Initialize other views and set up event listeners
        // ...

        // Set initial spinner selection
        TaskStatus initialStatus = viewModel.getStatus().getValue();
        if (initialStatus != null) {
            int initialPosition = initialStatus.ordinal();
            statusSpinner.setSelection(initialPosition);
        }
    }

    private void setupObservers() {
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
                        statusSpinner.setSelection(position);
                    }
                });
        disposables.add(disposable2);

        // Set up other observers
        // ...
    }

    // Rest of the code...

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

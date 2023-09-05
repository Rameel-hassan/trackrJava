package com.example.trackr.ui.tasks;

/**
 * @author Rameel Hassan
 * Created 28/08/2023 at 12:06 pm
 */
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackr.R;
import com.example.trackr.databinding.TasksFragmentBinding;
import com.example.trackr.shared.db.views.TaskSummary;
import com.example.trackr.ui.utils.EdgeToEdgeViewUtils;
import com.google.android.material.snackbar.Snackbar;
import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.jetbrains.annotations.NotNull;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

@AndroidEntryPoint
public class TasksFragment extends Fragment implements TasksAdapter.ItemListener {

    private TasksViewModel tasksViewModel;
    private TasksFragmentBinding binding;
    private TasksAdapter tasksAdapter;


    @Inject
    Clock clock;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @SuppressLint("ShowToast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = TasksFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tasksViewModel = new ViewModelProvider(getActivity()).get(TasksViewModel.class);

        tasksAdapter = new TasksAdapter(this,clock);

        binding.setListener(this);
        binding.setLifecycleOwner(getActivity());

        RecyclerView recyclerView = binding.tasksList;

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeAndDragCallback());
        itemTouchHelper.attachToRecyclerView(recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(tasksAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                updateStickyHeader();
            }
        });

        EdgeToEdgeViewUtils.doOnApplyWindowInsets(binding.tasksList, new EdgeToEdgeViewUtils.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat insets, Insets padding, Insets margins) {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

                if (binding.bottomAppBar != null) {
                    View finalBottomBar = binding.bottomAppBar;
                    finalBottomBar.post(new Runnable() {
                        @Override
                        public void run() {
                            view.setPadding(
                                    padding.left + systemBars.left,
                                    padding.top,
                                    padding.right + systemBars.right,
                                    systemBars.bottom + finalBottomBar.getHeight()

                            );
                        }
                    });


                }else{
                    view.setPadding(
                            view.getPaddingLeft() + systemBars.left,
                            view.getPaddingTop(),
                            view.getPaddingRight() + systemBars.right,
                            padding.bottom + systemBars.bottom
                    );
                }

                Insets inset = Insets.of( 0, systemBars.top, 0, systemBars.bottom);
                return new WindowInsetsCompat.Builder().setInsets(WindowInsetsCompat.Type.systemBars(),inset).build();
            }
        });

        binding.add.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_task_edit_graph));
        binding.bottomAppBar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
//                case R.id.archive:
//                    Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_archives);
//                    return true;
                case R.id.settings:
                    Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_settings);
                    return true;
                default:
                    return false;
            }
        });

        Disposable listItemsDisposable = tasksViewModel.getListItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(items -> tasksAdapter.submitList(items, this::updateStickyHeader));
        compositeDisposable.add(listItemsDisposable);



        Disposable archivedItemDisposable = tasksViewModel.archivedItem
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArchivedItem>() {
                    @Override
                    public void accept(ArchivedItem item) throws Throwable {
                        Snackbar.make(binding.coordinator, getString(R.string.task_archived), Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.undo), v -> tasksViewModel.unarchiveTask(item))
                                .setAnchorView(binding.add)
                                .show();
                    }
                });
        compositeDisposable.add(archivedItemDisposable);

        Disposable undoReorderTasksDisposable =tasksViewModel.undoReorderTasks
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UndoReorderTasks>() {
                    @Override
                    public void accept(UndoReorderTasks undo) throws Throwable {
                        Snackbar.make(binding.coordinator, R.string.task_position_changed, Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.undo), v -> tasksViewModel.undoReorderTasks(undo))
                                .setAnchorView(binding.add)
                                .show();
                    }
                });
        compositeDisposable.add(undoReorderTasksDisposable);
    }

    private void updateStickyHeader() {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.tasksList.getLayoutManager();
        ListItem.TypeHeader headerItem = tasksAdapter.findHeaderItem(linearLayoutManager.findFirstVisibleItemPosition());
        if (headerItem != null) {
            binding.setHeaderData(headerItem.getHeaderData());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        compositeDisposable.clear();
        binding = null;
    }

    @Override
    public void onStarClicked(TaskSummary taskSummary) {
        tasksViewModel.toggleTaskStarState(taskSummary);
    }

    @Override
    public void onHeaderClicked(HeaderData headerData) {
        tasksViewModel.toggleExpandedState(headerData);
    }

    @Override
    public void onTaskClicked(TaskSummary taskSummary) {
        tasksViewModel.showTaskDetail(taskSummary,true);
    }

    @Override
    public void onTaskArchived(TaskSummary taskSummary) {
        tasksViewModel.archiveTask(taskSummary);
    }

    @Override
    public void onTaskDragged(int fromPosition,int toPosition) {
        tasksAdapter.changeTaskPosition(fromPosition, toPosition);
    }


    @Override
    public void onDragStarted() {

    }

    @Override
    public void onDragCompleted(
            int fromPosition,
            int toPosition,
            boolean usingDragAndDropCustomActions
    ) {
        // If using a custom action for drag and drop, the current list tasksAdapter.currentList
        // will return the original list. In that case, swap the items in the returned list.
        List<ListItem> list = new ArrayList<>(tasksAdapter.getCurrentList());
        if (usingDragAndDropCustomActions) {
            Collections.swap(list, fromPosition, toPosition);
        }

        // The item dragged and moved.
        ListItem.TypeTask draggedItem = (ListItem.TypeTask) list.get(toPosition);

        // The item on the other end of the range shifted by the movement.
        ListItem.TypeTask targetItem;
        if (fromPosition < toPosition) {
            targetItem = (ListItem.TypeTask) list.get(toPosition - 1);
        } else {
            targetItem = (ListItem.TypeTask) list.get(toPosition + 1);
        }

        tasksViewModel.reorderTasks(draggedItem.getTaskSummary(), targetItem.getTaskSummary());
    }

    // Implement other overridden methods here...
}

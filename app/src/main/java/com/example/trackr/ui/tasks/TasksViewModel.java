package com.example.trackr.ui.tasks;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.trackr.shared.db.tables.TaskStatus;
import com.example.trackr.shared.db.tables.User;
import com.example.trackr.shared.db.views.TaskSummary;
import com.example.trackr.shared.usecase.ArchiveUseCase;
import com.example.trackr.shared.usecase.GetOngoingTaskSummariesUseCase;
import com.example.trackr.shared.usecase.ReorderTasksUseCase;
import com.example.trackr.shared.usecase.ToggleTaskStarStateUseCase;
import com.example.trackr.shared.usecase.UnarchiveUseCase;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.Closeable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.processors.BehaviorProcessor;
import io.reactivex.rxjava3.processors.PublishProcessor;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.PublishSubject;

/**
 * @author Rameel Hassan
 * Created 23/08/2023 at 5:39 pm
 */
@HiltViewModel
public class TasksViewModel extends ViewModel {


    private GetOngoingTaskSummariesUseCase getOngoingTaskSummariesUseCase;
    private ToggleTaskStarStateUseCase toggleTaskStarStateUseCase;
    private ReorderTasksUseCase reorderTasksUseCase;
    private ArchiveUseCase archiveUseCase;
    private UnarchiveUseCase unarchiveUseCase;
    private User currentUser;


    private final BehaviorSubject<ArchivedItem> archivedItemSubject = BehaviorSubject.create();
    public Flowable<ArchivedItem> archivedItem = archivedItemSubject.toFlowable(BackpressureStrategy.DROP);

    private final BehaviorSubject<UndoReorderTasks> undoReorderTasksSubject = BehaviorSubject.create();
    public Flowable<UndoReorderTasks> undoReorderTasks = undoReorderTasksSubject.toFlowable(BackpressureStrategy.DROP);

    private Long detailTaskId = null;

    private final PublishSubject<ShowTaskDetailEvent> showTaskDetailProcessor = PublishSubject.create();
    public Flowable<ShowTaskDetailEvent> getShowTaskDetailEvents() {
        return showTaskDetailProcessor.toFlowable(BackpressureStrategy.LATEST);
    }

    BehaviorProcessor<Map<TaskStatus, Boolean>> expandedStatesMapProcessor;
    private final Map<TaskStatus, Boolean> expandedStatesMap = new HashMap<>();


    private final Flowable<List<TaskSummary>> taskSummaries;
    private final Flowable<List<ListItem>> listItems;


    @Inject
    public TasksViewModel(GetOngoingTaskSummariesUseCase getOngoingTaskSummariesUseCase, ToggleTaskStarStateUseCase toggleTaskStarStateUseCase, ReorderTasksUseCase reorderTasksUseCase, ArchiveUseCase archiveUseCase, UnarchiveUseCase unarchiveUseCase, User currentUser) {
//        super(closeables);
        this.getOngoingTaskSummariesUseCase = getOngoingTaskSummariesUseCase;
        this.toggleTaskStarStateUseCase = toggleTaskStarStateUseCase;
        this.reorderTasksUseCase = reorderTasksUseCase;
        this.archiveUseCase = archiveUseCase;
        this.unarchiveUseCase = unarchiveUseCase;
        this.currentUser = currentUser;



        taskSummaries = getOngoingTaskSummariesUseCase.invoke(currentUser.getId())
                .subscribeOn(Schedulers.io());


        for (TaskStatus status : TaskStatus.values()) {
            expandedStatesMap.put(status, true);
        }

        expandedStatesMapProcessor = BehaviorProcessor.createDefault(expandedStatesMap);

        listItems = Flowable.combineLatest(taskSummaries, expandedStatesMapProcessor,
                        (taskSummaries, statesMap) -> {
                            ListItemsCreator listItemsCreator = new ListItemsCreator(taskSummaries, statesMap);
                            List<ListItem> items = listItemsCreator.execute();
                            if (detailTaskId == null && !taskSummaries.isEmpty()) {
                                for (ListItem item : items) {
                                    if (item instanceof ListItem.TypeTask) {
                                        ListItem.TypeTask typeTask = (ListItem.TypeTask) item;
                                        showTaskDetail(typeTask.getTaskSummary(), false);
                                        break;
                                    }
                                }
                            }
                            return items;
                        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .share();

    }

    public Flowable<List<ListItem>> getListItems() {
        return listItems.observeOn(Schedulers.computation());
    }




    public void toggleExpandedState(HeaderData headerData) {
        expandedStatesMapProcessor
                .take(1)
                .map(map -> {
                    Map<TaskStatus, Boolean> updatedMap = new HashMap<>(map);
                    Boolean previous = map.get(headerData.getTaskStatus());
                    if (previous != null) {
                        updatedMap.put(headerData.getTaskStatus(), !previous);
                    }
                    return updatedMap;
                })
                .subscribe(expandedStatesMapProcessor::onNext);
    }

    public void toggleTaskStarState(TaskSummary taskSummary) {
        try {
            toggleTaskStarStateUseCase.invoke(taskSummary.getId(), currentUser)
                   .subscribeOn(Schedulers.io())
                     .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(new CompletableObserver() {
                       @Override
                       public void onSubscribe(@NonNull Disposable d) {
                           Log.d("TasksViewModel", "onSubscribe: ");
                       }

                       @Override
                       public void onComplete() {
                           Log.d("TasksViewModel", "onComplete: ");
                       }

                       @Override
                       public void onError(@NonNull Throwable e) {
                           Log.d("TasksViewModel", "onError: ");
                       }
                   });
        } catch (Exception e) {
            Log.d("TasksViewModel", "toggleTaskStarState: "+e.getMessage());
        }
    }


    public Completable archiveTask(TaskSummary taskSummary) {
        return Completable.concatArray(
                Completable.fromAction(() -> archiveUseCase.invoke(Collections.singletonList(taskSummary.getId()))),
                Single.fromCallable(() -> {
                    archivedItemSubject.onNext(new ArchivedItem(taskSummary.getId()));
                    return true;
                }).flatMapCompletable(value -> Completable.complete())
        ).subscribeOn(Schedulers.io());
    }


    public Completable unarchiveTask(ArchivedItem item) {
        return Completable.fromAction(() -> unarchiveUseCase.invoke(Collections.singletonList(item.getTaskId())))
                .subscribeOn(Schedulers.io());
    }

    public Completable reorderTasks(TaskSummary movedTask, TaskSummary targetTask) {
        if (!movedTask.status.equals(targetTask.status)) {
            return Completable.complete(); // Return a completed Completable if statuses don't match
        }

        return Completable.fromAction(() -> {
            reorderTasksUseCase.invoke(
                    movedTask.getId(),
                    movedTask.getStatus(),
                    movedTask.getOrderInCategory(),
                    targetTask.getOrderInCategory()
            );

            undoReorderTasksSubject.onNext(
                    new UndoReorderTasks(
                            movedTask.getId(),
                            movedTask.getStatus(),
                            targetTask.getOrderInCategory(),
                            movedTask.getOrderInCategory()
                    )
            );
        }).subscribeOn(Schedulers.io());
    }


    public Completable undoReorderTasks(UndoReorderTasks undo) {
        return Completable.fromAction(() -> {
            reorderTasksUseCase.invoke(
                    undo.taskId,
                    undo.status,
                    undo.currentOrderInCategory,
                    undo.targetOrderInCategory
            );
        }).subscribeOn(Schedulers.io());
    }

public void showTaskDetail(TaskSummary taskSummary, Boolean isUserSelection) {
    showTaskDetailProcessor.onNext(new ShowTaskDetailEvent(taskSummary.id, taskSummary.id != detailTaskId, isUserSelection));
        detailTaskId = taskSummary.id;
    }
}




    class ArchivedItem {
        private final long taskId;

        public ArchivedItem(long taskId) {
            this.taskId = taskId;
        }

        public long getTaskId() {
            return taskId;
        }
    }

    class UndoReorderTasks {
         final long taskId;
         final TaskStatus status;
         final int currentOrderInCategory;
         final int targetOrderInCategory;

        public UndoReorderTasks(
                long taskId,
                TaskStatus status,
                int currentOrderInCategory,
                int targetOrderInCategory
        ) {
            this.taskId = taskId;
            this.status = status;
            this.currentOrderInCategory = currentOrderInCategory;
            this.targetOrderInCategory = targetOrderInCategory;
        }

        public long getTaskId() {
            return taskId;
        }

        public TaskStatus getStatus() {
            return status;
        }

        public int getCurrentOrderInCategory() {
            return currentOrderInCategory;
        }

        public int getTargetOrderInCategory() {
            return targetOrderInCategory;
        }
    }

    class ShowTaskDetailEvent {
        private final long taskId;
        private final boolean isNewSelection;
        private final boolean isUserSelection;

        public ShowTaskDetailEvent(long taskId) {
            this(taskId, true, true);
        }

        public ShowTaskDetailEvent(long taskId, boolean isNewSelection, boolean isUserSelection) {
            this.taskId = taskId;
            this.isNewSelection = isNewSelection;
            this.isUserSelection = isUserSelection;
        }

        public long getTaskId() {
            return taskId;
        }

        public boolean isNewSelection() {
            return isNewSelection;
        }

        public boolean isUserSelection() {
            return isUserSelection;
        }

}

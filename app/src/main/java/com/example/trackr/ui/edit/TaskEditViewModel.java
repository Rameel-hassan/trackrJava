package com.example.trackr.ui.edit;

import androidx.lifecycle.ViewModel;

import com.example.trackr.shared.db.tables.Tag;
import com.example.trackr.shared.db.tables.TaskStatus;
import com.example.trackr.shared.db.tables.User;
import com.example.trackr.shared.db.views.TaskDetail;
import com.example.trackr.shared.usecase.LoadTagsUseCase;
import com.example.trackr.shared.usecase.LoadTaskDetailUseCase;
import com.example.trackr.shared.usecase.LoadUsersUseCase;
import com.example.trackr.shared.usecase.SaveTaskDetailUseCase;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.PublishSubject;

/**
 * @author Rameel Hassan
 * Created 15/08/2023 at 12:39 pm
 */
@HiltViewModel
public class TaskEditViewModel extends ViewModel {

    private LoadTaskDetailUseCase loadTaskDetailUseCase;
    private LoadUsersUseCase loadUsersUseCase;
    private LoadTagsUseCase loadTagsUseCase;
    private SaveTaskDetailUseCase saveTaskDetailUseCase;
    private User currentUser;
    Long taskId = 0L;

    @Inject
    public TaskEditViewModel(LoadTaskDetailUseCase loadTaskDetailUseCase, LoadUsersUseCase loadUsersUseCase, LoadTagsUseCase loadTagsUseCase, SaveTaskDetailUseCase saveTaskDetailUseCase, User currentUser) {
        this.loadTaskDetailUseCase = loadTaskDetailUseCase;
        this.loadUsersUseCase = loadUsersUseCase;
        this.loadTagsUseCase = loadTagsUseCase;
        this.saveTaskDetailUseCase = saveTaskDetailUseCase;
        this.currentUser = currentUser;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
        loadInitialData(taskId);
    }
    private void loadInitialData(long taskId) {
        Disposable disposable = Single.fromCallable(() -> {
                    users = loadUsersUseCase.invoke();
                    allTags = loadTagsUseCase.invoke();
                    if (taskId != 0L) {
                        TaskDetail detail = loadTaskDetailUseCase.invoke(taskId).blockingGet();
                        if (detail != null) {
                            initialTitle = detail.getTitle();
                            initialDescription = detail.getDescription();
                            _title.onNext(initialTitle);
                            _description.onNext(initialDescription);
                            _status.onNext(detail.getStatus());
                            _owner.onNext(detail.getOwner());
                            _creator.onNext(detail.getCreator());
                            _dueAt.onNext(detail.getDueAt());
                            _createdAt.onNext(detail.getCreatedAt());
                            orderInCategory = detail.getOrderInCategory();
                            isArchived = detail.isArchived;
                            topInCategory = false;
                            _tags.onNext(detail.getTags());
                            starUsers.clear();
                            starUsers.addAll(detail.getStarUsers());
                            _modified.onNext(false);
                        }
                    }
                    return null;
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }
    private BehaviorSubject<String> _title = BehaviorSubject.createDefault("");
    public String title =_title.getValue();

    private String initialTitle = "";

    private BehaviorSubject<String> _description = BehaviorSubject.createDefault("");
    public String description = _description.getValue();

    private String initialDescription = "";

    private BehaviorSubject<TaskStatus> _status = BehaviorSubject.createDefault(TaskStatus.NOT_STARTED);

    public BehaviorSubject<TaskStatus> getStatus() {
        return _status;
    }

    public void setStatus(TaskStatus status) {
        _status.onNext(status);
    }

    private BehaviorSubject<User> _owner = BehaviorSubject.createDefault(currentUser);
    public BehaviorSubject<User> owner = _owner;

    private BehaviorSubject<User> _creator = BehaviorSubject.createDefault(currentUser);

    private BehaviorSubject<Instant> _dueAt = BehaviorSubject.createDefault(Instant.now().plus(Duration.ofDays(7)));
    public BehaviorSubject<Instant> dueAt = _dueAt;
    private BehaviorSubject<Instant> _createdAt = BehaviorSubject.createDefault(Instant.now());

    private int orderInCategory = 0;
    private boolean topInCategory = true;
    private boolean isArchived = false;

    private BehaviorSubject<List<Tag>> _tags = BehaviorSubject.createDefault(new ArrayList<>());
    public List<Tag> tags = _tags.getValue();

    private List<User> starUsers = new ArrayList<>();

    public Flowable<List<User>> users;
    public Flowable<List<Tag>> allTags;

    public PublishSubject<Object> discarded = PublishSubject.create();

    private BehaviorSubject<Boolean> _modified = BehaviorSubject.createDefault(false);
    public BehaviorSubject<Boolean> modified = _modified;

    private Disposable modifiedTitleDescriptionJob;

    public Flowable<List<User>> getUsers() {
        return users;
    }

    public void setUsers(Flowable<List<User>> users) {
        this.users = users;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        _tags.onNext(tags);
    }

    public Flowable<List<Tag>> getAllTags() {
        return allTags;
    }

    public void setAllTags(Flowable<List<Tag>> allTags) {
        this.allTags = allTags;
    }

    public TaskEditViewModel() {
        modifiedTitleDescriptionJob = Flowable.combineLatest(
                        _title.toFlowable(BackpressureStrategy.LATEST),
                        _description.toFlowable(BackpressureStrategy.LATEST),
                        new BiFunction<String, String, Boolean>() {
                            @Override
                            public Boolean apply(String title, String description) throws Throwable {
                                return !title.equals(initialTitle) || !description.equals(initialDescription);
                            }
                        }
                )
                .subscribeOn(Schedulers.io())
                .subscribe(modified::onNext);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        modifiedTitleDescriptionJob.dispose();
    }

    public void updateState(TaskStatus newStatus) {
        if (!_status.getValue().equals(newStatus)) {
            _status.onNext(newStatus);
            _modified.onNext(true);
            topInCategory = true;
        }
    }

    public void updateOwner(User newUser) {
        _owner.onNext(newUser);
        _modified.onNext(true);
    }

    public void updateDueAt(Instant newDueAt) {
        _dueAt.onNext(newDueAt);
        _modified.onNext(true);
    }

    public void addTag(Tag newTag) {
        List<Tag> currentTags = _tags.getValue();
        if (!currentTags.contains(newTag)) {
            currentTags.add(newTag);
            _tags.onNext(currentTags);
            _modified.onNext(true);
        }
    }

    public void removeTag(Tag tagToRemove) {
        List<Tag> currentTags = _tags.getValue();
        if (currentTags.contains(tagToRemove)) {
            currentTags.remove(tagToRemove);
            _tags.onNext(currentTags);
            _modified.onNext(true);
        }
    }

    public void save(OnSaveFinishedListener onSaveFinishedListener) {
        Disposable disposable = io.reactivex.rxjava3.core.Single.fromCallable(() -> {
                    TaskDetail taskDetail = new TaskDetail(
                            taskId,
                            _title.getValue(),
                            _description.getValue(),
                            _status.getValue(),
                            _createdAt.getValue(),
                            _dueAt.getValue(),
                            orderInCategory,
                            isArchived,
                            _owner.getValue(),
                            _creator.getValue(),
                            _tags.getValue(),
                            starUsers
                    );
                    saveTaskDetailUseCase.invoke(taskDetail, topInCategory);
                    return true;
                })
                .subscribeOn(Schedulers.io())
                .subscribe(
                        result -> onSaveFinishedListener.onSaveFinished(true),
                        throwable -> {
                            throwable.printStackTrace();
                            onSaveFinishedListener.onSaveFinished(false);
                        }
                );
    }

    public void discardChanges() {
        discarded.onNext(new Object());
    }


    // Define the interface for the listener
    public interface OnSaveFinishedListener {
        void onSaveFinished(boolean success);
    }
}



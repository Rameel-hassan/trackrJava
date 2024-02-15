package com.example.trackr.ui.detail;

/**
 * @author Rameel Hassan
 * Created 28/08/2023 at 3:29 pm
 */

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.trackr.shared.db.tables.User;
import com.example.trackr.shared.db.views.TaskDetail;
import com.example.trackr.shared.usecase.FindTaskDetailUseCase;
import com.example.trackr.shared.usecase.ToggleTaskStarStateUseCase;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

@HiltViewModel
public class TaskDetailViewModel extends ViewModel {

    private final FindTaskDetailUseCase findTaskDetailUseCase;
    private final ToggleTaskStarStateUseCase toggleTaskStarStateUseCase;
    private final User currentUser;
    private final MutableLiveData<TaskDetail> detailLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> starredLiveData = new MutableLiveData<>();
    private final BehaviorSubject<Long> taskIdSubject = BehaviorSubject.createDefault(0L);

    private final CompositeDisposable disposables = new CompositeDisposable();

    @Inject
    public TaskDetailViewModel(
            FindTaskDetailUseCase findTaskDetailUseCase,
            ToggleTaskStarStateUseCase toggleTaskStarStateUseCase,
            User currentUser) {

        this.findTaskDetailUseCase = findTaskDetailUseCase;
        this.toggleTaskStarStateUseCase = toggleTaskStarStateUseCase;
        this.currentUser = currentUser;

        Disposable disposable = taskIdSubject.switchMap(id -> this.findTaskDetailUseCase.invoke(id).toObservable())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(detail -> {
                    if (detail != null) {
                        detailLiveData.postValue(detail);
                        boolean contains = containsId(detail.getStarUsers(), currentUser.getId());
                        boolean hasStar = detail.getStarUsers() != null;
                        starredLiveData.postValue(contains && hasStar);
                    }
                });
        disposables.add(disposable);
    }

    public LiveData<TaskDetail> getDetailLiveData() {
        return detailLiveData;
    }

    public LiveData<Boolean> getStarredLiveData() {
        return starredLiveData;
    }

    public void setTaskId(long taskId) {
        taskIdSubject.onNext(taskId);
    }

    public void toggleTaskStarState() {
        Long id = taskIdSubject.getValue();
        if (id == null || id <= 0L) return;
        Disposable disposable = toggleTaskStarStateUseCase.invoke(id, currentUser)
                .subscribeOn(Schedulers.io())
                .subscribe();
        disposables.add(disposable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }

    private boolean containsId(final List<User> list, final Long id) {
        return list != null && list.stream().map(User::getId).anyMatch(id::equals);
    }
}

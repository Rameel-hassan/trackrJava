package com.example.trackr.ui.detail;

/**
 * @author Rameel Hassan
 * Created 28/08/2023 at 3:29 pm
 */
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.trackr.shared.db.tables.User;
import com.example.trackr.shared.db.views.TaskDetail;
import com.example.trackr.shared.usecase.FindTaskDetailUseCase;
import com.example.trackr.shared.usecase.ToggleTaskStarStateUseCase;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;



@HiltViewModel
public class TaskDetailViewModel extends ViewModel {

    private final FindTaskDetailUseCase findTaskDetailUseCase;
    private final ToggleTaskStarStateUseCase toggleTaskStarStateUseCase;
    private final User currentUser;

    private final MutableLiveData<Long> taskIdLiveData = new MutableLiveData<>();
    private final MutableLiveData<TaskDetail> detailLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> starredLiveData = new MutableLiveData<>();

    private final CompositeDisposable disposables = new CompositeDisposable();

    @Inject
    public TaskDetailViewModel(
            FindTaskDetailUseCase findTaskDetailUseCase,
            ToggleTaskStarStateUseCase toggleTaskStarStateUseCase,
            User currentUser) {
        this.findTaskDetailUseCase = findTaskDetailUseCase;
        this.toggleTaskStarStateUseCase = toggleTaskStarStateUseCase;
        this.currentUser = currentUser;
    }

    public LiveData<TaskDetail> getDetailLiveData() {
        return detailLiveData;
    }

    public LiveData<Boolean> getStarredLiveData() {
        return starredLiveData;
    }

    public void setTaskId(long taskId) {
        taskIdLiveData.setValue(taskId);

        Disposable disposable = findTaskDetailUseCase.invoke(taskId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<TaskDetail>() {
                            @Override
                            public void accept(TaskDetail detail) throws Throwable {
                              detailLiveData.setValue(detail);
                            }
                        });

        disposables.add(disposable);
    }

    public void toggleTaskStarState() {
        Long id = taskIdLiveData.getValue();
        if (id == null || id <= 0L) return;

        Disposable disposable = Completable.fromCallable(() -> {
                    Log.d("TaskDetailViewModel", "toggleTaskStarState: start");
                    try {
                        toggleTaskStarStateUseCase.invoke(id, currentUser);
                    }catch (Exception e)
                    {
                        Log.d("TaskDetailViewModel", "toggleTaskStarState: call exception " + e.getMessage());

                    }

                    Log.d("TaskDetailViewModel", "toggleTaskStarState: call complete");
                    return true;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action() {
                            @Override
                            public void run() throws Throwable {
                                Log.d("TaskDetailViewModel", "run: working");
                            }
                        }
                );

        disposables.add(disposable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}
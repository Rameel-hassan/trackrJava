package com.example.trackr.ui;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;



import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.processors.BehaviorProcessor;
import io.reactivex.rxjava3.processors.PublishProcessor;


/**
 * @author Rameel Hassan
 * Created 15/03/2023 at 4:00 PM
 */




@HiltViewModel
public class TwoPaneViewModel extends ViewModel {
    public MutableLiveData<Boolean> isTwoPane = new MutableLiveData<>(false);

    @Inject
    public TwoPaneViewModel() {
    }
    public final PublishProcessor<Object> detailPaneUpEventSubject = PublishProcessor.create();
    public final PublishProcessor<Long> editTaskEventSubject = PublishProcessor.create();

    public void setIsTwoPane(boolean twoPane) {
        isTwoPane.setValue(twoPane);
    }

    public void onDetailPaneNavigateUp() {
        detailPaneUpEventSubject.onNext(new Object());
    }

    public void onEditTask(long taskId) {
        editTaskEventSubject.onNext(taskId);
    }
}

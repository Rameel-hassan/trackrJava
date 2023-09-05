package com.example.trackr.shared.usecase;

import com.example.trackr.shared.db.dao.TaskDao;
import com.example.trackr.shared.db.views.TaskDetail;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;


/**
 * @author Rameel Hassan
 * Created 06/03/2023 at 4:21 PM
 */
public class FindTaskDetailUseCase {
    private TaskDao taskDao;

    @Inject
    public FindTaskDetailUseCase(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    public Maybe<TaskDetail> invoke(Long taskId){
       return taskDao.findTaskDetailById(taskId);
    }
}

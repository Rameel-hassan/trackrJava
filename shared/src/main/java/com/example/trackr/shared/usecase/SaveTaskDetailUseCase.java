package com.example.trackr.shared.usecase;

import com.example.trackr.shared.db.dao.TaskDao;
import com.example.trackr.shared.db.views.TaskDetail;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;

/**
 * @author Rameel Hassan
 * Created 06/03/2023 at 4:39 PM
 */
public class SaveTaskDetailUseCase {

    private TaskDao taskDao;

    @Inject
    public SaveTaskDetailUseCase(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

   public void invoke(TaskDetail detail, Boolean topOrderInCategory) {
        taskDao.saveTaskDetail(detail, topOrderInCategory);
    }
}

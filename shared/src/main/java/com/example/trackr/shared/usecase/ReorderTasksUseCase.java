package com.example.trackr.shared.usecase;

import com.example.trackr.shared.db.dao.TaskDao;
import com.example.trackr.shared.db.tables.TaskStatus;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;


/**
 * @author Rameel Hassan
 * Created 06/03/2023 at 4:36 PM
 */
public class ReorderTasksUseCase {


    private TaskDao taskDao;
    @Inject
    public ReorderTasksUseCase(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    void invoke(Long taskId, TaskStatus status, int currentOrderInCategory, int targetOrderInCategory) {
        taskDao.reorderTasks(taskId, status, currentOrderInCategory, targetOrderInCategory);
    }
}

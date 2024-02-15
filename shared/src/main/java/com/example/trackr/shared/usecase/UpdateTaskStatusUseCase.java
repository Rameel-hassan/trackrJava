package com.example.trackr.shared.usecase;

import com.example.trackr.shared.db.dao.TaskDao;
import com.example.trackr.shared.db.tables.TaskStatus;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;

/**
 * @author Rameel Hassan
 * Created 06/03/2023 at 4:53 PM
 */
public class UpdateTaskStatusUseCase {

    private TaskDao taskDao;

    @Inject
    public UpdateTaskStatusUseCase(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

   public Completable invoke(List<Long> taskIds, TaskStatus status) {
        return taskDao.updateTaskStatus(taskIds, status);
    }
}

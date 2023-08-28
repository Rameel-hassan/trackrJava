package com.example.trackr.shared.usecase;

import com.example.trackr.shared.db.dao.TaskDao;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;

/**
 * @author Rameel Hassan
 * Created 06/03/2023 at 4:52 PM
 */
public class UnarchiveUseCase {

    private TaskDao taskDao;

    @Inject
    public UnarchiveUseCase(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

   public Completable invoke(List<Long> taskIds) {
       return taskDao.setIsArchived(taskIds, false);
    }
}

package com.example.trackr.shared.usecase;

import com.example.trackr.shared.db.dao.TaskDao;
import com.example.trackr.shared.db.views.TaskSummary;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Flowable;


/**
 * @author Rameel Hassan
 * Created 06/03/2023 at 4:02 PM
 */
public class ArchivedTaskListItemsUseCase {
    private TaskDao taskDao;

    @Inject
    public ArchivedTaskListItemsUseCase(TaskDao taskDao) {
        this.taskDao = taskDao;
    }
     public Flowable<List<TaskSummary>> invoke(Long userId){
        return taskDao.getArchivedTaskSummaries(userId);
     }

}

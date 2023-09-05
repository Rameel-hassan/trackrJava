package com.example.trackr.shared.usecase;

import androidx.room.Transaction;

import com.example.trackr.shared.db.AppDatabase;
import com.example.trackr.shared.db.dao.TaskDao;
import com.example.trackr.shared.db.tables.Task;
import com.example.trackr.shared.db.tables.User;
import com.example.trackr.shared.db.tables.UserTask;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author Rameel Hassan
 * Created 06/03/2023 at 4:42 PM
 */
public class ToggleTaskStarStateUseCase {
    private AppDatabase db;
    private TaskDao taskDao ;

    @Inject
    public ToggleTaskStarStateUseCase(AppDatabase db, TaskDao taskDao) {
        this.db = db;
        this.taskDao = taskDao;
    }

    @Transaction
  public Completable invoke(Long taskId, User currentUser) {
             UserTask userTask = taskDao.getUserTask(taskId, currentUser.id).blockingFirst();
            if (userTask != null) {
               return taskDao.deleteUserTasks(List.of(userTask));
            } else {
               return taskDao.insertUserTasks(List.of(new UserTask(currentUser.id, taskId)));
            }
    }
}

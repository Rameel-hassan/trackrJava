package com.example.trackr.shared.usecase;

import com.example.trackr.shared.db.dao.TaskDao;
import com.example.trackr.shared.db.tables.User;
import com.example.trackr.shared.db.tables.UserTask;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;

/**
 * @author Rameel Hassan
 * Created 06/03/2023 at 4:42 PM
 */
public class ToggleTaskStarStateUseCase {
    private TaskDao taskDao;
    @Inject
    public ToggleTaskStarStateUseCase(TaskDao taskDao) {
        this.taskDao = taskDao;
    }
    public Completable invoke(Long taskId, User currentUser) {
             return taskDao.getUserTask(taskId, currentUser.id)
                     .flatMapCompletable(userTask -> taskDao.deleteUserTasks(List.of(userTask)))
                     .onErrorResumeNext(throwable -> taskDao.insertUserTasks(List.of(new UserTask(currentUser.id, taskId))));
    }
}

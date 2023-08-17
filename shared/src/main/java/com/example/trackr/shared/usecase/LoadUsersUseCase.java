package com.example.trackr.shared.usecase;

import com.example.trackr.shared.db.dao.TaskDao;
import com.example.trackr.shared.db.tables.User;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Flowable;

/**
 * @author Rameel Hassan
 * Created 06/03/2023 at 4:33 PM
 */
public class LoadUsersUseCase {

    private TaskDao taskDao;

    @Inject
    public LoadUsersUseCase(TaskDao taskDao) {
        this.taskDao = taskDao;
    }


    public Flowable<List<User>> invoke() {
        return taskDao.loadUsers();
     }
}

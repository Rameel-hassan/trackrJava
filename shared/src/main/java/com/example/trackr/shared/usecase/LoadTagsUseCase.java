package com.example.trackr.shared.usecase;

import com.example.trackr.shared.db.dao.TaskDao;
import com.example.trackr.shared.db.tables.Tag;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Flowable;


/**
 * @author Rameel Hassan
 * Created 06/03/2023 at 4:26 PM
 */
public class LoadTagsUseCase {
    private TaskDao taskDao;
    @Inject
    public LoadTagsUseCase(TaskDao taskDao) {
        this.taskDao = taskDao;
    }


   public Flowable<List<Tag>> invoke() {
        return taskDao.loadTags();
     }

}

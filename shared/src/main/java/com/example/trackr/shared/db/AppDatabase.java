package com.example.trackr.shared.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.trackr.shared.db.dao.TaskDao;
import com.example.trackr.shared.db.tables.Tag;
import com.example.trackr.shared.db.tables.Task;
import com.example.trackr.shared.db.tables.TaskTag;
import com.example.trackr.shared.db.tables.User;
import com.example.trackr.shared.db.tables.UserTask;
import com.example.trackr.shared.db.views.TaskDetail;
import com.example.trackr.shared.db.views.TaskSummary;

/**
 * @author Rameel Hassan
 * Created 02/03/2023 at 3:52 PM
 */
@Database(
        entities = {Task.class, Tag.class, User.class, TaskTag.class, UserTask.class},
        views = {TaskSummary.class, TaskDetail.class},
        version = 1,
        exportSchema = false
)
@TypeConverters(AppDatabaseTypeConverters.class)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();


}

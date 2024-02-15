package com.example.trackr.shared.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RewriteQueriesToDropUnusedColumns;
import androidx.room.Transaction;

import com.example.trackr.shared.db.tables.Tag;
import com.example.trackr.shared.db.tables.Task;
import com.example.trackr.shared.db.tables.TaskStatus;
import com.example.trackr.shared.db.tables.TaskTag;
import com.example.trackr.shared.db.tables.User;
import com.example.trackr.shared.db.tables.UserTask;
import com.example.trackr.shared.db.views.TaskDetail;
import com.example.trackr.shared.db.views.TaskSummary;


import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;




/**
 * @author Rameel Hassan
 * Created 01/03/2023 at 5:44 PM
 */
@Dao
public interface TaskDao {

    // TODO :consider creating UserDao and moving some of the logic in this Dao there
    @Insert
    void insertUsers(List<User> users);


    @Insert
    void insertTags(List<Tag> tags);

    @Insert
    void insertTasks(List<Task> tasks);

    @Insert
    void insertTaskTags(List<TaskTag> taskTags);

    @Insert
    Completable insertUserTasks(List<UserTask> userTasks);

    @Delete
    Completable deleteUserTasks(List<UserTask> userTasks);

    @Query("SELECT * FROM tasks")   //Maybe will either return a User
    Flowable<List<Task>> getTasks();   // or throw an error

    @Query("SELECT * FROM USERS WHERE id = :id")
    Maybe<User> getUserById(Long id);

    @Transaction
    @Query("SELECT * FROM TaskDetail WHERE id = :id")
    Flowable<TaskDetail> findTaskDetailById(Long id);

    @Transaction
    @Query("SELECT * FROM TaskDetail WHERE id = :id")
    Maybe<TaskDetail> loadTaskDetailById(Long id);

    @Transaction
    @Query(
            "SELECT s.*, "+
                    "    EXISTS( "+
                    "        SELECT id "+
                    "        FROM user_tasks AS t "+
                    "        WHERE t.taskId = s.id AND t.userId = :userId "+
                    "    ) AS starred " +
                    " FROM TaskSummary AS s WHERE s.isArchived = 0"+
                    " ORDER BY s.status, s.orderInCategory "
    )
    Flowable<List<TaskSummary>> getOngoingTaskSummaries(Long userId);

    @Transaction
    @Query(
            " SELECT s.*,"+
                    "     EXISTS( "+
                    "         SELECT id "+
                    "         FROM user_tasks AS t "+
                    "         WHERE t.taskId = s.id AND t.userId = :userId "+
                    "     ) AS starred "+
                    " FROM TaskSummary AS s WHERE s.isArchived <> 0 "+
                    " ORDER BY s.orderInCategory "
    )
    Flowable<List<TaskSummary>> getArchivedTaskSummaries(Long userId);

    @Query("UPDATE tasks SET status = :status WHERE id = :id")
    Completable updateTaskStatus(Long id , TaskStatus status);

    @Query("UPDATE tasks SET status = :status WHERE id IN (:ids)")
    Completable updateTaskStatus(List<Long> ids,TaskStatus status);

    @Query("UPDATE tasks SET orderInCategory = :orderInCategory WHERE id = :id")
    Completable updateOrderInCategory(Long id,int orderInCategory);

    @Query("UPDATE tasks SET isArchived = :isArchived WHERE id IN (:ids)")
    Completable setIsArchived(List<Long> ids,Boolean isArchived);

    @Query("SELECT * FROM user_tasks WHERE taskId = :taskId AND userId = :userId")
    Single<UserTask> getUserTask(Long taskId,Long userId);

    @Query("SELECT * FROM users")
    Flowable<List<User>> loadUsers();

    @Query("SELECT * FROM tags")
    Flowable<List<Tag>> loadTags();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTask(Task task);

    @Query("SELECT tagId FROM task_tags WHERE taskId = :taskId")
    Flowable<List<Long>> loadTaskTagIds(Long taskId);

    @Query("DELETE FROM task_tags WHERE taskId = :taskId AND tagId IN(:tagIds)")
    Completable deleteTaskTags(Long taskId, List<Long> tagIds );

    @Query(
            " SELECT MIN(orderInCategory) FROM tasks " +
                    " WHERE " +
                    "     status = :status " +
                    "     AND id <> :excludeTaskId "
    )
    Single<Integer> loadMinOrderInCategory(TaskStatus status, Long excludeTaskId);

    @Transaction
    default void saveTaskDetail(TaskDetail detail, Boolean topOrderInCategory) {
        if (detail.title.isEmpty()) {
            throw new IllegalArgumentException("Task must include non-empty title.");
        }
        if (topOrderInCategory) {
            int min = loadMinOrderInCategory(detail.status, detail.id).blockingGet();
            detail.orderInCategory =  min == 0 ? 1 : min - 1 ;
        }

        Task task = Task.builder()
                .setId(detail.id)
                .setTitle(detail.title)
                .setDescription(detail.description)
                .setStatus(detail.status)
                .setCreatorId(detail.creator.id)
                .setOwnerId(detail.owner.id)
                .setCreatedAt(detail.createdAt)
                .setDueAt(detail.dueAt)
                .setArchived(detail.isArchived)
                .setOrderInCategory(detail.orderInCategory)
                .build();
        Long taskId = insertTask(task);



        List<Long> updatedTagIds =  detail.tags.stream().map(Tag::getId).collect(Collectors.toList());
        List<Long> currentTagIds = loadTaskTagIds(taskId).blockingFirst();

        List<Long> removedTagIds = currentTagIds.stream().filter(element -> !updatedTagIds.contains(element)).collect(Collectors.toList());
        deleteTaskTags(taskId, removedTagIds);

        List<Long> newTagIds = updatedTagIds.stream().filter(element -> !currentTagIds.contains(element)).collect(Collectors.toList());
        List<TaskTag> newTaskTags = newTagIds.stream().map(id -> new TaskTag(taskId, id)).collect(Collectors.toList());
         insertTaskTags(newTaskTags);

    }

    @Query(
            " UPDATE tasks " +
                    " SET orderInCategory = orderInCategory + :delta " +
                    " WHERE " +
                    "     status = :status " +
                    "     AND orderInCategory BETWEEN :minOrderInCategory AND :maxOrderInCategory "

    )
    Completable shiftTasks(
            TaskStatus status,
            int minOrderInCategory,
            int maxOrderInCategory,
            int delta
    );

    @Transaction
    default void reorderTasks(Long taskId, TaskStatus status, int currentOrderInCategory, int targetOrderInCategory) {

        if (currentOrderInCategory < targetOrderInCategory) {
            shiftTasks(status, currentOrderInCategory + 1, targetOrderInCategory, -1);
        } else {
            shiftTasks(status, targetOrderInCategory, currentOrderInCategory - 1, 1);
        }
         updateOrderInCategory(taskId, targetOrderInCategory);
    }
}

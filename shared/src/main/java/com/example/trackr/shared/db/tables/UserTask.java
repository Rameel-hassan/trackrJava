package com.example.trackr.shared.db.tables;

/**
 * @author Rameel Hassan
 * Created 02/03/2023 at 11:04 AM
 */

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_tasks",
        foreignKeys = {
        @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId"),
        @ForeignKey(entity = Task.class, parentColumns = "id",childColumns = "taskId")
        },
        indices = {
                @Index(value ={"userId", "taskId"}, unique = true), @Index("userId"), @Index("taskId")
        })
public class UserTask {
        @PrimaryKey(autoGenerate = true)
        public  int id = 0;
        public  Long userId;
        public Long taskId;

        public UserTask(Long userId, Long taskId) {
                this.userId = userId;
                this.taskId = taskId;
        }
}


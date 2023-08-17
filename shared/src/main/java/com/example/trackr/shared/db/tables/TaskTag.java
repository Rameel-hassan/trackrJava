package com.example.trackr.shared.db.tables;

/**
 * @author Rameel Hassan
 * Created 02/03/2023 at 11:04 AM
 */

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "task_tags",
        foreignKeys = {
        @ForeignKey(childColumns = "taskId", entity = Task.class, parentColumns = "id"),
        @ForeignKey(childColumns = "tagId", entity = Tag.class, parentColumns = "id")
        },
        indices = {
        @Index(value = {"taskId", "tagId"}, unique = true),
        @Index("taskId"),
        @Index("tagId")
        }
        )
public class TaskTag {
        @PrimaryKey(autoGenerate = true)
        public int id = 0;
        public Long taskId;
        public Long tagId;

        public TaskTag(Long taskId, Long tagId) {
                this.taskId = taskId;
                this.tagId = tagId;
        }
}
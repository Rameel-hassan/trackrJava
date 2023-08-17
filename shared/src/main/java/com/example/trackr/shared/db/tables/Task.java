package com.example.trackr.shared.db.tables;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.Duration;
import java.time.Instant;

/**
 * @author Rameel Hassan
 * Created 02/03/2023 at 11:03 AM
 */


@Entity(
        tableName = "tasks",
        foreignKeys = {
                @ForeignKey(childColumns = "creatorId", entity=User.class, parentColumns = "id"),
                @ForeignKey(childColumns= "ownerId" , entity=User.class, parentColumns= "id")
        },
        indices = {
                @Index("creatorId"),
                @Index("ownerId")
        }
        )


        public class Task{
        @PrimaryKey(autoGenerate = true)
       public Long id;

        /**
         * The task title. TODO: consider adding char limit which may help showcase a11y validation issues.
         */
       public String title;

        /**
         * The task description, which can be verbose.
         */
        public  String description = "";

        /**
         * The state of the task.
         */
        public TaskStatus status = TaskStatus.NOT_STARTED;

        /**
         * The team member who created the task (this defaults to the current user).
         */
        public Long creatorId;

        /**
         * The team member who the task has been assigned to.
         */
        public Long ownerId;

        /**
         * When this task was created.
         */
        public  Instant createdAt = Instant.now();

        /**
         * When this task is due.
         */
        public Instant dueAt = Instant.now().plus(Duration.ofDays(7));

        /**
         * Tracks the order in which tasks are presented within a category.
         */
        public int orderInCategory;

/**
 * Whether this task is archived.
 */
@ColumnInfo(
        // SQLite has no boolean type, so integers 0 and 1 are used instead. Room will do the
        // conversion automatically.
        defaultValue = "0"
)
   public Boolean isArchived = false;

        public Task() {
        }

        Task(Builder builder) {
                this.id = builder.id;
                this.title = builder.title;
                this.description = builder.description;
                this.status = builder.status;
                this.creatorId = builder.creatorId;
                this.ownerId = builder.ownerId;
                this.createdAt = builder.createdAt;
                this.dueAt = builder.dueAt;
                this.orderInCategory = builder.orderInCategory;
                this.isArchived = builder.isArchived;
        }

        public static Builder builder() {
                return new Builder();
        }

        public static class Builder {

                Long id;
                String title;
                String description = "";
                TaskStatus status = TaskStatus.NOT_STARTED;
                Long creatorId;
                Long ownerId;
                Instant createdAt = Instant.now();
                Instant dueAt = Instant.now().plus(Duration.ofDays(7));
                int orderInCategory;
                Boolean isArchived = false;

                public Long getId() {
                        return id;
                }

                public Builder setId(Long id) {
                        this.id = id;
                        return this;
                }

                public Builder setTitle(String title) {
                        this.title = title;
                        return this;
                }

                public Builder setDescription(String description) {
                        this.description = description;
                        return this;
                }

                public Builder setStatus(TaskStatus status) {
                        this.status = status;
                        return this;
                }

                public Builder setCreatorId(Long creatorId) {
                        this.creatorId = creatorId;
                        return this;
                }

                public Builder setOwnerId(Long ownerId) {
                        this.ownerId = ownerId;
                        return this;
                }

                public Builder setCreatedAt(Instant createdAt) {
                        this.createdAt = createdAt;
                        return this;
                }

                public Builder setDueAt(Instant dueAt) {
                        this.dueAt = dueAt;
                        return this;
                }

                public Builder setOrderInCategory(int orderInCategory) {
                        this.orderInCategory = orderInCategory;
                        return this;
                }

                public Builder setArchived(Boolean archived) {
                        isArchived = archived;
                        return this;
                }

                public Task build() {
                        return new Task(this);
                }


        }

}
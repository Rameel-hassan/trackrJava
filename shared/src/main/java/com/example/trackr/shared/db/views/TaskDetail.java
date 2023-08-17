package com.example.trackr.shared.db.views;

import androidx.room.DatabaseView;
import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.trackr.shared.db.tables.Tag;
import com.example.trackr.shared.db.tables.TaskStatus;
import com.example.trackr.shared.db.tables.TaskTag;
import com.example.trackr.shared.db.tables.User;
import com.example.trackr.shared.db.tables.UserTask;

import java.time.Instant;
import java.util.List;

/**
 * @author Rameel Hassan
 * Created 02/03/2023 at 3:20 PM
 */

@DatabaseView(
          "  SELECT " +
          "      t.id, t.title, t.description, t.status, t.createdAt, t.dueAt, t.orderInCategory, " +
          "      t.isArchived, " +
          "      o.id AS owner_id, o.username AS owner_username, o.avatar AS owner_avatar, " +
          "      c.id AS creator_id, c.username AS creator_username, c.avatar as creator_avatar " +
          "  FROM tasks AS t " +
          "  INNER JOIN users AS o ON o.id = t.ownerId " +
          "  INNER JOIN users AS c ON c.id = t.creatorId "
)
public class TaskDetail {
    public Long id;
    public String title;
    public String description;
    public TaskStatus status;
    public Instant createdAt;
    public Instant dueAt;
    public int orderInCategory;
    public Boolean isArchived;
    @Embedded(prefix = "owner_")
    public User owner;
    @Embedded(prefix = "creator_")
    public User creator;
    @Relation(
            parentColumn = "id",
            entity = Tag.class,
            entityColumn = "id",
            associateBy = @Junction(
                    value = TaskTag.class,
            parentColumn = "taskId",
            entityColumn = "tagId"
    )
    )
    public List<Tag> tags;

    @Relation(
            parentColumn = "id",
            entity = User.class,
            entityColumn = "id",
            associateBy = @Junction(
                    value = UserTask.class,
            parentColumn = "taskId",
            entityColumn = "userId"
    )
    )
    public List<User> starUsers;


 public TaskDetail(Long id, String title, String description, TaskStatus status, Instant createdAt, Instant dueAt, int orderInCategory, Boolean isArchived, User owner, User creator, List<Tag> tags, List<User> starUsers) {
  this.id = id;
  this.title = title;
  this.description = description;
  this.status = status;
  this.createdAt = createdAt;
  this.dueAt = dueAt;
  this.orderInCategory = orderInCategory;
  this.isArchived = isArchived;
  this.owner = owner;
  this.creator = creator;
  this.tags = tags;
  this.starUsers = starUsers;
 }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getDueAt() {
        return dueAt;
    }

    public void setDueAt(Instant dueAt) {
        this.dueAt = dueAt;
    }

    public int getOrderInCategory() {
        return orderInCategory;
    }

    public void setOrderInCategory(int orderInCategory) {
        this.orderInCategory = orderInCategory;
    }

    public Boolean getArchived() {
        return isArchived;
    }

    public void setArchived(Boolean archived) {
        isArchived = archived;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<User> getStarUsers() {
        return starUsers;
    }

    public void setStarUsers(List<User> starUsers) {
        this.starUsers = starUsers;
    }
}

package com.example.trackr.shared.db.views;

import androidx.room.DatabaseView;
import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.trackr.shared.db.tables.Tag;
import com.example.trackr.shared.db.tables.TaskStatus;
import com.example.trackr.shared.db.tables.TaskTag;
import com.example.trackr.shared.db.tables.User;

import java.time.Instant;
import java.util.List;

/**
 * @author Rameel Hassan
 * Created 02/03/2023 at 3:06 PM
 */

@DatabaseView(" SELECT t.id, t.title, t.status, t.dueAt, t.orderInCategory, t.isArchived, o.id AS owner_id, o.username AS owner_username, o.avatar AS owner_avatar FROM tasks AS t INNER JOIN users AS o ON o.id = t.ownerId ")
public class TaskSummary {

   public Long id;

    public String title;

    public TaskStatus status;

    public Instant dueAt;

    public int orderInCategory;

    public Boolean isArchived = false;

    @Embedded(prefix = "owner_")
    public User owner;

    @Relation(
            parentColumn = "id",
            entity = Tag.class,
            entityColumn = "id",
            associateBy = @Junction(
                    value = TaskTag.class,
            parentColumn = "taskId",
            entityColumn = "tagId")
    )
    public List<Tag> tags;

    public Boolean starred;


 public Long getId() {
  return id;
 }

 public void setId(Long id) {
  this.id = id;
 }

 public TaskStatus getStatus() {
  return status;
 }

 public void setStatus(TaskStatus status) {
  this.status = status;
 }

 public Boolean getStarred() {
  return starred;
 }

 public void setStarred(Boolean starred) {
  this.starred = starred;
 }

 public int getOrderInCategory() {
  return orderInCategory;
 }

 public void setOrderInCategory(int orderInCategory) {
  this.orderInCategory = orderInCategory;
 }
}

package com.example.trackr.shared.db;

import androidx.room.TypeConverter;

import com.example.trackr.shared.db.tables.Avatar;
import com.example.trackr.shared.db.tables.TagColor;
import com.example.trackr.shared.db.tables.TaskStatus;

import java.time.Instant;

/**
 * @author Rameel Hassan
 * Created 02/03/2023 at 3:54 PM
 */
public class AppDatabaseTypeConverters {


    @TypeConverter
   public Long instantToLong(Instant value) {
        return value.toEpochMilli();
    }

    @TypeConverter
    public Instant longToInstant(Long value) {
        return Instant.ofEpochMilli(value);
    }

    @TypeConverter
    public Integer taskStatusToInt(TaskStatus taskStatus) {
        return taskStatus.key;
    }

    @TypeConverter
    public TaskStatus intToTaskStatus(Integer integ) {
        return TaskStatus.fromKey(integ);
    }

    @TypeConverter
    public String tagColorToString(TagColor color) {
        return color.name();
    }

    @TypeConverter
    public TagColor stringToTagColor( String string){
        return TagColor.valueOf(string);
    }

    @TypeConverter
    public String avatarToString(Avatar avatar) {
        return avatar.name();
    }

    @TypeConverter
    public Avatar stringToAvatar(String name) {
        return Avatar.fromName(name);
    }
}

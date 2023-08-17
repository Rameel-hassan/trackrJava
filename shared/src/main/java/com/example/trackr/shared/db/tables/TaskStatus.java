package com.example.trackr.shared.db.tables;

import com.example.trackr.shared.R;

/**
 * @author Rameel Hassan
 * Created 02/03/2023 at 11:04 AM
 */


public enum TaskStatus {
    NOT_STARTED(1, R.string.not_started),
    IN_PROGRESS(2, R.string.in_progress),
    COMPLETED(3, R.string.completed);
   public int key;
   public int stringResId;


    public int getStringResId() {
        return stringResId;
    }

    public void setStringResId(int stringResId) {
        this.stringResId = stringResId;
    }

    TaskStatus(int key) {
        this.key = key;
    }

    TaskStatus(int key, int stringResId) {
        this.key = key;
        this.stringResId = stringResId;
    }


    public static TaskStatus fromKey(int abbr){
        for(TaskStatus v : values()){
            if( v.key == abbr){
                return v;
            }
        }
        return null;
    }
}

package com.example.trackr.shared.db.tables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @author Rameel Hassan
 * Created 02/03/2023 at 11:02 AM
 */

@Entity(tableName = "tags")
public class Tag{
        @PrimaryKey
       public Long id;

        /**
         * A short label for the tag.
         */
        public    String label;

        // TODO: consider making the label optional and adding an icon/pattern for color-only tags.

        /**
         * A color associated with the tag.
         */
       public TagColor color;



        public Tag(Long id, String label, TagColor color) {
                this.id = id;
                this.label = label;
                this.color = color;
        }

        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public String getLabel() {
                return label;
        }

        public void setLabel(String label) {
                this.label = label;
        }

        public TagColor getColor() {
                return color;
        }

        public void setColor(TagColor color) {
                this.color = color;
        }
}

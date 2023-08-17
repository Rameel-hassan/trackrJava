package com.example.trackr.shared.db.tables;


import com.example.trackr.shared.R;

/**
 * @author Rameel Hassan
 * Created 02/03/2023 at 11:08 AM
 */
// Denotes the tag text and background color to be displayed


public enum TagColor {
    BLUE(R.attr.blueTagTextColor, R.attr.blueTagBackgroundColor),
    GREEN(R.attr.greenTagTextColor, R.attr.greenTagBackgroundColor),
    PURPLE(R.attr.purpleTagTextColor, R.attr.purpleTagBackgroundColor),
    RED(R.attr.redTagTextColor, R.attr.redTagBackgroundColor),
    TEAL(R.attr.tealTagTextColor, R.attr.tealTagBackgroundColor),
    YELLOW(R.attr.yellowTagTextColor, R.attr.yellowTagBackgroundColor);

  public   int textColor;
  public   int backgroundColor;

    TagColor() {
    }

    TagColor(int textColor, int backgroundColor) {
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}


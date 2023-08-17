package com.example.trackr.shared.db.tables;

/**
 * @author Rameel Hassan
 * Created 02/03/2023 at 11:04 AM
 */

import com.example.trackr.shared.R;

public enum Avatar{
    DARING_DOVE(R.drawable.ic_daring_dove),
    LIKEABLE_LARK(R.drawable.ic_likeable_lark),
    PEACEFUL_PUFFIN(R.drawable.ic_peaceful_puffin),
    DEFAULT_USER(R.drawable.ic_user);
    public int drawableResId;
     Avatar() {
    }

      Avatar(int drawableResId) {
        this.drawableResId = drawableResId;
    }
    public static Avatar fromName(String abbr){
        for(Avatar v : values()){
            if( v.name() == abbr){
                return v;
            }
        }
        return null;
    }

}

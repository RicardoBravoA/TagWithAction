package com.rba.tagwithactions.control.tag.listener;

import com.rba.tagwithactions.control.tag.TagGroup;

/**
 * Created by Ricardo Bravo on 7/07/16.
 */

public interface OnTagClickListener {
    void onTagClick(TagGroup.Tag tag, int position);
}
package com.rba.tagwithactions.control.tag.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by Ricardo Bravo on 7/07/16.
 */

public class Util {
    private Util() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }

    public static int dipToPx(Context c, float dipValue) {
        DisplayMetrics metrics = c.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
}

package tech.michaelx.loadinglibrary;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.view.View;

/**
 * Created by michaelx on 2017/9/14.
 */

public class ResCompat {
    public static void setBackground(View view, Drawable bg) {
        if (view == null) return;

        if (Build.VERSION.SDK_INT >= 16) {
            view.setBackground(bg);
        } else {
            view.setBackgroundDrawable(bg);
        }
    }

    public static Drawable getDrawable(View view, @DrawableRes int drawableId) {
        if (view == null) return null;

        if (Build.VERSION.SDK_INT >= 21) {
            return view.getResources().getDrawable(drawableId, view.getContext().getTheme());
        } else {
            return view.getResources().getDrawable(drawableId);
        }
    }
}

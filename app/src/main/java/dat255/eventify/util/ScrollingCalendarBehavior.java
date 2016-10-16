package dat255.eventify.util;

/**
 * Created by Kristoffer on 2016-10-16.
 */

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ScrollingCalendarBehavior extends AppBarLayout.Behavior {

    public ScrollingCalendarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
        return false;/*super.onInterceptTouchEvent(parent, child, ev);*/
    }
}
package com.example.projectofmurad.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import androidx.annotation.NonNull;

/**
 * The type View animation utils.
 */
public abstract class ViewAnimationUtils {

    /**
     * Expand or collapse.
     *
     * @param v                the v
     * @param expandOrCollapse the expand or collapse
     */
    public static void expandOrCollapse(@NonNull final View v, boolean expandOrCollapse) {
        if (expandOrCollapse){
            expand(v);
        }
        else {
            collapse(v);
        }
    }

    /**
     * Expand.
     *
     * @param v the v
     */
    public static void expand(@NonNull final View v) {
        if (v.getVisibility() == View.VISIBLE) {
            return;
        }

        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = (interpolatedTime == 1)
                        ? targetHeight
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density))*4);
        v.startAnimation(a);
    }

    /**
     * Collapse.
     *
     * @param v the v
     */
    public static void collapse(@NonNull final View v) {
        if (v.getVisibility() == View.GONE){
            return;
        }

        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }
                else{
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density)) * 4);
        v.startAnimation(a);
    }
}
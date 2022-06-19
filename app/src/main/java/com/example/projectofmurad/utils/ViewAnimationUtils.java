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
     * @param view                the v
     * @param expandOrCollapse the expand or collapse
     */
    public static void expandOrCollapse(@NonNull final View view, boolean expandOrCollapse) {
        if (expandOrCollapse){
            expand(view);
        }
        else {
            collapse(view);
        }
    }

    /**
     * Expand.
     *
     * @param view the view
     */
    public static void expand(@NonNull final View view) {
        if (view.getVisibility() == View.VISIBLE) {
            return;
        }

        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        final int targetHeight = view.getMeasuredHeight();

        view.getLayoutParams().height = 0;
        view.setVisibility(View.VISIBLE);

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                view.getLayoutParams().height = (interpolatedTime == 1)
                        ? targetHeight
                        : (int) (targetHeight * interpolatedTime);
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(((int) (targetHeight / view.getContext().getResources().getDisplayMetrics().density))*4);
        view.startAnimation(a);
    }

    /**
     * Collapse.
     *
     * @param view the view
     */
    public static void collapse(@NonNull final View view) {
        if (view.getVisibility() == View.GONE){
            return;
        }

        final int initialHeight = view.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    view.setVisibility(View.GONE);
                }
                else{
                    view.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    view.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(((int) (initialHeight / view.getContext().getResources().getDisplayMetrics().density)) * 4);
        view.startAnimation(a);
    }
}
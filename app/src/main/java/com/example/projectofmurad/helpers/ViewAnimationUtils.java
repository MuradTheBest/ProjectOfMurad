package com.example.projectofmurad.helpers;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import androidx.annotation.NonNull;

public class ViewAnimationUtils {

    public static void expand(@NonNull final View v) {
        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
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

    public static void collapse(@NonNull final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }
                else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
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
package com.example.projectofmurad.helpers;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyGridLayoutManager extends GridLayoutManager {

    public MyGridLayoutManager(Context context, AttributeSet attrs,
                               int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MyGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public MyGridLayoutManager(Context context, int spanCount, int orientation,
                               boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }

    public void setOnLayoutCompleteListener(OnLayoutCompleteCallback callback) {
        mCallback = callback;
    }

    private OnLayoutCompleteCallback mCallback = null;

    @Override
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
        if (mCallback != null)
            mCallback.onLayoutComplete();
    }

    public interface OnLayoutCompleteCallback {
        void onLayoutComplete();
    }
}

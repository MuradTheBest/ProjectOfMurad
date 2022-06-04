package com.example.projectofmurad.helpers;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LinearLayoutManagerWrapper extends LinearLayoutManager {

    public LinearLayoutManagerWrapper(Context context) {
        super(context);
    }

    public LinearLayoutManagerWrapper(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public LinearLayoutManagerWrapper(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        }
        catch (IndexOutOfBoundsException e) {
            Log.e("murad", "meet a IOOBE in RecyclerView");
        }
    }

    private OnLayoutCompleteCallback onLayoutCompleteCallback = null;

    public LinearLayoutManagerWrapper setOnLayoutCompleteListener(OnLayoutCompleteCallback onLayoutCompleteCallback) {
        this.onLayoutCompleteCallback = onLayoutCompleteCallback;
        return this;
    }

    @Override
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
        if (onLayoutCompleteCallback != null) onLayoutCompleteCallback.onLayoutComplete();
    }

    public interface OnLayoutCompleteCallback {
        void onLayoutComplete();
    }
}

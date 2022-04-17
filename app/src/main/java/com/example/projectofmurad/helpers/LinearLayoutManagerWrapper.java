package com.example.projectofmurad.helpers;

import android.content.Context;
import android.util.AttributeSet;

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

    private OnLayoutCompleteCallback mCallback = null;

    public void setOnLayoutCompleteListener(OnLayoutCompleteCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
        if (mCallback != null) mCallback.onLayoutComplete();
    }

    public interface OnLayoutCompleteCallback {
        void onLayoutComplete();
    }
}

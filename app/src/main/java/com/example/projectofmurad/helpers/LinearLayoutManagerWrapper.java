package com.example.projectofmurad.helpers;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * The type Linear layout manager wrapper.
 */
public class LinearLayoutManagerWrapper extends LinearLayoutManager {

    /**
     * Instantiates a new Linear layout manager wrapper.
     *
     * @param context the context
     */
    public LinearLayoutManagerWrapper(Context context) {
        super(context);
    }

    /**
     * Instantiates a new Linear layout manager wrapper.
     *
     * @param context       the context
     * @param orientation   the orientation
     * @param reverseLayout the reverse layout
     */
    public LinearLayoutManagerWrapper(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    /**
     * Instantiates a new Linear layout manager wrapper.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     * @param defStyleRes  the def style res
     */
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

    /**
     * Sets on layout complete listener.
     *
     * @param onLayoutCompleteCallback the on layout complete callback
     *
     * @return the on layout complete listener
     */
    public LinearLayoutManagerWrapper setOnLayoutCompleteListener(OnLayoutCompleteCallback onLayoutCompleteCallback) {
        this.onLayoutCompleteCallback = onLayoutCompleteCallback;
        return this;
    }

    @Override
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
        if (onLayoutCompleteCallback != null) onLayoutCompleteCallback.onLayoutComplete();
    }

    /**
     * The interface On layout complete callback.
     */
    public interface OnLayoutCompleteCallback {
        /**
         * On layout complete.
         */
        void onLayoutComplete();
    }
}

package com.example.projectofmurad;

import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RVOnItemTouchListenerForVP2 implements RecyclerView.OnItemTouchListener {

    private final RecyclerView recyclerView;
    private MutableLiveData<Boolean> toSwipe;

    public RVOnItemTouchListenerForVP2(RecyclerView recyclerView,
                                       MutableLiveData<Boolean> toSwipe) {
        this.recyclerView = recyclerView;
        this.toSwipe = toSwipe;
    }

    int lastX = 0;
    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) e.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                boolean isScrollingRight = e.getX() < lastX;
                if ((isScrollingRight && ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition() == recyclerView.getAdapter().getItemCount() - 1) ||
                        (!isScrollingRight && ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition() == 0)) {
                    toSwipe.setValue(true);
                } else {
                    toSwipe.setValue(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                lastX = 0;
                toSwipe.setValue(true);
                break;
        }
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

}
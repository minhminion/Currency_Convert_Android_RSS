package com.example.currencyconvert;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;


public class SliderLayoutManager extends LinearLayoutManager{

    interface OnItemSelectedListener {
        void onItemSelected(Integer layoutPosition);
    }

    public void setCallback(OnItemSelectedListener callback) {
        this.callback = callback;
    }

    OnItemSelectedListener callback ;
    private RecyclerView recyclerView;


    public SliderLayoutManager(Context context, int orientation, boolean reverseLayout, OnItemSelectedListener callback) {
        super(context, orientation, reverseLayout);
        this.callback = callback;
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);

        recyclerView = view;

        // Smart snapping

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE) {

            // Find the closest child to the recyclerView center --> this is the selected item.
            int recyclerViewCenterX = getRecyclerViewCenterX();
            int minDistance = recyclerView.getWidth();
            int position = -1;
            for( int i = 0; i < recyclerView.getChildCount() ; i++) {
                View child = recyclerView.getChildAt(i);
                int childCenterX = getDecoratedLeft(child) + (getDecoratedRight(child) - getDecoratedLeft(child)) / 2;
                int childDistanceFromCenter = Math.abs(childCenterX - recyclerViewCenterX);
                if (childDistanceFromCenter < minDistance) {
                    minDistance = childDistanceFromCenter;
                    position = recyclerView.getChildLayoutPosition(child);
                }
            }

            // Notify on the selected item
            callback.onItemSelected(position);
        }
    }

    private int getRecyclerViewCenterX() {
        return (recyclerView.getRight() - recyclerView.getLeft())/2 + recyclerView.getLeft();
    }


}

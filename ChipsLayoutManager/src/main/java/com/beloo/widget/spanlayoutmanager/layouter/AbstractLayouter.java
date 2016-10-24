package com.beloo.widget.spanlayoutmanager.layouter;

import android.graphics.Rect;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

import com.beloo.widget.spanlayoutmanager.ChipsLayoutManager;
import com.beloo.widget.spanlayoutmanager.SpanLayoutChildGravity;
import com.beloo.widget.spanlayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.spanlayoutmanager.gravity.GravityModifiersFactory;
import com.beloo.widget.spanlayoutmanager.gravity.IChildGravityResolver;
import com.beloo.widget.spanlayoutmanager.gravity.IGravityModifier;

abstract class AbstractLayouter implements ILayouter {
    int currentViewWidth;
    int currentViewHeight;
    private int currentViewPosition;
    List<Pair<Rect, View>> rowViews = new LinkedList<>();
    /** bottom of current row*/
    int rowBottom;
    /** top of current row*/
    int rowTop;


    /** Max items in row restriction. Layout of row should be stopped when this count of views reached*/
    @Nullable
    Integer maxViewsInRow = null;

    int rowSize = 0;
    int previousRowSize;

    private ChipsLayoutManager layoutManager;
    private IViewCacheStorage cacheStorage;

    private IChildGravityResolver childGravityResolver;
    private GravityModifiersFactory gravityModifiersFactory = new GravityModifiersFactory();

    AbstractLayouter(ChipsLayoutManager layoutManager, int topOffset, int bottomOffset, IViewCacheStorage cacheStorage, IChildGravityResolver childGravityResolver) {
        this.layoutManager = layoutManager;
        this.rowTop = topOffset;
        this.rowBottom = bottomOffset;
        this.cacheStorage = cacheStorage;
        this.childGravityResolver = childGravityResolver;
    }

    int getCanvasWidth() {
        return layoutManager.getWidth();
    }

    int getCanvasHeight() {
        return layoutManager.getHeight();
    }

    int getCurrentViewPosition() {
        return currentViewPosition;
    }

    IViewCacheStorage getCacheStorage() {
        return cacheStorage;
    }

    @Override
    public int getPreviousRowSize() {
        return previousRowSize;
    }

    void setMaxViewsInRow(@Nullable Integer maxViewsInRow) {
        this.maxViewsInRow = maxViewsInRow;
    }

    /** read view params to memory */
    private void calculateView(View view) {
        currentViewHeight = layoutManager.getDecoratedMeasuredHeight(view);
        currentViewWidth = layoutManager.getDecoratedMeasuredWidth(view);
        currentViewPosition = layoutManager.getPosition(view);
    }

    @Override
    @CallSuper
    /** calculate view positions, view won't be actually added to layout when calling this method
     * @return true if view successfully placed, false if view can't be placed because out of space on screen and have to be recycled */
    public final boolean placeView(View view) {
        calculateView(view);

        if (canNotBePlacedInCurrentRow()) {
            layoutRow();
        }

        if (isFinishedLayouting()) return false;

        Rect rect = createViewRect(view);
        rowViews.add(new Pair<>(rect, view));

        return true;
    }

    /** if all necessary view have placed*/
    abstract boolean isFinishedLayouting();

    /** check if we can not add current view to row*/
    abstract boolean canNotBePlacedInCurrentRow();

    /** factory method for Rect, where view will be placed. Creation based on inner layouter parameters */
    abstract Rect createViewRect(View view);

    /** add view to layout manager */
    abstract void addView(View view);

    @CallSuper
    @Override
    /** Read layouter state from current attached view. We need only last of it, but we can't determine here which is last.
     * Based on characteristics of last attached view, layouter algorithm will be able to continue placing from it.
     * This method have to be called on attaching view*/
    public boolean onAttachView(View view) {
        rowSize++;

        if (isFinishedLayouting()) return false;

        layoutManager.attachView(view);
        return true;
    }

    @CallSuper
    @Override
    /** add views from current row to layout*/
    public void layoutRow() {
        previousRowSize = rowSize;
        this.rowSize = 0;
    }

    /** layout pre-calculated row on a recyclerView canvas
     * @param leftOffsetOfRow How much row have to be shifted before placing. Should be negative on RTL
     * returns rowTop */
    int layoutRow(List<Pair<Rect, View>> rowViews, int minTop, int maxBottom, int leftOffsetOfRow) {
        for (Pair<Rect, View> rowViewRectPair : rowViews) {
            Rect viewRect = rowViewRectPair.first;

            viewRect.left = viewRect.left - leftOffsetOfRow;
            viewRect.right = viewRect.right - leftOffsetOfRow;

            minTop = Math.min(minTop, viewRect.top);
            maxBottom = Math.max(maxBottom, viewRect.bottom);
        }

        for (Pair<Rect, View> rowViewRectPair : rowViews) {
            Rect viewRect = rowViewRectPair.first;
            View view = rowViewRectPair.second;

            @SpanLayoutChildGravity
            int viewGravity = childGravityResolver.getItemGravity(getLayoutManager().getPosition(view));
            IGravityModifier gravityModifier = gravityModifiersFactory.getGravityModifier(viewGravity);
            gravityModifier.modifyChildRect(minTop, maxBottom, viewRect);

            addView(view);

            //layout whole views in a row
            layoutManager.layoutDecorated(view, viewRect.left, viewRect.top, viewRect.right, viewRect.bottom);
        }

        return minTop;
    }

    ChipsLayoutManager getLayoutManager() {
        return layoutManager;
    }
}

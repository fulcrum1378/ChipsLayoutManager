package ir.mahdiparastesh.chlm;

import android.util.SparseArray;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ir.mahdiparastesh.chlm.layouter.ICanvas;
import ir.mahdiparastesh.chlm.layouter.IStateFactory;

class DisappearingViewsManager implements IDisappearingViewsManager {

    private final ICanvas canvas;
    private final ChildViewsIterable childViews;
    private final IStateFactory stateFactory;
    private int deletingItemsOnScreenCount;

    DisappearingViewsManager(ICanvas canvas, ChildViewsIterable childViews, IStateFactory stateFactory) {
        this.canvas = canvas;
        this.childViews = childViews;
        this.stateFactory = stateFactory;
    }

    static class DisappearingViewsContainer {
        private final SparseArray<View> backwardViews = new SparseArray<>();
        private final SparseArray<View> forwardViews = new SparseArray<>();

        int size() {
            return backwardViews.size() + forwardViews.size();
        }

        SparseArray<View> getBackwardViews() {
            return backwardViews;
        }

        SparseArray<View> getForwardViews() {
            return forwardViews;
        }
    }


    @Override
    public DisappearingViewsContainer getDisappearingViews(RecyclerView.Recycler recycler) {
        final List<RecyclerView.ViewHolder> scrapList = recycler.getScrapList();
        DisappearingViewsContainer container = new DisappearingViewsContainer();

        for (RecyclerView.ViewHolder holder : scrapList) {
            final View child = holder.itemView;
            final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
            if (!lp.isItemRemoved()) {
                if (lp.getViewLayoutPosition() < canvas.getMinPositionOnScreen()) {
                    container.backwardViews.put(lp.getViewLayoutPosition(), child);
                } else if (lp.getViewLayoutPosition() > canvas.getMaxPositionOnScreen()) {
                    container.forwardViews.put(lp.getViewLayoutPosition(), child);
                }
            }
        }
        return container;
    }

    @Override
    public int calcDisappearingViewsLength(RecyclerView.Recycler recycler) {
        int removedLength = 0;
        int minStart = Integer.MAX_VALUE;
        int maxEnd = Integer.MIN_VALUE;

        for (View view : childViews) {
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) view.getLayoutParams();
            boolean probablyMovedFromScreen = false;

            if (!lp.isItemRemoved()) {
                int pos = lp.getViewLayoutPosition();
                pos = recycler.convertPreLayoutPositionToPostLayout(pos);
                probablyMovedFromScreen = pos < canvas.getMinPositionOnScreen()
                        || pos > canvas.getMaxPositionOnScreen();
            }

            if (lp.isItemRemoved() || probablyMovedFromScreen) {
                deletingItemsOnScreenCount++;
                minStart = Math.min(minStart, stateFactory.getStart(view));
                maxEnd = Math.max(maxEnd, stateFactory.getEnd(view));
            }
        }

        if (minStart != Integer.MAX_VALUE)
            removedLength = maxEnd - minStart;
        return removedLength;
    }

    @Override
    public int getDeletingItemsOnScreenCount() {
        return deletingItemsOnScreenCount;
    }

    @Override
    public void reset() {
        deletingItemsOnScreenCount = 0;
    }
}

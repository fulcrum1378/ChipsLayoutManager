package ir.mahdiparastesh.chlm;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import ir.mahdiparastesh.chlm.anchor.AnchorViewState;
import ir.mahdiparastesh.chlm.layouter.ICanvas;
import ir.mahdiparastesh.chlm.layouter.IStateFactory;

abstract class ScrollingController implements IScrollingController {
    private final ChipsLayoutManager lm;
    private final IScrollerListener scrollerListener;
    private final IStateFactory stateFactory;
    ICanvas canvas;

    interface IScrollerListener {
        void onScrolled(IScrollingController scrollingController, RecyclerView.Recycler recycler,
                        RecyclerView.State state);
    }

    ScrollingController(ChipsLayoutManager layoutManager, IStateFactory stateFactory, IScrollerListener scrollerListener) {
        this.lm = layoutManager;
        this.scrollerListener = scrollerListener;
        this.stateFactory = stateFactory;
        this.canvas = layoutManager.getCanvas();
    }

    final int calculateEndGap() {
        if (lm.getChildCount() == 0) return 0;

        int visibleViewsCount = lm.getCompletelyVisibleViewsCount();

        if (visibleViewsCount == lm.getItemCount()) return 0;
        int currentEnd = stateFactory.getEndViewBound();
        int desiredEnd = stateFactory.getEndAfterPadding();

        int diff = desiredEnd - currentEnd;
        return Math.max(diff, 0);
    }

    final int calculateStartGap() {
        if (lm.getChildCount() == 0) return 0;
        int currentStart = stateFactory.getStartViewBound();
        int desiredStart = stateFactory.getStartAfterPadding();
        int diff = currentStart - desiredStart;
        return Math.max(diff, 0);
    }

    @Override
    public final boolean normalizeGaps(RecyclerView.Recycler recycler, RecyclerView.State state) {
        int backwardGap = calculateStartGap();
        if (backwardGap > 0) {
            offsetChildren(-backwardGap);
            return true;
        }

        int forwardGap = calculateEndGap();
        if (forwardGap > 0) {
            scrollBy(-forwardGap, recycler, state);
            return true;
        }

        return false;
    }

    final int calcOffset(int d) {
        int childCount = lm.getChildCount();
        if (childCount == 0) return 0;

        int delta = 0;
        if (d < 0) {
            delta = onContentScrolledBackward(d);
        } else if (d > 0) {
            delta = onContentScrolledForward(d);
        }
        return delta;
    }

    final int onContentScrolledBackward(int d) {
        int delta;

        AnchorViewState anchor = lm.getAnchor();
        if (anchor.getAnchorViewRect() == null) {
            return 0;
        }

        if (anchor.getPosition() != 0) {
            delta = d;
        } else {
            int startBorder = stateFactory.getStartAfterPadding();
            int viewStart = stateFactory.getStart(anchor);
            int distance;
            distance = viewStart - startBorder;

            if (distance >= 0) delta = distance;
            else delta = Math.max(distance, d);
        }
        return delta;
    }

    final int onContentScrolledForward(int d) {
        int childCount = lm.getChildCount();
        int itemCount = lm.getItemCount();
        int delta;

        View lastView = lm.getChildAt(childCount - 1);
        int lastViewAdapterPos = lm.getPosition(lastView);
        if (lastViewAdapterPos < itemCount - 1) {
            delta = d;
        } else {
            int viewEnd = stateFactory.getEndViewBound();
            int parentEnd = stateFactory.getEndAfterPadding();
            delta = Math.min(viewEnd - parentEnd, d);
        }
        return delta;
    }

    abstract void offsetChildren(int d);

    @Override
    public final int scrollHorizontallyBy(int d, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return canScrollHorizontally()? scrollBy(d, recycler, state) : 0;
    }

    @Override
    public final int scrollVerticallyBy(int d, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return canScrollVertically()? scrollBy(d, recycler, state) : 0;
    }

    private int scrollBy(int d, RecyclerView.Recycler recycler, RecyclerView.State state) {
        d = calcOffset(d);
        offsetChildren(-d);

        scrollerListener.onScrolled(this, recycler, state);

        return d;
    }

    private int getLaidOutArea() {
        return stateFactory.getEndViewBound() -
                stateFactory.getStartViewBound();
    }

    private int computeScrollOffset(RecyclerView.State state) {
        if (lm.getChildCount() == 0 || state.getItemCount() == 0) {
            return 0;
        }

        int firstVisiblePos = lm.findFirstVisibleItemPosition();
        int lastVisiblePos = lm.findLastVisibleItemPosition();
        final int itemsBefore = Math.max(0, firstVisiblePos);

        if (!lm.isSmoothScrollbarEnabled()) {
            return itemsBefore;
        }

        final int itemRange = Math.abs(firstVisiblePos - lastVisiblePos) + 1;

        final float avgSizePerRow = (float) getLaidOutArea() / itemRange;

        return Math.round(itemsBefore * avgSizePerRow +
                (stateFactory.getStartAfterPadding() - stateFactory.getStartViewBound()));
    }

    private int computeScrollExtent(RecyclerView.State state) {
        if (lm.getChildCount() == 0 || state.getItemCount() == 0) {
            return 0;
        }

        int firstVisiblePos = lm.findFirstVisibleItemPosition();
        int lastVisiblePos = lm.findLastVisibleItemPosition();

        if (!lm.isSmoothScrollbarEnabled())
            return Math.abs(lastVisiblePos - firstVisiblePos) + 1;

        return Math.min(stateFactory.getTotalSpace(), getLaidOutArea());
    }

    private int computeScrollRange(RecyclerView.State state) {
        if (lm.getChildCount() == 0 || state.getItemCount() == 0) {
            return 0;
        }

        if (!lm.isSmoothScrollbarEnabled()) {
            return state.getItemCount();
        }

        int firstVisiblePos = lm.findFirstVisibleItemPosition();
        int lastVisiblePos = lm.findLastVisibleItemPosition();

        final int laidOutRange = Math.abs(firstVisiblePos - lastVisiblePos) + 1;

        return (int) ((float) getLaidOutArea() / laidOutRange * state.getItemCount());
    }

    @Override
    public final int computeVerticalScrollExtent(RecyclerView.State state) {
        return canScrollVertically() ? computeScrollExtent(state) : 0;
    }

    @Override
    public final int computeVerticalScrollRange(RecyclerView.State state) {
        return canScrollVertically() ? computeScrollRange(state) : 0;
    }

    @Override
    public final int computeVerticalScrollOffset(RecyclerView.State state) {
        return canScrollVertically() ? computeScrollOffset(state) : 0;
    }

    @Override
    public final int computeHorizontalScrollRange(RecyclerView.State state) {
        return canScrollHorizontally() ? computeScrollRange(state) : 0;
    }

    @Override
    public final int computeHorizontalScrollOffset(RecyclerView.State state) {
        return canScrollHorizontally() ? computeScrollOffset(state) : 0;
    }

    @Override
    public final int computeHorizontalScrollExtent(RecyclerView.State state) {
        return canScrollHorizontally() ? computeScrollExtent(state) : 0;
    }
}

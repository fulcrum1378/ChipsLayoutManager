package ir.mahdiparastesh.chlm;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ir.mahdiparastesh.chlm.anchor.AnchorViewState;

public interface IScrollingController {

    RecyclerView.SmoothScroller createSmoothScroller(@NonNull Context context, int position,
                                                     int timeMs, AnchorViewState anchor);

    boolean canScrollVertically();

    boolean canScrollHorizontally();

    int scrollVerticallyBy(int d, RecyclerView.Recycler recycler, RecyclerView.State state);

    int scrollHorizontallyBy(int d, RecyclerView.Recycler recycler, RecyclerView.State state);

    boolean normalizeGaps(RecyclerView.Recycler recycler, RecyclerView.State state);

    int computeVerticalScrollOffset(RecyclerView.State state);

    int computeVerticalScrollExtent(RecyclerView.State state);

    int computeVerticalScrollRange(RecyclerView.State state);

    int computeHorizontalScrollOffset(RecyclerView.State state);

    int computeHorizontalScrollExtent(RecyclerView.State state);

    int computeHorizontalScrollRange(RecyclerView.State state);
}

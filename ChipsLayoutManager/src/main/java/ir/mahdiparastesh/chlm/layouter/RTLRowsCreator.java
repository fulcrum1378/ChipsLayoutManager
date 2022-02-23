package ir.mahdiparastesh.chlm.layouter;

import android.graphics.Rect;

import androidx.recyclerview.widget.RecyclerView;

import ir.mahdiparastesh.chlm.anchor.AnchorViewState;

class RTLRowsCreator implements ILayouterCreator {

    private final RecyclerView.LayoutManager layoutManager;

    RTLRowsCreator(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public Rect createOffsetRectForBackwardLayouter(AnchorViewState anchor) {
        Rect anchorRect = anchor.getAnchorViewRect();
        return new Rect(
                anchorRect == null ? 0 : anchorRect.right,
                anchorRect == null ? 0 : anchorRect.top,
                0,
                anchorRect == null ? 0 : anchorRect.bottom);
    }

    @Override
    public AbstractLayouter.Builder createBackwardBuilder() {
        return RTLUpLayouter.newBuilder();
    }

    @Override
    public AbstractLayouter.Builder createForwardBuilder() {
        return RTLDownLayouter.newBuilder();
    }

    @Override
    public Rect createOffsetRectForForwardLayouter(AnchorViewState anchor) {
        Rect anchorRect = anchor.getAnchorViewRect();

        return new Rect(
                0,
                anchorRect == null ? anchor.getPosition() == 0 ? layoutManager.getPaddingTop()
                        : 0 : anchorRect.top,
                anchorRect == null ? layoutManager.getPaddingRight() : anchorRect.right,
                anchorRect == null ? anchor.getPosition() == 0 ? layoutManager.getPaddingBottom()
                        : 0 : anchorRect.bottom);
    }
}

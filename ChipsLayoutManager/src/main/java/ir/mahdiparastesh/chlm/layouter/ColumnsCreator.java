package ir.mahdiparastesh.chlm.layouter;

import android.graphics.Rect;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ir.mahdiparastesh.chlm.anchor.AnchorViewState;

class ColumnsCreator implements ILayouterCreator {

    private final RecyclerView.LayoutManager layoutManager;

    ColumnsCreator(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public AbstractLayouter.Builder createBackwardBuilder() {
        return LeftLayouter.newBuilder();
    }

    @Override
    public AbstractLayouter.Builder createForwardBuilder() {
        return RightLayouter.newBuilder();
    }

    @Override
    public Rect createOffsetRectForBackwardLayouter(@NonNull AnchorViewState anchor) {
        Rect anchorRect = anchor.getAnchorViewRect();

        return new Rect(
                anchorRect == null ? 0 : anchorRect.left,
                0,
                anchorRect == null ? 0 : anchorRect.right,
                anchorRect == null ? 0 : anchorRect.top);
    }

    @Override
    public Rect createOffsetRectForForwardLayouter(@NonNull AnchorViewState anchor) {
        Rect anchorRect = anchor.getAnchorViewRect();

        return new Rect(
                anchorRect == null ? anchor.getPosition() == 0
                        ? layoutManager.getPaddingLeft() : 0 : anchorRect.left,
                anchorRect == null ? layoutManager.getPaddingTop() : anchorRect.top,
                anchorRect == null ? anchor.getPosition() == 0
                        ? layoutManager.getPaddingRight() : 0 : anchorRect.right,
                0);
    }
}

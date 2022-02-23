package ir.mahdiparastesh.chlm.anchor;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import ir.mahdiparastesh.chlm.ChildViewsIterable;
import ir.mahdiparastesh.chlm.layouter.ICanvas;

public class RowsAnchorFactory extends AbstractAnchorFactory {

    private final ChildViewsIterable childViews;

    public RowsAnchorFactory(RecyclerView.LayoutManager lm, ICanvas canvas) {
        super(lm, canvas);
        childViews = new ChildViewsIterable(lm);
    }

    @Override
    public AnchorViewState getAnchor() {

        AnchorViewState minPosView = AnchorViewState.getNotFoundState();

        int minPosition = Integer.MAX_VALUE;
        int minTop = Integer.MAX_VALUE;

        for (View view : childViews) {
            AnchorViewState anchorViewState = createAnchorState(view);
            int pos = lm.getPosition(view);
            int top = lm.getDecoratedTop(view);

            Rect viewRect = new Rect(anchorViewState.getAnchorViewRect());

            if (getCanvas().isInside(viewRect) && !anchorViewState.isRemoving()) {
                if (minPosition > pos) {
                    minPosition = pos;
                    minPosView = anchorViewState;
                }

                if (minTop > top) {
                    minTop = top;
                }
            }
        }

        if (!minPosView.isNotFoundState()) {
            minPosView.getAnchorViewRect().top = minTop;
            minPosView.setPosition(minPosition);
        }
        return minPosView;
    }

    @Override
    public void resetRowCoordinates(AnchorViewState anchorView) {
        if (!anchorView.isNotFoundState()) {
            Rect rect = anchorView.getAnchorViewRect();
            rect.left = getCanvas().getCanvasLeftBorder();
            rect.right = getCanvas().getCanvasRightBorder();
        }
    }
}

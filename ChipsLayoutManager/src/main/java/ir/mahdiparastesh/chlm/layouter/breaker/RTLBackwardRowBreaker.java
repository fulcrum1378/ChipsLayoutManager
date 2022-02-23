package ir.mahdiparastesh.chlm.layouter.breaker;

import ir.mahdiparastesh.chlm.layouter.AbstractLayouter;

class RTLBackwardRowBreaker implements ILayoutRowBreaker {
    @Override
    public boolean isRowBroke(AbstractLayouter al) {
        return al.getViewLeft() + al.getCurrentViewWidth() > al.getCanvasRightBorder()
                && al.getViewLeft() > al.getCanvasLeftBorder();
    }
}

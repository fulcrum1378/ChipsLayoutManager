package ir.mahdiparastesh.chlm.layouter.breaker;

import ir.mahdiparastesh.chlm.layouter.AbstractLayouter;


class RTLForwardRowBreaker implements ILayoutRowBreaker {

    @Override
    public boolean isRowBroke(AbstractLayouter al) {
        return al.getViewRight() < al.getCanvasRightBorder()
                && al.getViewRight() - al.getCurrentViewWidth() < al.getCanvasLeftBorder();

    }
}

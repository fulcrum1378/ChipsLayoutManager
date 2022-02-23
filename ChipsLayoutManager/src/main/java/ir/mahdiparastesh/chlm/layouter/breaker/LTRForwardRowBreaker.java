package ir.mahdiparastesh.chlm.layouter.breaker;

import ir.mahdiparastesh.chlm.layouter.AbstractLayouter;

class LTRForwardRowBreaker implements ILayoutRowBreaker {
    @Override
    public boolean isRowBroke(AbstractLayouter al) {
        return al.getViewLeft() > al.getCanvasLeftBorder()
                && al.getViewLeft() + al.getCurrentViewWidth() > al.getCanvasRightBorder();
    }
}

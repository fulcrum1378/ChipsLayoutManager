package ir.mahdiparastesh.chlm.gravity;

import ir.mahdiparastesh.chlm.layouter.AbstractLayouter;

abstract class GravityUtil {
    static int getHorizontalDifference(AbstractLayouter layouter) {
        return layouter.getCanvasRightBorder() - layouter.getCanvasLeftBorder() - layouter.getRowLength();
    }

    static int getVerticalDifference(AbstractLayouter layouter) {
        return layouter.getCanvasBottomBorder() - layouter.getCanvasTopBorder() - layouter.getRowLength();
    }
}

package ir.mahdiparastesh.chlm.gravity;

import android.graphics.Rect;

import java.util.List;

import ir.mahdiparastesh.chlm.layouter.AbstractLayouter;
import ir.mahdiparastesh.chlm.layouter.Item;

class ColumnFillSpaceStrategy implements IRowStrategy {
    @Override
    public void applyStrategy(AbstractLayouter abstractLayouter, List<Item> row) {
        if (abstractLayouter.getRowSize() == 1) return;
        int difference = GravityUtil.getVerticalDifference(abstractLayouter)
                / (abstractLayouter.getRowSize() - 1);
        int offsetDifference = 0;

        for (Item item : row) {
            Rect childRect = item.getViewRect();

            if (childRect.top == abstractLayouter.getCanvasTopBorder()) {
                int topDif = childRect.top - abstractLayouter.getCanvasTopBorder();
                childRect.top = abstractLayouter.getCanvasTopBorder();
                childRect.bottom -= topDif;
                continue;
            }
            offsetDifference += difference;

            childRect.top += offsetDifference;
            childRect.bottom += offsetDifference;
        }
    }
}

package ir.mahdiparastesh.chlm.gravity;

import android.graphics.Rect;

import java.util.List;

import ir.mahdiparastesh.chlm.layouter.AbstractLayouter;
import ir.mahdiparastesh.chlm.layouter.Item;

class ColumnFillStrategy implements IRowStrategy {
    @Override
    public void applyStrategy(AbstractLayouter abstractLayouter, List<Item> row) {
        int difference = GravityUtil.getVerticalDifference(abstractLayouter)
                / abstractLayouter.getRowSize();
        int offsetDifference = difference;

        for (Item item : row) {
            Rect childRect = item.getViewRect();

            if (childRect.top == abstractLayouter.getCanvasTopBorder()) {
                int topDif = childRect.top - abstractLayouter.getCanvasTopBorder();
                childRect.top = abstractLayouter.getCanvasTopBorder();
                childRect.bottom -= topDif;
                childRect.bottom += offsetDifference;
                continue;
            }
            childRect.top += offsetDifference;
            offsetDifference += difference;
            childRect.bottom += offsetDifference;
        }
    }
}

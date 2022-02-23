package ir.mahdiparastesh.chlm.gravity;

import android.graphics.Rect;

import java.util.List;

import ir.mahdiparastesh.chlm.layouter.AbstractLayouter;
import ir.mahdiparastesh.chlm.layouter.Item;

class ColumnFillSpaceCenterStrategy implements IRowStrategy {

    @Override
    public void applyStrategy(AbstractLayouter abstractLayouter, List<Item> row) {
        int difference = GravityUtil.getVerticalDifference(abstractLayouter)
                / (abstractLayouter.getRowSize() + 1);
        int offsetDifference = 0;
        for (Item item : row) {
            Rect childRect = item.getViewRect();
            offsetDifference += difference;
            childRect.top += offsetDifference;
            childRect.bottom += offsetDifference;
        }
    }
}

package ir.mahdiparastesh.chlm.gravity;

import android.graphics.Rect;

import java.util.List;

import ir.mahdiparastesh.chlm.layouter.AbstractLayouter;
import ir.mahdiparastesh.chlm.layouter.Item;

class LTRRowFillSpaceCenterStrategy implements IRowStrategy {
    @Override
    public void applyStrategy(AbstractLayouter abstractLayouter, List<Item> row) {
        int difference = GravityUtil.getHorizontalDifference(abstractLayouter)
                / (abstractLayouter.getRowSize() + 1);
        int offsetDifference = 0;
        for (Item item : row) {
            Rect childRect = item.getViewRect();
            offsetDifference += difference;
            childRect.left += offsetDifference;
            childRect.right += offsetDifference;
        }
    }
}

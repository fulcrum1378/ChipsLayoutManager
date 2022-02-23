package ir.mahdiparastesh.chlm.gravity;

import android.graphics.Rect;

import java.util.List;

import ir.mahdiparastesh.chlm.layouter.AbstractLayouter;
import ir.mahdiparastesh.chlm.layouter.Item;

class RTLRowFillSpaceCenterDenseStrategy implements IRowStrategy {
    @Override
    public void applyStrategy(AbstractLayouter abstractLayouter, List<Item> row) {
        int difference = GravityUtil.getHorizontalDifference(abstractLayouter) / 2;
        for (Item item : row) {
            Rect childRect = item.getViewRect();
            childRect.left -= difference;
            childRect.right -= difference;
        }
    }
}

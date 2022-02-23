package ir.mahdiparastesh.chlm.gravity;

import android.graphics.Rect;

import java.util.List;

import ir.mahdiparastesh.chlm.layouter.AbstractLayouter;
import ir.mahdiparastesh.chlm.layouter.Item;

class LTRRowFillStrategy implements IRowStrategy {
    @Override
    public void applyStrategy(AbstractLayouter abstractLayouter, List<Item> row) {
        int difference = GravityUtil.getHorizontalDifference(abstractLayouter)
                / abstractLayouter.getRowSize();
        int offsetDifference = difference;
        for (Item item : row) {
            Rect childRect = item.getViewRect();
            if (childRect.left == abstractLayouter.getCanvasLeftBorder()) {
                int leftDif = childRect.left - abstractLayouter.getCanvasLeftBorder();
                childRect.left = abstractLayouter.getCanvasLeftBorder();
                childRect.right -= leftDif;
                childRect.right += offsetDifference;
                continue;
            }
            childRect.left += offsetDifference;
            offsetDifference += difference;
            childRect.right += offsetDifference;
        }
    }
}

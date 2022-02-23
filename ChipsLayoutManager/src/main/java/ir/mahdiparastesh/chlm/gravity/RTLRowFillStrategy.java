package ir.mahdiparastesh.chlm.gravity;

import android.graphics.Rect;

import java.util.List;

import ir.mahdiparastesh.chlm.layouter.AbstractLayouter;
import ir.mahdiparastesh.chlm.layouter.Item;

class RTLRowFillStrategy implements IRowStrategy {
    @Override
    public void applyStrategy(AbstractLayouter abstractLayouter, List<Item> row) {
        int difference = GravityUtil.getHorizontalDifference(abstractLayouter)
                / abstractLayouter.getRowSize();
        int offsetDifference = difference;
        for (Item item : row) {
            Rect childRect = item.getViewRect();
            if (childRect.right == abstractLayouter.getCanvasRightBorder()) {
                int rightDif = abstractLayouter.getCanvasRightBorder() - childRect.right;
                childRect.left += rightDif;
                childRect.right = abstractLayouter.getCanvasRightBorder();
                childRect.left -= offsetDifference;
                continue;
            }
            childRect.right -= offsetDifference;
            offsetDifference += difference;
            childRect.left -= offsetDifference;
        }
    }
}

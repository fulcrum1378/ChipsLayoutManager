package ir.mahdiparastesh.chlm.gravity;

import android.graphics.Rect;

import java.util.List;

import ir.mahdiparastesh.chlm.layouter.AbstractLayouter;
import ir.mahdiparastesh.chlm.layouter.Item;

class RTLRowFillSpaceStrategy implements IRowStrategy {
    @Override
    public void applyStrategy(AbstractLayouter abstractLayouter, List<Item> row) {
        if (abstractLayouter.getRowSize() == 1) return;
        int difference = GravityUtil.getHorizontalDifference(abstractLayouter)
                / (abstractLayouter.getRowSize() - 1);
        int offsetDifference = 0;
        for (Item item : row) {
            Rect childRect = item.getViewRect();
            if (childRect.right == abstractLayouter.getCanvasRightBorder()) {
                int rightDif = abstractLayouter.getCanvasRightBorder() - childRect.right;
                childRect.left += rightDif;
                childRect.right = abstractLayouter.getCanvasRightBorder();
                continue;
            }
            offsetDifference += difference;
            childRect.right -= offsetDifference;
            childRect.left -= offsetDifference;
        }
    }
}

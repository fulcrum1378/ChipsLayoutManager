package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;
import com.beloo.widget.chipslayoutmanager.layouter.Item;

import java.util.List;

class LTRRowFillSpaceCenterStrategy implements IRowStrategy {

    @Override
    public void applyStrategy(AbstractLayouter abstractLayouter, List<Item> row) {
        int difference = GravityUtil.getHorizontalDifference(abstractLayouter) / (abstractLayouter.getRowSize() + 1);
        int offsetDifference = 0;

        for (Item item : row) {
            Rect childRect = item.getViewRect();


//            if (childRect.left == abstractLayouter.getCanvasLeftBorder()) {
//                //left view of row
//                offsetDifference += difference/2;
//
//                childRect.left += offsetDifference;
//                childRect.right += offsetDifference;
//                continue;
//            }

            offsetDifference += difference;

            childRect.left += offsetDifference;
            childRect.right += offsetDifference;
        }
    }
}

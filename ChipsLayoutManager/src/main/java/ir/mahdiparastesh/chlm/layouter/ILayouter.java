package ir.mahdiparastesh.chlm.layouter;

import android.graphics.Rect;
import android.view.View;

import java.util.List;

public interface ILayouter {

    void layoutRow();

    boolean placeView(View view);

    boolean onAttachView(View view);

    int getRowSize();

    int getViewTop();

    int getViewBottom();

    int getPreviousRowSize();

    List<Item> getCurrentRowItems();

    Rect getRowRect();

    void addLayouterListener(ILayouterListener layouterListener);

    void removeLayouterListener(ILayouterListener layouterListener);

    AbstractPositionIterator positionIterator();
}

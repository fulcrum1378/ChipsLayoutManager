package ir.mahdiparastesh.chlm.layouter;

import androidx.recyclerview.widget.RecyclerView;

class RowSquare extends Square {

    RowSquare(RecyclerView.LayoutManager lm) {
        super(lm);
    }

    public final int getCanvasRightBorder() {
        return lm.getWidth() - lm.getPaddingRight();
    }

    public final int getCanvasBottomBorder() {
        return lm.getHeight();
    }

    public final int getCanvasLeftBorder() {
        return lm.getPaddingLeft();
    }

    public final int getCanvasTopBorder() {
        return 0;
    }
}

package ir.mahdiparastesh.chlm.layouter.placer;

import androidx.recyclerview.widget.RecyclerView;

abstract class AbstractPlacer implements IPlacer {
    private final RecyclerView.LayoutManager layoutManager;

    AbstractPlacer(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return layoutManager;
    }
}

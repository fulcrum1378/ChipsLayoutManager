package ir.mahdiparastesh.chlm.layouter.placer;

import androidx.recyclerview.widget.RecyclerView;

class DisappearingPlacerFactory implements IPlacerFactory {

    private final RecyclerView.LayoutManager lm;

    DisappearingPlacerFactory(RecyclerView.LayoutManager layoutManager) {
        this.lm = layoutManager;
    }

    @Override
    public IPlacer getAtStartPlacer() {
        return new DisappearingViewAtStartPlacer(lm);
    }

    @Override
    public IPlacer getAtEndPlacer() {
        return new DisappearingViewAtEndPlacer(lm);
    }
}

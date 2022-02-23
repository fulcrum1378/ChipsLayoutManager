package ir.mahdiparastesh.chlm.layouter.placer;

import ir.mahdiparastesh.chlm.ChipsLayoutManager;

public class PlacerFactory {
    private final ChipsLayoutManager lm;

    public PlacerFactory(ChipsLayoutManager lm) {
        this.lm = lm;
    }

    public IPlacerFactory createRealPlacerFactory() {
        return new RealPlacerFactory(lm);
    }

    public IPlacerFactory createDisappearingPlacerFactory() {
        return new DisappearingPlacerFactory(lm);
    }
}

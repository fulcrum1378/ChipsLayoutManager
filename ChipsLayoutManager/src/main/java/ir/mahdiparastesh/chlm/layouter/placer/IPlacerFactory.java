package ir.mahdiparastesh.chlm.layouter.placer;

public interface IPlacerFactory {
    IPlacer getAtStartPlacer();

    IPlacer getAtEndPlacer();
}

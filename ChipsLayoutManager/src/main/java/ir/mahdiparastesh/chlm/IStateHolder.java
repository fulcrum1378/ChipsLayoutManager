package ir.mahdiparastesh.chlm;

interface IStateHolder {
    boolean isLayoutRTL();

    @Orientation
    int layoutOrientation();
}

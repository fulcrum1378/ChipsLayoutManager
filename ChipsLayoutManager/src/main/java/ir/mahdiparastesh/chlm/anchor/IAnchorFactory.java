package ir.mahdiparastesh.chlm.anchor;

public interface IAnchorFactory {

    AnchorViewState getAnchor();

    AnchorViewState createNotFound();


    void resetRowCoordinates(AnchorViewState anchorView);
}

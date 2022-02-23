package ir.mahdiparastesh.chlm.layouter;

import android.view.View;

import ir.mahdiparastesh.chlm.IScrollingController;
import ir.mahdiparastesh.chlm.anchor.AnchorViewState;
import ir.mahdiparastesh.chlm.anchor.IAnchorFactory;
import ir.mahdiparastesh.chlm.layouter.criteria.AbstractCriteriaFactory;
import ir.mahdiparastesh.chlm.layouter.criteria.ICriteriaFactory;
import ir.mahdiparastesh.chlm.layouter.placer.IPlacerFactory;

public interface IStateFactory {
    LayouterFactory createLayouterFactory(ICriteriaFactory criteriaFactory, IPlacerFactory placerFactory);

    AbstractCriteriaFactory createDefaultFinishingCriteriaFactory();

    IAnchorFactory anchorFactory();

    IScrollingController scrollingController();

    ICanvas createCanvas();

    int getSizeMode();

    int getStart();

    int getStart(View view);

    int getStart(AnchorViewState anchor);

    int getStartAfterPadding();

    int getStartViewPosition();

    int getStartViewBound();

    int getEnd();

    int getEnd(View view);

    int getEndAfterPadding();

    int getEnd(AnchorViewState anchor);

    int getEndViewPosition();

    int getEndViewBound();

    int getTotalSpace();
}

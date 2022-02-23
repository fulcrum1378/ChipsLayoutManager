package ir.mahdiparastesh.chlm.layouter.criteria;

import ir.mahdiparastesh.chlm.layouter.AbstractLayouter;
import ir.mahdiparastesh.chlm.layouter.ILayouter;
import ir.mahdiparastesh.chlm.layouter.ILayouterListener;
import ir.mahdiparastesh.chlm.layouter.Item;

public class CriteriaPolitePositionReached extends FinishingCriteriaDecorator
        implements IFinishingCriteria, ILayouterListener {

    private boolean isPositionReached;
    private final int reachedPosition;

    CriteriaPolitePositionReached(AbstractLayouter abstractLayouter,
                                  IFinishingCriteria finishingCriteria, int reachedPosition) {
        super(finishingCriteria);
        this.reachedPosition = reachedPosition;
        abstractLayouter.addLayouterListener(this);
    }

    @Override
    public boolean isFinishedLayouting(AbstractLayouter abstractLayouter) {
        boolean isFinishedFlow = super.isFinishedLayouting(abstractLayouter);
        return isFinishedFlow || isPositionReached;
    }

    @Override
    public void onLayoutRow(ILayouter layouter) {
        if (isPositionReached) return;
        if (layouter.getRowSize() == 0) return;
        for (Item item : layouter.getCurrentRowItems())
            if (item.getViewPosition() == reachedPosition) {
                isPositionReached = true;
                return;
            }
    }
}

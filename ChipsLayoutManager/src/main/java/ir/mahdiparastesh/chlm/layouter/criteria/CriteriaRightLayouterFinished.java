package ir.mahdiparastesh.chlm.layouter.criteria;

import ir.mahdiparastesh.chlm.layouter.AbstractLayouter;

class CriteriaRightLayouterFinished implements IFinishingCriteria {

    @Override
    public boolean isFinishedLayouting(AbstractLayouter abstractLayouter) {
        return abstractLayouter.getViewLeft() >= abstractLayouter.getCanvasRightBorder();
    }
}

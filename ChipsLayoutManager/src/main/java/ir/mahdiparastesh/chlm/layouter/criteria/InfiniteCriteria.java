package ir.mahdiparastesh.chlm.layouter.criteria;

import ir.mahdiparastesh.chlm.layouter.AbstractLayouter;


class InfiniteCriteria implements IFinishingCriteria {

    @Override
    public boolean isFinishedLayouting(AbstractLayouter abstractLayouter) {
        return false;
    }

}

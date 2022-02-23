package ir.mahdiparastesh.chlm.layouter.criteria;

import ir.mahdiparastesh.chlm.layouter.AbstractLayouter;

abstract class FinishingCriteriaDecorator implements IFinishingCriteria {
    private final IFinishingCriteria finishingCriteria;

    FinishingCriteriaDecorator(IFinishingCriteria finishingCriteria) {
        this.finishingCriteria = finishingCriteria;
    }

    @Override
    public boolean isFinishedLayouting(AbstractLayouter abstractLayouter) {
        return finishingCriteria.isFinishedLayouting(abstractLayouter);
    }
}

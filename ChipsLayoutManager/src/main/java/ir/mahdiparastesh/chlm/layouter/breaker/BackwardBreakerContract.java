package ir.mahdiparastesh.chlm.layouter.breaker;

import ir.mahdiparastesh.chlm.layouter.AbstractLayouter;

class BackwardBreakerContract extends RowBreakerDecorator {

    private final IRowBreaker breaker;

    BackwardBreakerContract(IRowBreaker breaker, ILayoutRowBreaker decorate) {
        super(decorate);
        this.breaker = breaker;
    }

    @Override
    public boolean isRowBroke(AbstractLayouter al) {
        return super.isRowBroke(al) ||
                breaker.isItemBreakRow(al.getCurrentViewPosition());
    }
}

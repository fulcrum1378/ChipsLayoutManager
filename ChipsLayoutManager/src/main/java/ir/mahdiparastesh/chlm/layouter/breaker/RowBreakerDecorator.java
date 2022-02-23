package ir.mahdiparastesh.chlm.layouter.breaker;

import ir.mahdiparastesh.chlm.layouter.AbstractLayouter;

class RowBreakerDecorator implements ILayoutRowBreaker {

    private final ILayoutRowBreaker decorate;

    RowBreakerDecorator(ILayoutRowBreaker decorate) {
        this.decorate = decorate;
    }

    @Override
    public boolean isRowBroke(AbstractLayouter al) {
        return decorate.isRowBroke(al);
    }
}

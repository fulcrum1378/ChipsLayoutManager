package ir.mahdiparastesh.chlm.layouter;

import androidx.recyclerview.widget.RecyclerView;

import ir.mahdiparastesh.chlm.gravity.IRowStrategyFactory;
import ir.mahdiparastesh.chlm.gravity.LTRRowStrategyFactory;
import ir.mahdiparastesh.chlm.layouter.breaker.IBreakerFactory;
import ir.mahdiparastesh.chlm.layouter.breaker.LTRRowBreakerFactory;

class LTRRowsOrientationStateFactory implements IOrientationStateFactory {

    @Override
    public ILayouterCreator createLayouterCreator(RecyclerView.LayoutManager lm) {
        return new LTRRowsCreator(lm);
    }

    @Override
    public IRowStrategyFactory createRowStrategyFactory() {
        return new LTRRowStrategyFactory();
    }

    @Override
    public IBreakerFactory createDefaultBreaker() {
        return new LTRRowBreakerFactory();
    }
}

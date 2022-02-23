package ir.mahdiparastesh.chlm.layouter;

import androidx.recyclerview.widget.RecyclerView;

import ir.mahdiparastesh.chlm.gravity.IRowStrategyFactory;
import ir.mahdiparastesh.chlm.gravity.RTLRowStrategyFactory;
import ir.mahdiparastesh.chlm.layouter.breaker.IBreakerFactory;
import ir.mahdiparastesh.chlm.layouter.breaker.RTLRowBreakerFactory;

class RTLRowsOrientationStateFactory implements IOrientationStateFactory {

    @Override
    public ILayouterCreator createLayouterCreator(RecyclerView.LayoutManager lm) {
        return new RTLRowsCreator(lm);
    }

    @Override
    public IRowStrategyFactory createRowStrategyFactory() {
        return new RTLRowStrategyFactory();
    }

    @Override
    public IBreakerFactory createDefaultBreaker() {
        return new RTLRowBreakerFactory();
    }
}

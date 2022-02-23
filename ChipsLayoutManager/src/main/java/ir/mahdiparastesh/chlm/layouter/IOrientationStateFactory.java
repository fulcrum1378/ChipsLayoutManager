package ir.mahdiparastesh.chlm.layouter;

import androidx.recyclerview.widget.RecyclerView;

import ir.mahdiparastesh.chlm.gravity.IRowStrategyFactory;
import ir.mahdiparastesh.chlm.layouter.breaker.IBreakerFactory;

interface IOrientationStateFactory {
    ILayouterCreator createLayouterCreator(RecyclerView.LayoutManager lm);

    IRowStrategyFactory createRowStrategyFactory();

    IBreakerFactory createDefaultBreaker();
}

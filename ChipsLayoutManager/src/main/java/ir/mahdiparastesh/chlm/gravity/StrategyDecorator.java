package ir.mahdiparastesh.chlm.gravity;

import androidx.annotation.NonNull;

import java.util.List;

import ir.mahdiparastesh.chlm.layouter.AbstractLayouter;
import ir.mahdiparastesh.chlm.layouter.Item;

class StrategyDecorator implements IRowStrategy {

    @NonNull
    private final IRowStrategy rowStrategy;

    StrategyDecorator(@NonNull IRowStrategy rowStrategy) {
        this.rowStrategy = rowStrategy;
    }

    @Override
    public void applyStrategy(AbstractLayouter abstractLayouter, List<Item> row) {
        rowStrategy.applyStrategy(abstractLayouter, row);
    }
}

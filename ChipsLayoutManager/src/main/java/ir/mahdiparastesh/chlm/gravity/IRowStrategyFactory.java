package ir.mahdiparastesh.chlm.gravity;

import ir.mahdiparastesh.chlm.RowStrategy;

public interface IRowStrategyFactory {
    IRowStrategy createRowStrategy(@RowStrategy int rowStrategy);
}

package ir.mahdiparastesh.chlm;

import androidx.annotation.IntRange;

import ir.mahdiparastesh.chlm.layouter.breaker.IRowBreaker;

interface IChipsLayoutManagerContract extends IPositionsContract, IScrollingContract {

    void setScrollingEnabledContract(boolean isEnabled);

    void setMaxViewsInRow(@IntRange(from = 1) Integer maxViewsInRow);

    Integer getMaxViewsInRow();

    IRowBreaker getRowBreaker();

    @RowStrategy
    int getRowStrategyType();

    @Orientation
    int layoutOrientation();

    boolean isScrollingEnabledContract();
}

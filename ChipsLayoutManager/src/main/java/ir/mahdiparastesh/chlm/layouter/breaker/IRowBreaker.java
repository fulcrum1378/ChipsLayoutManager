package ir.mahdiparastesh.chlm.layouter.breaker;

import androidx.annotation.IntRange;

public interface IRowBreaker {
    boolean isItemBreakRow(@IntRange(from = 0) int position);
}

package ir.mahdiparastesh.chlm.util;

import androidx.recyclerview.widget.RecyclerView;

public class LayoutManagerUtil {
    public static void requestLayoutWithAnimations(final RecyclerView.LayoutManager lm) {
        lm.postOnAnimation(() -> {
            lm.requestLayout();
            lm.requestSimpleAnimationsInNextLayout();
        });
    }
}

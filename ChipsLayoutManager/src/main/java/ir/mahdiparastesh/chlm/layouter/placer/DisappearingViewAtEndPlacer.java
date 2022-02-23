package ir.mahdiparastesh.chlm.layouter.placer;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

class DisappearingViewAtEndPlacer extends AbstractPlacer {
    DisappearingViewAtEndPlacer(RecyclerView.LayoutManager layoutManager) {
        super(layoutManager);
    }

    @Override
    public void addView(View view) {
        getLayoutManager().addDisappearingView(view);
    }
}

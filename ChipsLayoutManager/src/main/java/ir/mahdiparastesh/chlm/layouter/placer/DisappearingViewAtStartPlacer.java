package ir.mahdiparastesh.chlm.layouter.placer;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

class DisappearingViewAtStartPlacer extends AbstractPlacer {
    DisappearingViewAtStartPlacer(RecyclerView.LayoutManager layoutManager) {
        super(layoutManager);
    }

    @Override
    public void addView(View view) {
        getLayoutManager().addDisappearingView(view, 0);
    }
}

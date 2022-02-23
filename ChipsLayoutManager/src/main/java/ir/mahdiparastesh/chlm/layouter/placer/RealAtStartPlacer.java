package ir.mahdiparastesh.chlm.layouter.placer;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

class RealAtStartPlacer extends AbstractPlacer implements IPlacer {
    RealAtStartPlacer(RecyclerView.LayoutManager layoutManager) {
        super(layoutManager);
    }

    @Override
    public void addView(View view) {
        getLayoutManager().addView(view, 0);
    }
}

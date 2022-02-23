package ir.mahdiparastesh.chlm.layouter.placer;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

class RealAtEndPlacer extends AbstractPlacer implements IPlacer {
    RealAtEndPlacer(RecyclerView.LayoutManager layoutManager) {
        super(layoutManager);
    }

    @Override
    public void addView(View view) {
        getLayoutManager().addView(view);
    }
}

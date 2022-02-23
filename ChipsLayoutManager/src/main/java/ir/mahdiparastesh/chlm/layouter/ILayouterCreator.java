package ir.mahdiparastesh.chlm.layouter;

import android.graphics.Rect;

import androidx.annotation.NonNull;

import ir.mahdiparastesh.chlm.anchor.AnchorViewState;

interface ILayouterCreator {
    Rect createOffsetRectForBackwardLayouter(@NonNull AnchorViewState anchorRect);

    AbstractLayouter.Builder createBackwardBuilder();

    AbstractLayouter.Builder createForwardBuilder();

    Rect createOffsetRectForForwardLayouter(AnchorViewState anchorRect);
}

package ir.mahdiparastesh.chlm.gravity;

import android.graphics.Rect;

import androidx.annotation.IntRange;

public interface IGravityModifier {
    Rect modifyChildRect(@IntRange(from = 0) int minTop, @IntRange(from = 0) int maxBottom, Rect childRect);
}

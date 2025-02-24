package ir.mahdiparastesh.chlm.gravity;

import android.util.SparseArray;
import android.view.Gravity;

import ir.mahdiparastesh.chlm.SpanLayoutChildGravity;

public class RowGravityModifiersFactory implements IGravityModifiersFactory {
    private final SparseArray<IGravityModifier> gravityModifierMap;

    public RowGravityModifiersFactory() {
        gravityModifierMap = new SparseArray<>();
        CenterInRowGravityModifier centerInRowGravityModifier = new CenterInRowGravityModifier();
        TopGravityModifier topGravityModifier = new TopGravityModifier();
        BottomGravityModifier bottomGravityModifier = new BottomGravityModifier();

        gravityModifierMap.put(Gravity.TOP, topGravityModifier);
        gravityModifierMap.put(Gravity.BOTTOM, bottomGravityModifier);
        gravityModifierMap.put(Gravity.CENTER, centerInRowGravityModifier);
        gravityModifierMap.put(Gravity.CENTER_VERTICAL, centerInRowGravityModifier);
    }

    public IGravityModifier getGravityModifier(@SpanLayoutChildGravity int gravity) {
        IGravityModifier gravityModifier = gravityModifierMap.get(gravity);
        if (gravityModifier == null)
            gravityModifier = gravityModifierMap.get(Gravity.CENTER_VERTICAL);
        return gravityModifier;
    }
}

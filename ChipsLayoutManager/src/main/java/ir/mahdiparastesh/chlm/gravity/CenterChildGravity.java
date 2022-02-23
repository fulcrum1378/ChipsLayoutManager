package ir.mahdiparastesh.chlm.gravity;

import android.view.Gravity;

import ir.mahdiparastesh.chlm.SpanLayoutChildGravity;

public class CenterChildGravity implements IChildGravityResolver {
    @Override
    @SpanLayoutChildGravity
    public int getItemGravity(int position) {
        return Gravity.CENTER_VERTICAL;
    }
}

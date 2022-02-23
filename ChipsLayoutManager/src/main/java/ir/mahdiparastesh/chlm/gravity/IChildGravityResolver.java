package ir.mahdiparastesh.chlm.gravity;

import ir.mahdiparastesh.chlm.SpanLayoutChildGravity;

public interface IChildGravityResolver {
    @SpanLayoutChildGravity
    int getItemGravity(int position);
}

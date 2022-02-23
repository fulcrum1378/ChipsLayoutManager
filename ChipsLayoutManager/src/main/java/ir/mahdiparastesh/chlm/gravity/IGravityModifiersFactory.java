package ir.mahdiparastesh.chlm.gravity;

import ir.mahdiparastesh.chlm.SpanLayoutChildGravity;

public interface IGravityModifiersFactory {
    IGravityModifier getGravityModifier(@SpanLayoutChildGravity int gravity);
}

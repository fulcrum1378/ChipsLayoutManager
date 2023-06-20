package ir.mahdiparastesh.chlm.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AssertionUtils {
    public static <T> void assertNotNull(@Nullable T object, @NonNull String parameterName)
            throws AssertionError {
        if (object == null)
            throw new AssertionError(parameterName + " can't be null.");
    }
}

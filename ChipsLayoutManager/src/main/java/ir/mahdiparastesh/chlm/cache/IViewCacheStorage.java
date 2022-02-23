package ir.mahdiparastesh.chlm.cache;

import android.graphics.Rect;
import android.os.Parcelable;
import android.util.Pair;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.List;

public interface IViewCacheStorage {

    boolean isPositionEndsRow(int position);

    boolean isPositionStartsRow(int position);

    void setCachingEnabled(boolean isEnabled);

    boolean isCachingEnabled();

    int getStartOfRow(int endRow);

    void storeRow(List<Pair<Rect, View>> row);

    boolean isInCache(int position);

    void purge();

    void purgeCacheToPosition(int position);

    @Nullable
    Integer getLastCachePosition();

    boolean isCacheEmpty();

    void purgeCacheFromPosition(int position);

    Parcelable onSaveInstanceState();

    void onRestoreInstanceState(@Nullable Parcelable parcelable);
}

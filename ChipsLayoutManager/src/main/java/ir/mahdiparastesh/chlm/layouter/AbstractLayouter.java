package ir.mahdiparastesh.chlm.layouter;

import android.graphics.Rect;
import android.util.Pair;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ir.mahdiparastesh.chlm.ChipsLayoutManager;
import ir.mahdiparastesh.chlm.IBorder;
import ir.mahdiparastesh.chlm.SpanLayoutChildGravity;
import ir.mahdiparastesh.chlm.cache.IViewCacheStorage;
import ir.mahdiparastesh.chlm.gravity.IChildGravityResolver;
import ir.mahdiparastesh.chlm.gravity.IGravityModifier;
import ir.mahdiparastesh.chlm.gravity.IGravityModifiersFactory;
import ir.mahdiparastesh.chlm.gravity.IRowStrategy;
import ir.mahdiparastesh.chlm.layouter.breaker.ILayoutRowBreaker;
import ir.mahdiparastesh.chlm.layouter.criteria.IFinishingCriteria;
import ir.mahdiparastesh.chlm.layouter.placer.IPlacer;
import ir.mahdiparastesh.chlm.util.AssertionUtils;

public abstract class AbstractLayouter implements ILayouter, IBorder {
    private int currentViewWidth;
    private int currentViewHeight;
    private int currentViewPosition;
    List<Pair<Rect, View>> rowViews = new LinkedList<>();

    int viewBottom;
    int viewTop;
    int viewRight;
    int viewLeft;

    private int rowSize = 0;
    private int previousRowSize;
    private boolean isRowCompleted;

    @NonNull
    private final ChipsLayoutManager layoutManager;
    @NonNull
    private final IViewCacheStorage cacheStorage;
    @NonNull
    private final IBorder border;
    @NonNull
    private final IChildGravityResolver childGravityResolver;
    @NonNull
    private IFinishingCriteria finishingCriteria;
    @NonNull
    private IPlacer placer;
    @NonNull
    private final ILayoutRowBreaker breaker;
    @NonNull
    private final IRowStrategy rowStrategy;
    private final Set<ILayouterListener> layouterListeners;
    @NonNull
    private final IGravityModifiersFactory gravityModifiersFactory;
    @NonNull
    private final AbstractPositionIterator positionIterator;

    AbstractLayouter(Builder builder) {
        layoutManager = builder.layoutManager;
        cacheStorage = builder.cacheStorage;
        border = builder.border;
        childGravityResolver = builder.childGravityResolver;
        this.finishingCriteria = builder.finishingCriteria;
        placer = builder.placer;
        this.viewTop = builder.offsetRect.top;
        this.viewBottom = builder.offsetRect.bottom;
        this.viewRight = builder.offsetRect.right;
        this.viewLeft = builder.offsetRect.left;
        this.layouterListeners = builder.layouterListeners;
        this.breaker = builder.breaker;
        this.gravityModifiersFactory = builder.gravityModifiersFactory;
        this.rowStrategy = builder.rowStrategy;
        this.positionIterator = builder.positionIterator;
    }

    void setFinishingCriteria(@NonNull IFinishingCriteria finishingCriteria) {
        this.finishingCriteria = finishingCriteria;
    }

    @Override
    public AbstractPositionIterator positionIterator() {
        return positionIterator;
    }

    public boolean isRowCompleted() {
        return isRowCompleted;
    }

    public List<Item> getCurrentRowItems() {
        List<Item> items = new LinkedList<>();
        List<Pair<Rect, View>> mutableRowViews = new LinkedList<>(rowViews);
        if (isReverseOrder()) {
            Collections.reverse(mutableRowViews);
        }
        for (Pair<Rect, View> rowView : mutableRowViews) {
            items.add(new Item(rowView.first, layoutManager.getPosition(rowView.second)));
        }
        return items;
    }

    public final int getCurrentViewPosition() {
        return currentViewPosition;
    }

    @NonNull
    final IViewCacheStorage getCacheStorage() {
        return cacheStorage;
    }

    public void addLayouterListener(ILayouterListener layouterListener) {
        if (layouterListener != null)
            layouterListeners.add(layouterListener);
    }

    @Override
    public void removeLayouterListener(ILayouterListener layouterListener) {
        layouterListeners.remove(layouterListener);
    }

    private void notifyLayouterListeners() {
        for (ILayouterListener layouterListener : layouterListeners)
            layouterListener.onLayoutRow(this);
    }

    @Override
    public final int getPreviousRowSize() {
        return previousRowSize;
    }

    private void calculateView(View view) {
        currentViewHeight = layoutManager.getDecoratedMeasuredHeight(view);
        currentViewWidth = layoutManager.getDecoratedMeasuredWidth(view);
        currentViewPosition = layoutManager.getPosition(view);
    }

    @Override
    @CallSuper
    public final boolean placeView(View view) {
        layoutManager.measureChildWithMargins(view, 0, 0);
        calculateView(view);

        if (canNotBePlacedInCurrentRow()) {
            isRowCompleted = true;
            layoutRow();
        }

        if (isFinishedLayouting()) return false;

        rowSize++;
        Rect rect = createViewRect(view);
        rowViews.add(new Pair<>(rect, view));

        return true;
    }

    public final boolean isFinishedLayouting() {
        return finishingCriteria.isFinishedLayouting(this);
    }

    public final boolean canNotBePlacedInCurrentRow() {
        return breaker.isRowBroke(this);
    }

    abstract Rect createViewRect(View view);

    abstract boolean isReverseOrder();

    abstract void onPreLayout();

    abstract void onAfterLayout();

    abstract boolean isAttachedViewFromNewRow(View view);

    abstract void onInterceptAttachView(View view);

    void setPlacer(@NonNull IPlacer placer) {
        this.placer = placer;
    }

    @CallSuper
    @Override
    public final boolean onAttachView(View view) {
        calculateView(view);

        if (isAttachedViewFromNewRow(view)) {
            notifyLayouterListeners();
            rowSize = 0;
        }

        onInterceptAttachView(view);
        if (isFinishedLayouting()) return false;
        rowSize++;
        layoutManager.attachView(view);
        return true;
    }

    @Override
    public final void layoutRow() {
        onPreLayout();

        if (rowViews.size() > 0)
            rowStrategy.applyStrategy(this, getCurrentRowItems());

        for (Pair<Rect, View> rowViewRectPair : rowViews) {
            Rect viewRect = rowViewRectPair.first;
            View view = rowViewRectPair.second;

            viewRect = applyChildGravity(view, viewRect);
            placer.addView(view);

            layoutManager.layoutDecorated(view, viewRect.left, viewRect.top, viewRect.right, viewRect.bottom);
        }

        onAfterLayout();

        notifyLayouterListeners();


        previousRowSize = rowSize;
        //clear row data
        this.rowSize = 0;
        rowViews.clear();
        isRowCompleted = false;
    }

    private Rect applyChildGravity(View view, Rect viewRect) {
        @SpanLayoutChildGravity
        int viewGravity = childGravityResolver.getItemGravity(getLayoutManager().getPosition(view));
        IGravityModifier gravityModifier = gravityModifiersFactory.getGravityModifier(viewGravity);
        return gravityModifier.modifyChildRect(getStartRowBorder(), getEndRowBorder(), viewRect);
    }

    @NonNull
    public ChipsLayoutManager getLayoutManager() {
        return layoutManager;
    }

    @Override
    public int getRowSize() {
        return rowSize;
    }

    public int getViewTop() {
        return viewTop;
    }

    public abstract int getStartRowBorder();

    public abstract int getEndRowBorder();

    @Override
    public Rect getRowRect() {
        return new Rect(getCanvasLeftBorder(), getViewTop(), getCanvasRightBorder(), getViewBottom());
    }

    public int getViewBottom() {
        return viewBottom;
    }

    @SuppressWarnings("unused")
    final Rect getOffsetRect() {
        return new Rect(viewLeft, viewTop, viewRight, viewBottom);
    }

    public final int getViewLeft() {
        return viewLeft;
    }

    public final int getViewRight() {
        return viewRight;
    }

    public final int getCurrentViewWidth() {
        return currentViewWidth;
    }

    public final int getCurrentViewHeight() {
        return currentViewHeight;
    }

    public abstract int getRowLength();

    public abstract static class Builder {
        private ChipsLayoutManager layoutManager;
        private IViewCacheStorage cacheStorage;
        private IBorder border;
        private IChildGravityResolver childGravityResolver;
        private IFinishingCriteria finishingCriteria;
        private IPlacer placer;
        private ILayoutRowBreaker breaker;
        private Rect offsetRect;
        private final HashSet<ILayouterListener> layouterListeners = new HashSet<>();
        private IGravityModifiersFactory gravityModifiersFactory;
        private IRowStrategy rowStrategy;
        private AbstractPositionIterator positionIterator;

        Builder() {
        }

        @SuppressWarnings("WeakerAccess")
        @NonNull
        public Builder offsetRect(@NonNull Rect offsetRect) {
            this.offsetRect = offsetRect;
            return this;
        }

        @NonNull
        public final Builder layoutManager(@NonNull ChipsLayoutManager layoutManager) {
            this.layoutManager = layoutManager;
            return this;
        }

        @NonNull
        final Builder cacheStorage(@NonNull IViewCacheStorage cacheStorage) {
            this.cacheStorage = cacheStorage;
            return this;
        }

        @NonNull
        Builder rowStrategy(IRowStrategy rowStrategy) {
            this.rowStrategy = rowStrategy;
            return this;
        }

        @NonNull
        final Builder canvas(@NonNull IBorder border) {
            this.border = border;
            return this;
        }

        @NonNull
        final Builder gravityModifiersFactory(@NonNull IGravityModifiersFactory gravityModifiersFactory) {
            this.gravityModifiersFactory = gravityModifiersFactory;
            return this;
        }

        @NonNull
        final Builder childGravityResolver(@NonNull IChildGravityResolver childGravityResolver) {
            this.childGravityResolver = childGravityResolver;
            return this;
        }

        @NonNull
        final Builder finishingCriteria(@NonNull IFinishingCriteria finishingCriteria) {
            this.finishingCriteria = finishingCriteria;
            return this;
        }

        @NonNull
        public final Builder placer(@NonNull IPlacer placer) {
            this.placer = placer;
            return this;
        }

        @SuppressWarnings("unused")
        @NonNull
        final Builder addLayouterListener(@Nullable ILayouterListener layouterListener) {
            if (layouterListener != null) {
                layouterListeners.add(layouterListener);
            }
            return this;
        }

        @NonNull
        final Builder breaker(@NonNull ILayoutRowBreaker breaker) {
            AssertionUtils.assertNotNull(breaker, "breaker shouldn't be null");
            this.breaker = breaker;
            return this;
        }

        @NonNull
        final Builder addLayouterListeners(@NonNull List<ILayouterListener> layouterListeners) {
            this.layouterListeners.addAll(layouterListeners);
            return this;
        }

        @NonNull
        public Builder positionIterator(AbstractPositionIterator positionIterator) {
            this.positionIterator = positionIterator;
            return this;
        }

        protected abstract AbstractLayouter createLayouter();

        public final AbstractLayouter build() {
            if (layoutManager == null)
                throw new IllegalStateException("layoutManager can't be null, call #layoutManager()");

            if (breaker == null)
                throw new IllegalStateException("breaker can't be null, call #breaker()");

            if (border == null)
                throw new IllegalStateException("border can't be null, call #border()");

            if (cacheStorage == null)
                throw new IllegalStateException("cacheStorage can't be null, call #cacheStorage()");

            if (rowStrategy == null)
                throw new IllegalStateException("rowStrategy can't be null, call #rowStrategy()");

            if (offsetRect == null)
                throw new IllegalStateException("offsetRect can't be null, call #offsetRect()");

            if (finishingCriteria == null)
                throw new IllegalStateException("finishingCriteria can't be null, call #finishingCriteria()");

            if (placer == null)
                throw new IllegalStateException("placer can't be null, call #placer()");

            if (gravityModifiersFactory == null)
                throw new IllegalStateException("gravityModifiersFactory can't be null, call #gravityModifiersFactory()");

            if (childGravityResolver == null)
                throw new IllegalStateException("childGravityResolver can't be null, call #childGravityResolver()");

            if (positionIterator == null)
                throw new IllegalStateException("positionIterator can't be null, call #positionIterator()");

            return createLayouter();
        }
    }

    public final int getCanvasRightBorder() {
        return border.getCanvasRightBorder();
    }

    public final int getCanvasBottomBorder() {
        return border.getCanvasBottomBorder();
    }

    public final int getCanvasLeftBorder() {
        return border.getCanvasLeftBorder();
    }

    public final int getCanvasTopBorder() {
        return border.getCanvasTopBorder();
    }

}

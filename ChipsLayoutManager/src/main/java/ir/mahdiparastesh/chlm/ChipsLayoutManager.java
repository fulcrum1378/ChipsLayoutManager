package ir.mahdiparastesh.chlm;

import android.content.Context;
import android.graphics.Rect;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.View;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import ir.mahdiparastesh.chlm.anchor.AnchorViewState;
import ir.mahdiparastesh.chlm.anchor.IAnchorFactory;
import ir.mahdiparastesh.chlm.cache.IViewCacheStorage;
import ir.mahdiparastesh.chlm.cache.ViewCacheFactory;
import ir.mahdiparastesh.chlm.gravity.CenterChildGravity;
import ir.mahdiparastesh.chlm.gravity.CustomGravityResolver;
import ir.mahdiparastesh.chlm.gravity.IChildGravityResolver;
import ir.mahdiparastesh.chlm.layouter.AbstractPositionIterator;
import ir.mahdiparastesh.chlm.layouter.ColumnsStateFactory;
import ir.mahdiparastesh.chlm.layouter.ICanvas;
import ir.mahdiparastesh.chlm.layouter.ILayouter;
import ir.mahdiparastesh.chlm.layouter.IMeasureSupporter;
import ir.mahdiparastesh.chlm.layouter.IStateFactory;
import ir.mahdiparastesh.chlm.layouter.LayouterFactory;
import ir.mahdiparastesh.chlm.layouter.MeasureSupporter;
import ir.mahdiparastesh.chlm.layouter.RowsStateFactory;
import ir.mahdiparastesh.chlm.layouter.breaker.EmptyRowBreaker;
import ir.mahdiparastesh.chlm.layouter.breaker.IRowBreaker;
import ir.mahdiparastesh.chlm.layouter.criteria.AbstractCriteriaFactory;
import ir.mahdiparastesh.chlm.layouter.criteria.ICriteriaFactory;
import ir.mahdiparastesh.chlm.layouter.criteria.InfiniteCriteriaFactory;
import ir.mahdiparastesh.chlm.layouter.placer.PlacerFactory;
import ir.mahdiparastesh.chlm.util.AssertionUtils;
import ir.mahdiparastesh.chlm.util.LayoutManagerUtil;

public class ChipsLayoutManager extends RecyclerView.LayoutManager
        implements IChipsLayoutManagerContract, IStateHolder, ScrollingController.IScrollerListener {
    public static final int HORIZONTAL = 1;
    public static final int VERTICAL = 2;

    public static final int STRATEGY_DEFAULT = 1;
    public static final int STRATEGY_FILL_VIEW = 2;
    public static final int STRATEGY_FILL_SPACE = 4;
    public static final int STRATEGY_CENTER = 5;
    public static final int STRATEGY_CENTER_DENSE = 6;

    private static final int INT_ROW_SIZE_APPROXIMATELY_FOR_CACHE = 10;
    private static final int APPROXIMATE_ADDITIONAL_ROWS_COUNT = 5;
    private static final float FAST_SCROLLING_COEFFICIENT = 2;

    private ICanvas canvas;
    private IDisappearingViewsManager disappearingViewsManager;
    private final ChildViewsIterable childViews = new ChildViewsIterable(this);
    private final SparseArray<View> childViewPositions = new SparseArray<>();

    private IChildGravityResolver childGravityResolver;
    private boolean isScrollingEnabledContract = true;
    private Integer maxViewsInRow = null;
    private IRowBreaker rowBreaker = new EmptyRowBreaker();
    @Orientation
    private int layoutOrientation = HORIZONTAL;
    @RowStrategy
    private int rowStrategy = STRATEGY_DEFAULT;
    private boolean isStrategyAppliedWithLastRow;

    private boolean isSmoothScrollbarEnabled = false;

    private final IViewCacheStorage viewPositionsStorage;

    @Nullable
    private Integer cacheNormalizationPosition = null;

    private final SparseArray<View> viewCache = new SparseArray<>();

    private ParcelableContainer container = new ParcelableContainer();

    private boolean isLayoutRTL = false;

    @DeviceOrientation
    private final int orientation;

    private AnchorViewState anchorView;

    private IStateFactory stateFactory;
    private final IMeasureSupporter measureSupporter;
    private IAnchorFactory anchorFactory;
    private IScrollingController scrollingController;
    private final PlacerFactory placerFactory = new PlacerFactory(this);
    private boolean isAfterPreLayout;

    ChipsLayoutManager(Context context) {
        @DeviceOrientation
        int orientation = context.getResources().getConfiguration().orientation;
        this.orientation = orientation;

        viewPositionsStorage = new ViewCacheFactory(this).createCacheStorage();
        measureSupporter = new MeasureSupporter(this);
    }

    public static Builder newBuilder(Context context) {
        if (context == null)
            throw new IllegalArgumentException("you have passed null context to builder");
        return new ChipsLayoutManager(context).new StrategyBuilder();
    }

    public IChildGravityResolver getChildGravityResolver() {
        return childGravityResolver;
    }

    @Override
    public boolean isAutoMeasureEnabled() {
        return true;
    }

    @Override
    public void setScrollingEnabledContract(boolean isEnabled) {
        isScrollingEnabledContract = isEnabled;
    }

    @Override
    public boolean isScrollingEnabledContract() {
        return isScrollingEnabledContract;
    }

    public void setMaxViewsInRow(@IntRange(from = 1) Integer maxViewsInRow) {
        if (maxViewsInRow < 1)
            throw new IllegalArgumentException("maxViewsInRow should be positive, but is = " + maxViewsInRow);
        this.maxViewsInRow = maxViewsInRow;
        onRuntimeLayoutChanges();
    }

    private void onRuntimeLayoutChanges() {
        cacheNormalizationPosition = 0;
        viewPositionsStorage.purge();
        requestLayoutWithAnimations();
    }

    @Override
    public Integer getMaxViewsInRow() {
        return maxViewsInRow;
    }

    @Override
    public IRowBreaker getRowBreaker() {
        return rowBreaker;
    }

    @Override
    @RowStrategy
    public int getRowStrategyType() {
        return rowStrategy;
    }

    public boolean isStrategyAppliedWithLastRow() {
        return isStrategyAppliedWithLastRow;
    }

    public IViewCacheStorage getViewPositionsStorage() {
        return viewPositionsStorage;
    }

    public ICanvas getCanvas() {
        return canvas;
    }

    @NonNull
    AnchorViewState getAnchor() {
        return anchorView;
    }

    public class StrategyBuilder extends Builder {
        public Builder withLastRow(boolean withLastRow) {
            ChipsLayoutManager.this.isStrategyAppliedWithLastRow = withLastRow;
            return this;
        }
    }

    public class Builder {
        @SpanLayoutChildGravity
        private Integer gravity;

        private Builder() {
        }

        public Builder setChildGravity(@SpanLayoutChildGravity int gravity) {
            this.gravity = gravity;
            return this;
        }

        public Builder setGravityResolver(@NonNull IChildGravityResolver gravityResolver) {
            AssertionUtils.assertNotNull(gravityResolver, "gravity resolver couldn't be null");
            childGravityResolver = gravityResolver;
            return this;
        }

        public Builder setScrollingEnabled(boolean isEnabled) {
            ChipsLayoutManager.this.setScrollingEnabledContract(isEnabled);
            return this;
        }

        public StrategyBuilder setRowStrategy(@RowStrategy int rowStrategy) {
            ChipsLayoutManager.this.rowStrategy = rowStrategy;
            return (StrategyBuilder) this;
        }

        public Builder setMaxViewsInRow(@IntRange(from = 1) int maxViewsInRow) {
            if (maxViewsInRow < 1)
                throw new IllegalArgumentException("maxViewsInRow should be positive, but is = " + maxViewsInRow);
            ChipsLayoutManager.this.maxViewsInRow = maxViewsInRow;
            return this;
        }

        public Builder setRowBreaker(@NonNull IRowBreaker breaker) {
            AssertionUtils.assertNotNull(breaker, "breaker couldn't be null");
            ChipsLayoutManager.this.rowBreaker = breaker;
            return this;
        }

        public Builder setOrientation(@Orientation int orientation) {
            if (orientation != HORIZONTAL && orientation != VERTICAL) {
                return this;
            }
            ChipsLayoutManager.this.layoutOrientation = orientation;
            return this;
        }

        public ChipsLayoutManager build() {
            if (childGravityResolver == null) {
                if (gravity != null)
                    childGravityResolver = new CustomGravityResolver(gravity);
                else childGravityResolver = new CenterChildGravity();
            }

            stateFactory = layoutOrientation == HORIZONTAL
                    ? new RowsStateFactory(ChipsLayoutManager.this)
                    : new ColumnsStateFactory(ChipsLayoutManager.this);
            canvas = stateFactory.createCanvas();
            anchorFactory = stateFactory.anchorFactory();
            scrollingController = stateFactory.scrollingController();

            anchorView = anchorFactory.createNotFound();

            disappearingViewsManager = new DisappearingViewsManager(canvas, childViews, stateFactory);

            return ChipsLayoutManager.this;
        }
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    private void requestLayoutWithAnimations() {
        LayoutManagerUtil.requestLayoutWithAnimations(this);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        container = (ParcelableContainer) state;

        anchorView = container.getAnchorViewState();
        if (orientation != container.getOrientation()) {
            //orientation have been changed, clear anchor rect
            int anchorPos = anchorView.getPosition();
            anchorView = anchorFactory.createNotFound();
            anchorView.setPosition(anchorPos);
        }

        viewPositionsStorage.onRestoreInstanceState(container.getPositionsCache(orientation));
        cacheNormalizationPosition = container.getNormalizationPosition(orientation);

        if (cacheNormalizationPosition != null) {
            viewPositionsStorage.purgeCacheFromPosition(cacheNormalizationPosition);
        }
        viewPositionsStorage.purgeCacheFromPosition(anchorView.getPosition());
    }

    @Override
    public Parcelable onSaveInstanceState() {
        container.putAnchorViewState(anchorView);
        container.putPositionsCache(orientation, viewPositionsStorage.onSaveInstanceState());
        container.putOrientation(orientation);

        Integer storedNormalizationPosition = cacheNormalizationPosition != null
                ? cacheNormalizationPosition : viewPositionsStorage.getLastCachePosition();
        container.putNormalizationPosition(orientation, storedNormalizationPosition);
        return container;
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return true;
    }

    public int getCompletelyVisibleViewsCount() {
        int visibleViewsCount = 0;
        for (View child : childViews)
            if (canvas.isFullyVisible(child))
                visibleViewsCount++;
        return visibleViewsCount;
    }

    @Override
    public int findFirstVisibleItemPosition() {
        if (getChildCount() == 0)
            return RecyclerView.NO_POSITION;
        return canvas.getMinPositionOnScreen();
    }

    @Override
    public int findFirstCompletelyVisibleItemPosition() {
        for (View view : childViews) {
            Rect rect = canvas.getViewRect(view);
            if (!canvas.isFullyVisible(rect)) continue;
            if (canvas.isInside(rect)) return getPosition(view);
        }
        return RecyclerView.NO_POSITION;
    }

    @Override
    public int findLastVisibleItemPosition() {
        if (getChildCount() == 0)
            return RecyclerView.NO_POSITION;
        return canvas.getMaxPositionOnScreen();
    }

    @Override
    public int findLastCompletelyVisibleItemPosition() {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            View view = getChildAt(i);
            Rect rect = canvas.getViewRect(view);
            if (!canvas.isFullyVisible(rect)) continue;
            if (canvas.isInside(view)) return getPosition(view);
        }
        return RecyclerView.NO_POSITION;
    }

    @Nullable
    View getChildWithPosition(int position) {
        return childViewPositions.get(position);
    }

    public boolean isLayoutRTL() {
        return getLayoutDirection() == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    @Override
    @Orientation
    public int layoutOrientation() {
        return layoutOrientation;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + disappearingViewsManager.getDeletingItemsOnScreenCount();
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0) {
            detachAndScrapAttachedViews(recycler);
            return;
        }

        if (isLayoutRTL() != isLayoutRTL) {
            isLayoutRTL = isLayoutRTL();
            detachAndScrapAttachedViews(recycler);
        }

        calcRecyclerCacheSize(recycler);

        if (state.isPreLayout()) {

            int additionalLength = disappearingViewsManager.calcDisappearingViewsLength(recycler);

            anchorView = anchorFactory.getAnchor();
            anchorFactory.resetRowCoordinates(anchorView);
            detachAndScrapAttachedViews(recycler);

            //in case removing draw additional rows to show predictive animations for appearing views
            AbstractCriteriaFactory criteriaFactory = stateFactory.createDefaultFinishingCriteriaFactory();
            criteriaFactory.setAdditionalRowsCount(APPROXIMATE_ADDITIONAL_ROWS_COUNT);
            criteriaFactory.setAdditionalLength(additionalLength);

            LayouterFactory layouterFactory = stateFactory.createLayouterFactory(criteriaFactory, placerFactory.createRealPlacerFactory());
            fill(recycler,
                    layouterFactory.getBackwardLayouter(anchorView),
                    layouterFactory.getForwardLayouter(anchorView));

            isAfterPreLayout = true;
        } else {
            detachAndScrapAttachedViews(recycler);

            viewPositionsStorage.purgeCacheFromPosition(anchorView.getPosition());
            if (cacheNormalizationPosition != null && anchorView.getPosition() <= cacheNormalizationPosition) {
                cacheNormalizationPosition = null;
            }
            AbstractCriteriaFactory criteriaFactory = stateFactory.createDefaultFinishingCriteriaFactory();
            criteriaFactory.setAdditionalRowsCount(APPROXIMATE_ADDITIONAL_ROWS_COUNT);

            LayouterFactory layouterFactory = stateFactory.createLayouterFactory(criteriaFactory, placerFactory.createRealPlacerFactory());
            ILayouter backwardLayouter = layouterFactory.getBackwardLayouter(anchorView);
            ILayouter forwardLayouter = layouterFactory.getForwardLayouter(anchorView);

            fill(recycler, backwardLayouter, forwardLayouter);

            if (scrollingController.normalizeGaps(recycler, null)) {
                anchorView = anchorFactory.getAnchor();
                requestLayoutWithAnimations();
            }

            if (isAfterPreLayout)
                layoutDisappearingViews(recycler, backwardLayouter, forwardLayouter);
            isAfterPreLayout = false;
        }

        disappearingViewsManager.reset();
        if (!state.isMeasuring()) measureSupporter.onSizeChanged();
    }

    @Override
    public void detachAndScrapAttachedViews(RecyclerView.Recycler recycler) {
        super.detachAndScrapAttachedViews(recycler);
        childViewPositions.clear();
    }

    private void layoutDisappearingViews(RecyclerView.Recycler recycler,
                                         @NonNull ILayouter upLayouter, ILayouter downLayouter) {
        ICriteriaFactory criteriaFactory = new InfiniteCriteriaFactory();
        LayouterFactory layouterFactory = stateFactory.createLayouterFactory(
                criteriaFactory, placerFactory.createDisappearingPlacerFactory());

        DisappearingViewsManager.DisappearingViewsContainer disappearingViews =
                disappearingViewsManager.getDisappearingViews(recycler);

        if (disappearingViews.size() > 0) {
            downLayouter = layouterFactory.buildForwardLayouter(downLayouter);

            for (int i = 0; i < disappearingViews.getForwardViews().size(); i++) {
                int position = disappearingViews.getForwardViews().keyAt(i);
                downLayouter.placeView(recycler.getViewForPosition(position));
            }
            downLayouter.layoutRow();

            upLayouter = layouterFactory.buildBackwardLayouter(upLayouter);
            for (int i = 0; i < disappearingViews.getBackwardViews().size(); i++) {
                int position = disappearingViews.getBackwardViews().keyAt(i);
                upLayouter.placeView(recycler.getViewForPosition(position));
            }

            upLayouter.layoutRow();
        }
    }

    private void fillCache() {
        for (int i = 0, cnt = getChildCount(); i < cnt; i++) {
            View view = getChildAt(i);
            int pos = getPosition(view);
            viewCache.put(pos, view);
        }
    }

    private void fill(RecyclerView.Recycler recycler, ILayouter backwardLayouter,
                      ILayouter forwardLayouter) {
        int startingPos = anchorView.getPosition();
        fillCache();

        for (int i = 0; i < viewCache.size(); i++)
            detachView(viewCache.valueAt(i));

        if (anchorView.getAnchorViewRect() != null)
            fillWithLayouter(recycler, backwardLayouter, startingPos - 1);
        fillWithLayouter(recycler, forwardLayouter, startingPos);
        for (int i = 0; i < viewCache.size(); i++)
            removeAndRecycleView(viewCache.valueAt(i), recycler);

        canvas.findBorderViews();
        buildChildWithPositionsMap();

        viewCache.clear();
    }

    private void buildChildWithPositionsMap() {
        childViewPositions.clear();
        for (View view : childViews) {
            int position = getPosition(view);
            childViewPositions.put(position, view);
        }
    }

    private void fillWithLayouter(RecyclerView.Recycler recycler, ILayouter layouter, int startingPos) {
        if (startingPos < 0) return;
        AbstractPositionIterator iterator = layouter.positionIterator();
        iterator.move(startingPos);
        while (iterator.hasNext()) {
            int pos = iterator.next();
            View view = viewCache.get(pos);
            if (view == null) {
                try {
                    view = recycler.getViewForPosition(pos);
                } catch (IndexOutOfBoundsException e) {
                    break;
                }

                if (!layouter.placeView(view)) {
                    recycler.recycleView(view);
                    break;
                }
            } else {
                if (!layouter.onAttachView(view)) break;
                viewCache.remove(pos);
            }
        }
        layouter.layoutRow();
    }

    private void calcRecyclerCacheSize(RecyclerView.Recycler recycler) {
        int viewsInRow = maxViewsInRow == null ? INT_ROW_SIZE_APPROXIMATELY_FOR_CACHE : maxViewsInRow;
        recycler.setViewCacheSize((int) (viewsInRow * FAST_SCROLLING_COEFFICIENT));
    }

    private void performNormalizationIfNeeded() {
        if (cacheNormalizationPosition != null && getChildCount() > 0) {
            final View firstView = getChildAt(0);
            int firstViewPosition = getPosition(firstView);

            if (firstViewPosition < cacheNormalizationPosition ||
                    (cacheNormalizationPosition == 0 && cacheNormalizationPosition == firstViewPosition)) {
                viewPositionsStorage.purgeCacheFromPosition(firstViewPosition);
                cacheNormalizationPosition = null;
                requestLayoutWithAnimations();
            }
        }
    }

    @Override
    public void setMeasuredDimension(int widthSize, int heightSize) {
        measureSupporter.measure(widthSize, heightSize);
        super.setMeasuredDimension(measureSupporter.getMeasuredWidth(), measureSupporter.getMeasuredHeight());
    }

    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter,
                                 RecyclerView.Adapter newAdapter) {
        if (oldAdapter != null && measureSupporter.isRegistered()) {
            try {
                measureSupporter.setRegistered(false);
                oldAdapter.unregisterAdapterDataObserver((RecyclerView.AdapterDataObserver) measureSupporter);
            } catch (IllegalStateException ignored) {
            }
        }
        if (newAdapter != null) {
            measureSupporter.setRegistered(true);
            newAdapter.registerAdapterDataObserver((RecyclerView.AdapterDataObserver) measureSupporter);
        }
        removeAllViews();
    }

    @Override
    public void onItemsRemoved(@NonNull final RecyclerView recyclerView, int positionStart, int itemCount) {
        super.onItemsRemoved(recyclerView, positionStart, itemCount);
        onLayoutUpdatedFromPosition(positionStart);

        measureSupporter.onItemsRemoved(recyclerView);
    }

    @Override
    public void onItemsAdded(@NonNull RecyclerView recyclerView, int positionStart, int itemCount) {
        super.onItemsAdded(recyclerView, positionStart, itemCount);
        onLayoutUpdatedFromPosition(positionStart);
    }

    @Override
    public void onItemsChanged(@NonNull RecyclerView recyclerView) {
        super.onItemsChanged(recyclerView);
        viewPositionsStorage.purge();
        onLayoutUpdatedFromPosition(0);
    }

    @Override
    public void onItemsUpdated(@NonNull RecyclerView recyclerView, int positionStart, int itemCount) {
        super.onItemsUpdated(recyclerView, positionStart, itemCount);
        onLayoutUpdatedFromPosition(positionStart);
    }

    @Override
    public void onItemsUpdated(@NonNull RecyclerView recyclerView, int positionStart, int itemCount, Object payload) {
        onItemsUpdated(recyclerView, positionStart, itemCount);
    }

    @Override
    public void onItemsMoved(@NonNull RecyclerView recyclerView, int from, int to, int itemCount) {
        super.onItemsMoved(recyclerView, from, to, itemCount);
        onLayoutUpdatedFromPosition(Math.min(from, to));
    }

    private void onLayoutUpdatedFromPosition(int position) {
        viewPositionsStorage.purgeCacheFromPosition(position);
        int startRowPos = viewPositionsStorage.getStartOfRow(position);
        cacheNormalizationPosition = cacheNormalizationPosition == null ?
                startRowPos : Math.min(cacheNormalizationPosition, startRowPos);
    }

    @Override
    public void setSmoothScrollbarEnabled(boolean enabled) {
        isSmoothScrollbarEnabled = enabled;
    }

    @Override
    public boolean isSmoothScrollbarEnabled() {
        return isSmoothScrollbarEnabled;
    }

    public void scrollToPosition(int position) {
        if (position >= getItemCount() || position < 0) return;

        Integer lastCachePosition = viewPositionsStorage.getLastCachePosition();
        cacheNormalizationPosition = cacheNormalizationPosition != null ? cacheNormalizationPosition : lastCachePosition;
        if (lastCachePosition != null && position < lastCachePosition) {
            position = viewPositionsStorage.getStartOfRow(position);
        }

        anchorView = anchorFactory.createNotFound();
        anchorView.setPosition(position);

        //Trigger a new view layout
        super.requestLayout();
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, final int position) {
        if (position >= getItemCount() || position < 0) return;
        RecyclerView.SmoothScroller scroller = scrollingController.createSmoothScroller(recyclerView.getContext(), position, 150, anchorView);
        scroller.setTargetPosition(position);
        startSmoothScroll(scroller);
    }

    @Override
    public boolean canScrollHorizontally() {
        return scrollingController.canScrollHorizontally();
    }

    @Override
    public boolean canScrollVertically() {
        return scrollingController.canScrollVertically();
    }


    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return scrollingController.scrollVerticallyBy(dy, recycler, state);
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return scrollingController.scrollHorizontallyBy(dx, recycler, state);
    }

    public VerticalScrollingController verticalScrollingController() {
        return new VerticalScrollingController(this, stateFactory, this);
    }

    public HorizontalScrollingController horizontalScrollingController() {
        return new HorizontalScrollingController(this, stateFactory, this);
    }

    @Override
    public void onScrolled(IScrollingController scrollingController, RecyclerView.Recycler recycler, RecyclerView.State state) {
        performNormalizationIfNeeded();
        anchorView = anchorFactory.getAnchor();

        AbstractCriteriaFactory criteriaFactory = stateFactory.createDefaultFinishingCriteriaFactory();
        criteriaFactory.setAdditionalRowsCount(1);
        LayouterFactory factory = stateFactory.createLayouterFactory(criteriaFactory, placerFactory.createRealPlacerFactory());

        fill(recycler,
                factory.getBackwardLayouter(anchorView),
                factory.getForwardLayouter(anchorView));
    }

    @Override
    public int computeVerticalScrollOffset(@NonNull RecyclerView.State state) {
        return scrollingController.computeVerticalScrollOffset(state);
    }

    @Override
    public int computeVerticalScrollExtent(@NonNull RecyclerView.State state) {
        return scrollingController.computeVerticalScrollExtent(state);
    }

    @Override
    public int computeVerticalScrollRange(@NonNull RecyclerView.State state) {
        return scrollingController.computeVerticalScrollRange(state);
    }

    @Override
    public int computeHorizontalScrollExtent(@NonNull RecyclerView.State state) {
        return scrollingController.computeHorizontalScrollExtent(state);
    }

    @Override
    public int computeHorizontalScrollOffset(@NonNull RecyclerView.State state) {
        return scrollingController.computeHorizontalScrollOffset(state);
    }

    @Override
    public int computeHorizontalScrollRange(@NonNull RecyclerView.State state) {
        return scrollingController.computeHorizontalScrollRange(state);
    }
}

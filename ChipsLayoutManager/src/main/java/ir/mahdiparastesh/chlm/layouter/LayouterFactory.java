package ir.mahdiparastesh.chlm.layouter;

import android.graphics.Rect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ir.mahdiparastesh.chlm.ChipsLayoutManager;
import ir.mahdiparastesh.chlm.anchor.AnchorViewState;
import ir.mahdiparastesh.chlm.cache.IViewCacheStorage;
import ir.mahdiparastesh.chlm.gravity.IGravityModifiersFactory;
import ir.mahdiparastesh.chlm.gravity.IRowStrategy;
import ir.mahdiparastesh.chlm.gravity.SkipLastRowStrategy;
import ir.mahdiparastesh.chlm.layouter.breaker.IBreakerFactory;
import ir.mahdiparastesh.chlm.layouter.criteria.ICriteriaFactory;
import ir.mahdiparastesh.chlm.layouter.placer.IPlacerFactory;

public class LayouterFactory {
    private final ChipsLayoutManager layoutManager;
    private final IViewCacheStorage cacheStorage;
    private final List<ILayouterListener> layouterListeners = new ArrayList<>();

    private final IBreakerFactory breakerFactory;
    private final ICriteriaFactory criteriaFactory;
    private final IPlacerFactory placerFactory;
    private final IGravityModifiersFactory gravityModifiersFactory;
    private final IRowStrategy rowStrategy;
    private final ILayouterCreator layouterCreator;

    LayouterFactory(ChipsLayoutManager layoutManager,
                    ILayouterCreator layouterCreator,
                    IBreakerFactory breakerFactory,
                    ICriteriaFactory criteriaFactory,
                    IPlacerFactory placerFactory,
                    IGravityModifiersFactory gravityModifiersFactory,
                    IRowStrategy rowStrategy) {
        this.layouterCreator = layouterCreator;
        this.cacheStorage = layoutManager.getViewPositionsStorage();
        this.layoutManager = layoutManager;
        this.breakerFactory = breakerFactory;
        this.criteriaFactory = criteriaFactory;
        this.placerFactory = placerFactory;
        this.gravityModifiersFactory = gravityModifiersFactory;
        this.rowStrategy = rowStrategy;
    }

    public void addLayouterListener(@Nullable ILayouterListener layouterListener) {
        if (layouterListener != null) {
            layouterListeners.add(layouterListener);
        }
    }

    private AbstractLayouter.Builder createBackwardBuilder() {
        return layouterCreator.createBackwardBuilder();
    }

    private AbstractLayouter.Builder createForwardBuilder() {
        return layouterCreator.createForwardBuilder();
    }

    private Rect createOffsetRectForBackwardLayouter(@NonNull AnchorViewState anchorRect) {
        return layouterCreator.createOffsetRectForBackwardLayouter(anchorRect);
    }

    private Rect createOffsetRectForForwardLayouter(AnchorViewState anchorRect) {
        return layouterCreator.createOffsetRectForForwardLayouter(anchorRect);
    }

    private ICanvas createCanvas() {
        return layoutManager.getCanvas();
    }

    @NonNull
    private AbstractLayouter.Builder fillBasicBuilder(AbstractLayouter.Builder builder) {
        return builder.layoutManager(layoutManager)
                .canvas(createCanvas())
                .childGravityResolver(layoutManager.getChildGravityResolver())
                .cacheStorage(cacheStorage)
                .gravityModifiersFactory(gravityModifiersFactory)
                .addLayouterListeners(layouterListeners);
    }

    @Nullable
    public final ILayouter getBackwardLayouter(@NonNull AnchorViewState anchorRect) {
        return fillBasicBuilder(createBackwardBuilder())
                .offsetRect(createOffsetRectForBackwardLayouter(anchorRect))
                .breaker(breakerFactory.createBackwardRowBreaker())
                .finishingCriteria(criteriaFactory.getBackwardFinishingCriteria())
                .rowStrategy(rowStrategy)
                .placer(placerFactory.getAtStartPlacer())
                .positionIterator(new DecrementalPositionIterator(layoutManager.getItemCount()))
                .build();
    }

    @NonNull
    public final ILayouter getForwardLayouter(@NonNull AnchorViewState anchorRect) {
        return fillBasicBuilder(createForwardBuilder())
                .offsetRect(createOffsetRectForForwardLayouter(anchorRect))
                .breaker(breakerFactory.createForwardRowBreaker())
                .finishingCriteria(criteriaFactory.getForwardFinishingCriteria())
                .rowStrategy(new SkipLastRowStrategy(rowStrategy, !layoutManager.isStrategyAppliedWithLastRow()))
                .placer(placerFactory.getAtEndPlacer())
                .positionIterator(new IncrementalPositionIterator(layoutManager.getItemCount()))
                .build();
    }

    @NonNull
    public final ILayouter buildForwardLayouter(@NonNull ILayouter layouter) {
        AbstractLayouter abstractLayouter = (AbstractLayouter) layouter;
        abstractLayouter.setFinishingCriteria(criteriaFactory.getForwardFinishingCriteria());
        abstractLayouter.setPlacer(placerFactory.getAtEndPlacer());
        return abstractLayouter;
    }

    @NonNull
    public final ILayouter buildBackwardLayouter(@NonNull ILayouter layouter) {
        AbstractLayouter abstractLayouter = (AbstractLayouter) layouter;
        abstractLayouter.setFinishingCriteria(criteriaFactory.getBackwardFinishingCriteria());
        abstractLayouter.setPlacer(placerFactory.getAtStartPlacer());
        return abstractLayouter;
    }
}

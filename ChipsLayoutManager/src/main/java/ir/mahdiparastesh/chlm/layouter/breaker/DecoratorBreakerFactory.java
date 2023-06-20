package ir.mahdiparastesh.chlm.layouter.breaker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ir.mahdiparastesh.chlm.cache.IViewCacheStorage;

public class DecoratorBreakerFactory implements IBreakerFactory {

    private final IBreakerFactory breakerFactory;
    private final IViewCacheStorage cacheStorage;
    private final IRowBreaker rowBreaker;


    @Nullable
    private final Integer maxViewsInRow;

    public DecoratorBreakerFactory(@NonNull IViewCacheStorage cacheStorage,
                                   @NonNull IRowBreaker rowBreaker,
                                   @Nullable Integer maxViewsInRow,
                                   @NonNull IBreakerFactory breakerFactory) {
        this.cacheStorage = cacheStorage;
        this.rowBreaker = rowBreaker;
        this.maxViewsInRow = maxViewsInRow;
        this.breakerFactory = breakerFactory;
    }

    @Override
    public ILayoutRowBreaker createBackwardRowBreaker() {
        ILayoutRowBreaker breaker = breakerFactory.createBackwardRowBreaker();
        breaker = new BackwardBreakerContract(rowBreaker, new CacheRowBreaker(cacheStorage, breaker));
        if (maxViewsInRow != null)
            breaker = new MaxViewsBreaker(maxViewsInRow, breaker);
        return breaker;
    }

    @Override
    public ILayoutRowBreaker createForwardRowBreaker() {
        ILayoutRowBreaker breaker = breakerFactory.createForwardRowBreaker();
        breaker = new ForwardBreakerContract(rowBreaker, breaker);
        if (maxViewsInRow != null)
            breaker = new MaxViewsBreaker(maxViewsInRow, breaker);
        return breaker;
    }
}

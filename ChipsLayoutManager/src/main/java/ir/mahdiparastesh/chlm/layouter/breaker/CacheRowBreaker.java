package ir.mahdiparastesh.chlm.layouter.breaker;

import ir.mahdiparastesh.chlm.cache.IViewCacheStorage;
import ir.mahdiparastesh.chlm.layouter.AbstractLayouter;

class CacheRowBreaker extends RowBreakerDecorator {

    private final IViewCacheStorage cacheStorage;

    CacheRowBreaker(IViewCacheStorage cacheStorage, ILayoutRowBreaker decorate) {
        super(decorate);
        this.cacheStorage = cacheStorage;
    }

    @Override
    public boolean isRowBroke(AbstractLayouter al) {
        boolean stopDueToCache = cacheStorage.isPositionEndsRow(al.getCurrentViewPosition());
        return super.isRowBroke(al) || stopDueToCache;
    }
}

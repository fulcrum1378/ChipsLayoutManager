package ir.mahdiparastesh.chlm.layouter.breaker;

public class RTLRowBreakerFactory implements IBreakerFactory {
    @Override
    public ILayoutRowBreaker createBackwardRowBreaker() {
        return new RTLBackwardRowBreaker();
    }

    @Override
    public ILayoutRowBreaker createForwardRowBreaker() {
        return new RTLForwardRowBreaker();
    }
}

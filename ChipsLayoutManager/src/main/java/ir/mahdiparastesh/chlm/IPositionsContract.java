package ir.mahdiparastesh.chlm;

interface IPositionsContract {
    int findFirstVisibleItemPosition();

    int findFirstCompletelyVisibleItemPosition();

    int findLastVisibleItemPosition();

    int findLastCompletelyVisibleItemPosition();
}

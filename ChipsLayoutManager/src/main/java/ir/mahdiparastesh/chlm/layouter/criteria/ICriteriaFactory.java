package ir.mahdiparastesh.chlm.layouter.criteria;

public interface ICriteriaFactory {

    IFinishingCriteria getBackwardFinishingCriteria();

    IFinishingCriteria getForwardFinishingCriteria();
}

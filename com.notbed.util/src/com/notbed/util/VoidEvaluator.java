package com.notbed.util;

/**
 * @author Alexandru Bledea
 * @since Aug 22, 2013
 */
public abstract class VoidEvaluator<O> implements IEvaluator<O, Void> {

	/* (non-Javadoc)
	 * @see com.notbed.util.IEvaluator#evaluate(java.lang.Object)
	 */
	@Override
	public final Void evaluate(O obj) {
		evaluateNoResult(obj);
		return null;
	}

	/**
	 * @param obj
	 */
	protected abstract void evaluateNoResult(O obj);

}

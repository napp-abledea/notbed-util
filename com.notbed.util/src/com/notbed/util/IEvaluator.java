package com.notbed.util;

/**
 * @author Alexandru Bledea
 * @since Jul 9, 2013
 */
public interface IEvaluator<O, R> extends IEvaluatorWithException<O, R> {

	/* (non-Javadoc)
	 * @see com.notbed.util.IEvaluatorWithException#evaluate(java.lang.Object)
	 */
	@Override
	R evaluate(O obj);

}

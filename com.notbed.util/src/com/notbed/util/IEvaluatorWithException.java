package com.notbed.util;

/**
 * @author Alexandru Bledea
 * @since Jul 9, 2013
 */
public interface IEvaluatorWithException<O, R> {

	/**
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	R evaluate(O obj) throws Exception;

}

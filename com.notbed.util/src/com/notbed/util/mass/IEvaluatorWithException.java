package com.notbed.util.mass;

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

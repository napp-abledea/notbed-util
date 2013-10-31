package com.notbed.util.mass;

/**
 * @author Alexandru Bledea
 * @since Sep 26, 2013
 * @param <O>
 */
public interface IStringEvaluator<O> extends IEvaluator<O, String> {

	/* (non-Javadoc)
	 * @see de.xwic.appkit.core.util.ILazyEval#evaluate(java.lang.Object)
	 */
	@Override
	String evaluate(O obj);

}
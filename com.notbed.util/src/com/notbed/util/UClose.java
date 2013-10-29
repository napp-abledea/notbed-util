/**
 *
 */
package com.notbed.util;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.commons.logging.LogFactory;

import com.notbed.util.mass.VoidEvaluator;

/**
 * @author Alexandru Bledea
 * @since Sep 22, 2013
 */
public class UClose {

	public static VoidEvaluator<Closeable> CLOSE_CLOSEABLE = new CloseEvaluator<Closeable>() {

		/* (non-Javadoc)
		 * @see com.notbed.util.UClose.CloseEvaluator#close(java.lang.Object)
		 */
		@Override
		protected void close(Closeable object) throws Exception {
			object.close();
		}
	};

	public static VoidEvaluator<ResultSet> CLOSE_RESULT_SET = new CloseEvaluator<ResultSet>() {

		/* (non-Javadoc)
		 * @see com.notbed.util.UClose.CloseEvaluator#close(java.lang.Object)
		 */
		@Override
		protected void close(ResultSet object) throws Exception {
			if (!object.isClosed()) {
				object.close();
			}
		}
	};

	public static VoidEvaluator<Statement> CLOSE_STATEMENT = new CloseEvaluator<Statement>() {

		/* (non-Javadoc)
		 * @see com.notbed.util.UClose.CloseEvaluator#close(java.lang.Object)
		 */
		@Override
		protected void close(Statement object) throws Exception {
			if (!object.isClosed()) {
				object.close();
			}
		}
	};

	public static VoidEvaluator<Connection> CLOSE_CONNECTION = new CloseEvaluator<Connection>() {

		/* (non-Javadoc)
		 * @see com.notbed.util.UClose.CloseEvaluator#close(java.lang.Object)
		 */
		@Override
		protected void close(Connection object) throws Exception {
			if (!object.isClosed()) {
				object.close();
			}
		}
	};

	/**
	 * @author Alexandru Bledea
	 * @since Sep 22, 2013
	 * @param <O>
	 */
	private static abstract class CloseEvaluator<O> extends VoidEvaluator<O> {

		/* (non-Javadoc)
		 * @see com.notbed.util.VoidEvaluator#evaluateNoResult(java.lang.Object)
		 */
		@Override
		public void evaluateNoResult(O obj) {
			if (obj != null) {
				try {
					close(obj);
				} catch (Throwable t) {
					LogFactory.getLog(getClass()).error("Failed to close", t);
				}
			}
		};

		/**
		 * @param object
		 */
		protected abstract void close(O object) throws Exception;
	}

}

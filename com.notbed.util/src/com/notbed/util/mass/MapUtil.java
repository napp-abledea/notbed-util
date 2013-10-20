package com.notbed.util.mass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import com.notbed.util.mass.InternalEvaluator.EvaluationResult;
import com.notbed.util.mass.InternalEvaluator.IDupeChecker;

/**
 * @author Alexandru Bledea
 * @since Jul 9, 2013
 */
public class MapUtil {

	/**
	 * Used to generate a map from the collection if <b>items</b> by using a <b>evaluator</b>. <br>
	 * If there are two objects with the same key, the latest entry will override the previous entry<br>
	 * If there are <b>null</b> values in the collection and objects that evaluate to <b>null</b> they are skipped<br>
	 * @param items the collection from which we create the map
	 * @param generator the key generator
	 * @return a map created from the items using the generator, if the key evaluates to null, it is not added to the map, if two items evaluate to the same key, the latest one will override any previous values
	 */
	public static <Key, Obj> Map<Key, Obj> generateMap(Collection<? extends Obj> items, IEvaluator<Obj, Key> generator) {
		try {
			return generateMap(items, generator, true);
		} catch (DuplicateKeyException e) {
			return null; // not going to happen
		}
	}

	/**
	 * Used to generate a map from the collection if <b>items</b> by using a <b>evaluator</b>. <br>
	 * Use <b>allowDupes</b> if you want values that evaluate to the same key to be added to the map. This will override the previous entry.<br>
	 * If there are <b>null</b> values in the collection and objects that evaluate to <b>null</b> they are skipped<br>
	 * @param items the collection from which we create the map
	 * @param generator the key generator
	 * @param allowDupes if we allow duplicate keys in the map, the latest one will always override the previous value, if we don't support it, throw {@link de.xwic.appkit.core.util.DuplicateKeyException}
	 * @return a map created from the items using the generator, if the key evaluates to null, it is not added to the map
	 * @throws DuplicateKeyException if we have a duplicate key and we don't allow that
	 */
	public static <Key, Obj> Map<Key, Obj> generateMap(Collection<? extends Obj> items, IEvaluator<Obj, Key> generator, boolean allowDupes)
			throws DuplicateKeyException {
		return generateMap(items, generator, allowDupes, true);
	}

	/**
	 * Used to generate a map from the collection if <b>items</b> by using a <b>evaluator</b>. <br>
	 * Use <b>allowDupes</b> if you want values that evaluate to the same key to be added to the map. This will override the previous entry.<br>
	 * Use <b>skipNullObjects</b> to avoid {@link java.lang.NullPointerException} if a null value is in the collection<br>
	 * Objects that evaluate to <b>null</b> won't be added to the map
	 * @param items the collection from which we create the map
	 * @param generator the key generator
	 * @param allowDupes if we allow duplicate keys in the map, the latest one will always override the previous value, if we don't support it, throw {@link de.xwic.appkit.core.util.DuplicateKeyException}
	 * @param skipNullObjects if we want to skip null objects, if we don't skip these objects, we will get a {@link java.lang.NullPointerException}
	 * @return a map created from the items using the generator, if the key evaluates to null, it is not added to the map
	 * @throws DuplicateKeyException if we have a duplicate key and we don't allow that
	 * @throws NullPointerException if there is a null value in the collection and we don't skip it
	 */
	public static <Key, Obj> Map<Key, Obj> generateMap(Collection<? extends Obj> items, IEvaluator<Obj, Key> generator, boolean allowDupes,
			boolean skipNullObjects) throws DuplicateKeyException {
		return generateMap(items, generator, allowDupes, skipNullObjects, true);
	}

	/**
	 * Used to generate a map from the collection if <b>items</b> by using a <b>evaluator</b>. <br>
	 * Use <b>allowDupes</b> if you want values that evaluate to the same key to be added to the map. This will override the previous entry.<br>
	 * Use <b>skipNullObjects</b> to avoid {@link java.lang.NullPointerException} if a null value is in the collection<br>
	 * Use <b>skipNullValues</b> to skip adding object to the map it evaluates to null
	 * @param items the collection from which we create the map
	 * @param generator the key generator
	 * @param allowDupes if we allow duplicate keys in the map, the latest one will always override the previous value, if we don't support it, throw {@link de.xwic.appkit.core.util.DuplicateKeyException}
	 * @param skipNullObjects if we want to skip null objects, if we don't skip these objects, we will get a {@link java.lang.NullPointerException}
	 * @param skipNullValues if we want to skip the null values from being added to the map
	 * @return a map created from the items using the generator
	 * @throws DuplicateKeyException if we have a duplicate key and we don't allow that
	 * @throws NullPointerException if there is a null value in the collection and we don't skip it
	 */
	public static <Key, Obj> Map<Key, Obj> generateMap(Collection<? extends Obj> items, IEvaluator<Obj, Key> generator, boolean allowDupes,
			boolean skipNullObjects, boolean skipNullValues) throws DuplicateKeyException {

		if (items == null) {
			items = new ArrayList<Obj>();
		}

		EvaluationResult<Key> result = new EvaluationResult<Key>();
		InternalHashMap<Key, Obj> map = new InternalHashMap<Key, Obj>();

		for (Obj t : items) {
			result = InternalEvaluator.evaluate(t, skipNullObjects, generator, skipNullValues, allowDupes, map, result);
			if (!result.skip()) {
				map.put(result.getResult(), t);
			}
		}
		return map;
	}

	/**
	 * @param initializer the map initializer
	 * @return a unmodifiable {@link java.util.HashMap} containting the values that were set using the <b>initializer</b>
	 */
	public static <Key, Obj> Map<Key, Obj> unmodifiableMap(IMapInitializer<Key, Obj> initializer) {
		Map<Key, Obj> map = new HashMap<Key, Obj>();
		initializer.initMap(map);
		return Collections.unmodifiableMap(map);
	}

	/**
	 * equivallent to<br>
	 * if (<b>map</b>.contains(<b>key</b>){<br>
	 * &nbsp;&nbsp;return <b>map</b>.get(<b>key</b>);<br>
	 * }<br>
	 * ? x = <b>initializer</b>.evaluate(<b>initValue</b>); // init something that should be mapped to the <b>key</b><br>
	 * <b>map</b>.put(<b>key</b>, x);<br>
	 * return x;<br>
	 *
	 * @deprecated consider using AIMap instead
	 * @param map
	 * @param key
	 * @param initValue
	 * @param initializer
	 * @return
	 */
	@Deprecated
	public static <K, X, I, V extends X> V get(Map<K, V> map, K key, I initValue, IEvaluator<I, X> initializer) {
		if (map.containsKey(key)) {
			return map.get(key);
		}
//		risky but convinient, this will allow us to reuse initializers
//		java 6 doesn't reify generics
//		if it did reify generics we would have had O extends X instead of X extends O
//		which would have been a much safer choice, oh well...
		V call = (V) initializer.evaluate(initValue);
		map.put(key, call);
		return call;
	}

	/**
	 * equivallent to<br>
	 * if (<b>map</b>.contains(<b>key</b>){<br>
	 * &nbsp;&nbsp;return <b>map</b>.get(<b>key</b>);<br>
	 * }<br>
	 * ? x = <b>initializer</b>.call(); // init something that should be mapped to the <b>key</b><br>
	 * <b>map</b>.put(<b>key</b>, x);<br>
	 * return x;<br>
	 *
	 * @deprecated consider using AIMap instead
	 * @param map
	 * @param key
	 * @param initializer
	 * @return
	 *
	 */
	@Deprecated
	public static <K, O, X extends O> X get(Map<K, X> map, K key, final LazyInit<O> initializer) {
		return get(map, key, null, initializer);
	}

	/**
	 * @author Alexandru Bledea
	 * @since Jul 31, 2013
	 * @param <K>
	 * @param <V>
	 */
	private static class InternalHashMap<K, V> extends HashMap<K, V> implements IDupeChecker<K> {

		/* (non-Javadoc)
		 * @see de.xwic.appkit.core.util.InternalEvaluator.DupeChecker#checkDupe(java.lang.Object)
		 */
		@Override
		public boolean checkIfDupe(K what) {
			return containsKey(what);
		}
	}

	/**
	 * used to create objects in a lazy manner, used when you <b>may</b> need something<br><br>
	 * for instance<br>
	 * if (condition1 & condition2 & condition3 & condition4){<br>
	 * &nbsp;&nbsp;return <b>initializer.call();</b><br>
	 * }
	 * @author Alexandru Bledea
	 * @since Aug 12, 2013
	 * @param <V>
	 *
	 */
	public abstract static class LazyInit<V> implements IEvaluator<Object, V>, Callable<V> {

		/* (non-Javadoc)
		 * @see de.xwic.appkit.core.util.IEvaluator#evaluate(java.lang.Object)
		 */
		@Override
		public final V evaluate(Object obj) {
			try {
				return call();
			} catch (Exception e) {
				throw new RuntimeException("Error creating new object", e);
			}
		}

	}

	/**
	 * used to create objects in a lazy manner, used when you <b>may</b> need something<br><br>
	 * for instance<br>
	 * if (condition1 & condition2 & condition3 & condition4){<br>
	 * &nbsp;&nbsp;return <b>initializer.call();</b><br>
	 * }
	 * @author Alexandru Bledea
	 * @since Aug 12, 2013
	 * @param <V>
	 *
	 */
	public static class LazyCallable<V> extends LazyInit<V> {

		protected Callable<V> callable;

		/**
		 * @param callable
		 */
		public LazyCallable(Callable<V> callable) {
			if ((this.callable = callable) == null) {
				throw new RuntimeException("Missing initializer");
			}
		}

		/* (non-Javadoc)
		 * @see java.util.concurrent.Callable#call()
		 */
		@Override
		public final V call() throws Exception {
			return callable.call();
		}
	}

}

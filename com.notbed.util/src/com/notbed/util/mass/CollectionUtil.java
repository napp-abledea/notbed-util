package com.notbed.util.mass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.notbed.util.UString;
import com.notbed.util.mass.InternalEvaluator.EvaluationResult;
import com.notbed.util.mass.InternalEvaluator.IDupeChecker;


/**
 * @author Alexandru Bledea
 * @since Jul 30, 2013
 */
public class CollectionUtil {

	/**
	 * Used to transform an array of <b>objects</b> into a <b>collection</b><br>
	 * The values for the second collection are generated by using a <b>evaluator</b>. <br>
	 * Objects are null or that evaluate to <b>null</b> will be skipped
	 * @param objects the collection from which we create the collection
	 * @param evaluator the evaluator
	 * @param collection the collection where the evaluation result will be added
	 * @return the collection passed as argument filled with the evaluated values, if an object evaluates to null, it is not added to the collection
	 */
	public static <C extends Collection<V>, V, O> C createCollection(O[] objects, IEvaluator<O, V> evaluator, C collection) {
		if (objects == null) {
			return collection;
		}
		return createCollection(Arrays.asList(objects), evaluator, collection);
	}

	/**
	 * Used to transform a collection of <b>objects</b> into another <b>collection</b><br>
	 * The values for the second collection are generated by using a <b>evaluator</b>. <br>
	 * Objects are null or that evaluate to <b>null</b> will be skipped
	 * @param objects the collection from which we create the collection
	 * @param evaluator the evaluator
	 * @param collection the collection where the evaluation result will be added
	 * @return the collection passed as argument filled with the evaluated values, if an object evaluates to null, it is not added to the collection
	 */
	public static <C extends Collection<V>, V, O> C createCollection(Collection<? extends O> objects, IEvaluator<O, V> evaluator, C collection) {
		try {
			return createCollection(objects, evaluator, collection, true);
		} catch (DuplicateKeyException e) {
			return null; //not going to happen
		}
	}

	/**
	 * Used to transform a collection of <b>objects</b> into another <b>collection</b><br>
	 * The values for the second collection are generated by using a <b>evaluator</b>. <br>
	 * Use <b>allowDupes</b> if you want values that evaluate to the same object to be added to the collection.<br>
	 * Objects are null or that evaluate to <b>null</b> will be skipped
	 * @param objects the collection from which we create the collection
	 * @param evaluator the evaluator
	 * @param collection the collection where the evaluation result will be added
	 * @param allowDupes if we allow dupes in the collection
	 * @return the collection passed as argument filled with the evaluated values, if an object evaluates to null, it is not added to the collection
	 * @throws DuplicateKeyException if we have a duplicate key and we don't allow that
	 */
	public static <C extends Collection<V>, V, O> C createCollection(Collection<? extends O> objects, IEvaluator<O, V> evaluator, C collection,
			boolean allowDupes) throws DuplicateKeyException {
		return createCollection(objects, evaluator, collection, allowDupes, true);
	}

	/**
	 * Used to transform a collection of <b>objects</b> into another <b>collection</b><br>
	 * The values for the second collection are generated by using a <b>evaluator</b>. <br>
	 * Use <b>allowDupes</b> if you want values that evaluate to the same object to be added to the collection.<br>
	 * Use <b>skipNullObjects</b> to avoid {@link java.lang.NullPointerException} if a null value is in the collection<br>
	 * Objects that evaluate to <b>null</b> will be skipped
	 * @param objects the collection from which we create the collection
	 * @param evaluator the evaluator
	 * @param collection the collection where the evaluation result will be added
	 * @param allowDupes if we allow dupes in the collection
	 * @param skipNullObjects if we skip null objects, if we don't we will get a we will get a {@link java.lang.NullPointerException}
	 * @return the collection passed as argument filled with the evaluated values, if an object evaluates to null, it is not added to the collection
	 * @throws DuplicateKeyException if we have a duplicate key and we don't allow that
	 * @throws NullPointerException if there is a null value in the collection and we don't skip it
	 */
	public static <C extends Collection<V>, V, O> C createCollection(Collection<? extends O> objects, IEvaluator<O, V> evaluator, C collection,
			boolean allowDupes, boolean skipNullObjects) throws DuplicateKeyException {
		return createCollection(objects, evaluator, collection, allowDupes, skipNullObjects, true);
	}

	/**
	 * Used to transform a collection of <b>objects</b> into another <b>collection</b><br>
	 * The values for the second collection are generated by using a <b>evaluator</b>. <br>
	 * Use <b>allowDupes</b> if you want values that evaluate to the same key to be added to the collection.<br>
	 * Use <b>skipNullObjects</b> to avoid {@link java.lang.NullPointerException} if a null value is in the collection<br>
	 * Use <b>skipNullValues</b> to skip adding object to the map it evaluates to null
	 * @param objects the collection from which we create the collection
	 * @param evaluator the evaluator
	 * @param collection the collection where the evaluation result will be added
	 * @param allowDupes if we allow dupes in the collection
	 * @param skipNullObjects if we skip null objects, if we don't we will get a we will get a {@link java.lang.NullPointerException}
	 * @param skipNullValues if we want to skip the null values from being added to the collection
	 * @return the collection passed as argument filled with the evaluated values
	 * @throws DuplicateKeyException if we have a duplicate key and we don't allow that
	 * @throws NullPointerException if there is a null value in the collection and we don't skip it
	 */
	public static <C extends Collection<V>, V, O> C createCollection(Collection<? extends O> objects, IEvaluator<O, V> evaluator, final C collection,
			boolean allowDupes, boolean skipNullObjects, boolean skipNullValues) throws DuplicateKeyException {

		if (objects == null) {
			objects = new ArrayList<O>();
		}

		EvaluationResult<V> result = new EvaluationResult<V>();
		IDupeChecker<V> dupeChecker = new IDupeChecker<V>() {

			@Override
			public boolean checkIfDupe(V what) {
				return collection.contains(what);
			}
		};

		for (O t : objects) {
			result = InternalEvaluator.evaluate(t, skipNullObjects, evaluator, skipNullValues, allowDupes, dupeChecker, result);
			if (!result.skip()) {
				collection.add(result.getResult());
			}
		}
		return collection;
	}

	/**
	 * used to break a large collection into smaller collection
	 * @param collection
	 * @param maxElements
	 * @param clazz
	 * @return
	 */
	public static <O, C extends Object & Collection> List<Collection<O>> breakCollection(Collection<O> collection, int maxElements, Class<C> clazz) {
		List<Collection<O>> result = new ArrayList<Collection<O>>();
		C step = instantiate(clazz);
		Iterator<O> iterator = collection.iterator();
		while (iterator.hasNext()) {
			if (step.size() == maxElements) {
				result.add(step);
				step = instantiate(clazz);
			}
			step.add(iterator.next());
		}
		if (!step.isEmpty()) {
			result.add(step);
		}
		return result;
	}

	/**
	 * @param collection
	 * @param evaluator
	 * @param emptyMessage
	 * @return
	 */
	public static <O> String join(Collection<O> collection, IStringEvaluator<O> evaluator, String separator, String emptyMessage) {
		List<String> strings = new ArrayList();
		createCollection(collection, evaluator, strings);

		Iterator<String> iterator = strings.iterator();
		if (iterator.hasNext()) {
			return UString.join(iterator, separator);
		}
		return emptyMessage;
	}

	/**
	 * @param collection
	 * @param evaluator
	 * @return an empty string if no values
	 */
	public static <O> String join(Collection<O> collection, IStringEvaluator<O> evaluator, String separator) {
		return join(collection, evaluator, separator, "");
	}

	/**
	 * @param element
	 * @param collection
	 */
	public static <O> void addIfNotNull(O element, Collection<O> collection) {
		if (element != null) {
			collection.add(element);
		}
	}

	/**
	 * @param clazz
	 * @return
	 */
	public static <C extends Object & Collection> C instantiate(Class<C> clazz) {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Illegal implementation class " + clazz.getName());
		}
	}
}
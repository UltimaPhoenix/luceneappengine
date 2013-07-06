package com.googlecode.luceneappengine.objectify.util;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Work;

/**
 * Utility methods to work with {@link ObjectifyUtil}.
 * @author Fabio Grucci (github: <i>UltimaPhoenix</i>, bitbucket: <i>Dark_Phoenix</i>, googlecode: <i>fabio.grucci</i>)
 *
 */
public enum ObjectifyUtil {
	;

	public static <T> Key<T> newKey(final Class<T> clazz, final long id) {
		return Key.create(clazz, id);
	}
	
	public static <T> Key<T> newKey(final Class<T> clazz, final String name) {
		return Key.create(clazz, name);
	}
	
	public static <P, C> Key<C> newChildKey(final Class<P> parentClass, final long parentId, final Class<C> childClass, final long childId) {
		return Key.create(newKey(parentClass, parentId), childClass, childId);
	}
	
	public static <P, C> Key<C> newChildKey(final Class<P> parentClass, final long parentId, final Class<C> childClass, final String childName) {
		return Key.create(newKey(parentClass, parentId), childClass, childName);
	}
	
	public static <P, C> Key<C> newChildKey(final Class<P> parentClass, final String parentName, final Class<C> childClass, final long childId) {
		return Key.create(newKey(parentClass, parentName), childClass, childId);
	}
	
	public static <P, C> Key<C> newChildKey(final Class<P> parentClass, final String parentName, final Class<C> childClass, final String childName) {
		return Key.create(newKey(parentClass, parentName), childClass, childName);
	}
	
	public static <T> T getOrCreate(final Key<T> key, final ObjectifyBuilder<T> builder) {
		final Objectify objectify = ofy();
		T t = objectify.load().key(key).now();
		if(t == null) {
			t = objectify.transactNew(4, new Work<T>() {
				@Override
				public T run() {
					T t = objectify.load().key(key).now();
					if(t == null) {
						t = builder.newIstance(key);
						objectify.save().entity(t).now();
					}
					return t;
				}
			});
		}
		return t;
	}
	
	public static void commit(Objectify objectify) {
		objectify.getTransaction().commit();
	}
	

	public static void closeQuietly(Objectify objectify) {
		if (objectify.getTransaction().isActive())
			objectify.getTransaction().rollback();
	}
	
}

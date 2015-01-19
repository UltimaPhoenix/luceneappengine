package com.googlecode.luceneappengine.objectify.util;

import com.google.appengine.api.datastore.Key;
import com.sun.xml.internal.ws.api.pipe.FiberContextSwitchInterceptor;
import com.textquo.twist.ObjectStore;
import com.textquo.twist.types.Function;

import static com.textquo.twist.ObjectStoreService.store;

/**
 * Utility methods to work with {@link TwistUtil}.
 * @author Fabio Grucci (github: <i>UltimaPhoenix</i>, bitbucket: <i>Dark_Phoenix</i>, googlecode: <i>fabio.grucci</i>)
 *
 */
public enum TwistUtil {
	;

	public static <T> T getOrCreate(Class<T> clazz, final Key key, final TwistBuilder<T> builder) {
		final ObjectStore objectStore = store();
		T t = objectStore.get(clazz, key);
		if(t == null) {
			store().transact(new Function<T>() {
				@Override
				public T execute() {
					T t = null;
					t = (T) objectStore.get(t.getClass(), key);
					if(t == null){
						t = builder.newIstance(key);
						objectStore.put(t);
					}
					return t;
				}
			});
		}
		return t;
	}
	
	public static void commit(ObjectStore store) {
		store.getTransaction().commit();
	}
	

	public static void closeQuietly(ObjectStore store) {
		if (store.getTransaction().isActive())
			store.getTransaction().rollback();
	}
	
}

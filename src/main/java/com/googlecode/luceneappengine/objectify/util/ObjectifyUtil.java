//package com.googlecode.luceneappengine.objectify.util;
//
//import com.googlecode.objectify.Key;
//import com.googlecode.objectify.Objectify;
//import com.googlecode.objectify.Work;
//
//import static com.googlecode.objectify.ObjectifyService.ofy;
//
///**
// * Utility methods to work with {@link ObjectifyUtil}.
// * @author Fabio Grucci (github: <i>UltimaPhoenix</i>, bitbucket: <i>Dark_Phoenix</i>, googlecode: <i>fabio.grucci</i>)
// *
// */
//public enum ObjectifyUtil {
//	;
//
//	public static <T> T getOrCreate(final Key<T> key, final ObjectifyBuilder<T> builder) {
//		final Objectify objectify = ofy();
//		T t = objectify.load().key(key).now();
//		if(t == null) {
//			t = objectify.transactNew(4, new Work<T>() {
//				@Override
//				public T run() {
//					T t = ofy().load().key(key).now();
//					if(t == null) {
//						t = builder.newInstance(key);
//						ofy().save().entity(t).now();
//					}
//					return t;
//				}
//			});
//		}
//		return t;
//	}
//
//	public static void commit(Objectify objectify) {
//		objectify.getTransaction().commit();
//	}
//
//
//	public static void closeQuietly(Objectify objectify) {
//		if (objectify.getTransaction().isActive())
//			objectify.getTransaction().rollback();
//	}
//
//}

package com.googlecode.luceneappengine.objectify.util;


import com.google.appengine.api.datastore.Key;

public interface TwistBuilder<T> {
	
	public T newIstance(Key key);
	
}
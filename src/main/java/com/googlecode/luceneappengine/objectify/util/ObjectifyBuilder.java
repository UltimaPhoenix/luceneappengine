package com.googlecode.luceneappengine.objectify.util;

import com.googlecode.objectify.Key;

public interface ObjectifyBuilder<T> {
	
	public T newIstance(Key<T> key);
	
}
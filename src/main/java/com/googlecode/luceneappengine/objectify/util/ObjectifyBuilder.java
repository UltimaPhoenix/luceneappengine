package com.googlecode.luceneappengine.objectify.util;

import com.googlecode.objectify.Key;

public interface ObjectifyBuilder<T> {
	
	T newInstance(Key<T> key);
	
}
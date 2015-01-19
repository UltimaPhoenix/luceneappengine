package com.googlecode.luceneappengine;


import com.google.appengine.api.datastore.Key;
import com.textquo.twist.annotations.*;

/**
 * Class representing a lock used during indexing operations.
 * 
 * @author Fabio Grucci (github: <i>UltimaPhoenix</i>, bitbucket: <i>Dark_Phoenix</i>, googlecode: <i>fabio.grucci</i>)
 * @see GaeLockFactory
 *
 */
@Entity
@Unindexed
@Cached
class GaeLock {

	@Ancestor
	Key indexKey;
	
	@Id
	String name;
	
	boolean locked;
	
	@SuppressWarnings("unused")
	private GaeLock() {/* objectify */}
	
	public GaeLock(Key indexKey, String name) {
		this.indexKey = indexKey;
		this.name = name;
	}
	
}

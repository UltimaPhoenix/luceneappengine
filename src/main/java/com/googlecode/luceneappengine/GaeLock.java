package com.googlecode.luceneappengine;


import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Unindex;

/**
 * Class representing a lock used during indexing operations.
 * 
 * @author Fabio Grucci (github: <i>UltimaPhoenix</i>, bitbucket: <i>Dark_Phoenix</i>, googlecode: <i>fabio.grucci</i>)
 * @see GaeLockFactory
 *
 */
@Entity
@Unindex
@Cache
class GaeLock {

	@Parent
	Key<LuceneIndex> indexKey;
	
	@Id
	String name;
	
	boolean locked;
	
	@SuppressWarnings("unused")
	private GaeLock() {/* objectify */}
	
	public GaeLock(Key<LuceneIndex> indexKey, String name) {
		this.indexKey = indexKey;
		this.name = name;
	}
	
}

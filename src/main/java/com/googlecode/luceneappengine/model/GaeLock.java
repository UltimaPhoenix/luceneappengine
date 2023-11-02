package com.googlecode.luceneappengine.model;


//import com.googlecode.luceneappengine.GaeLockFactory;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class representing a lock used during indexing operations.
 * 
 * @author Fabio Grucci (github: <i>UltimaPhoenix</i>, bitbucket: <i>Dark_Phoenix</i>, googlecode: <i>fabio.grucci</i>)
 *
 */
@Getter
@Setter
@NoArgsConstructor
public final class GaeLock implements FireStoreEntity {

	/**
	 * The lock name
	 */
	@DocumentId
	public String name;

	/**
	 * A boolean that represent if the lock is locked.
	 */
	public boolean locked;

	/**
	 * Create an index lock.
	 * @param name the name of the lock
	 */
	public GaeLock(String name) {
		this.name = name;
	}



}

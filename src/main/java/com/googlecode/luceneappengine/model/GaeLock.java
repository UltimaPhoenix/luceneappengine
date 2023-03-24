package com.googlecode.luceneappengine.model;


//import com.googlecode.luceneappengine.GaeLockFactory;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.Exclude;
import com.google.cloud.firestore.annotation.PropertyName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class representing a lock used during indexing operations.
 * 
 * @author Fabio Grucci (github: <i>UltimaPhoenix</i>, bitbucket: <i>Dark_Phoenix</i>, googlecode: <i>fabio.grucci</i>)
 * @see GaeLockFactory
 *
 */
//@Entity
//@Unindex
//@Cache
@Getter
@Setter
@NoArgsConstructor
public final class GaeLock implements FireStoreEntity {

//	public final DocumentReference parentCollection;

	@DocumentId
	public String name;

	public boolean locked;
	
	public GaeLock(LuceneIndex index, String name) {
//		this.index = index;
		this.name = name;
	}



}

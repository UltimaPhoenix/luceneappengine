package com.googlecode.luceneappengine;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Unindex;

/**
 * Class representing a GAE store index.
 * 
 * @author Fabio Grucci (github: <i>UltimaPhoenix</i>, bitbucket: <i>Dark_Phoenix</i>, googlecode: <i>fabio.grucci</i>)
 *
 */
@Entity
@Unindex
@Cache
public class LuceneIndex {

	@Id
	String name;
	
	@SuppressWarnings("unused")
	private LuceneIndex() {/* objectify */}
	
	LuceneIndex(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	/**
	 * Return the corresponding {@link GaeDirectory} ready to use.
	 * @return The corresponding {@link GaeDirectory}
	 */
	public GaeDirectory directory() {
		return new GaeDirectory(this);
	}
}

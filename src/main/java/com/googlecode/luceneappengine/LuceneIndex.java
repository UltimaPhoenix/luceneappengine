package com.googlecode.luceneappengine;


import com.textquo.twist.annotations.Cached;
import com.textquo.twist.annotations.Entity;
import com.textquo.twist.annotations.Id;
import com.textquo.twist.annotations.Unindexed;

/**
 * Class representing a GAE store index.
 * 
 * @author Fabio Grucci (github: <i>UltimaPhoenix</i>, bitbucket: <i>Dark_Phoenix</i>, googlecode: <i>fabio.grucci</i>)
 * @author Kerby Martino
 */
@Entity
@Unindexed
@Cached
public class LuceneIndex {

	@Id
	String name;
	
	@SuppressWarnings("unused")
	private LuceneIndex() {}
	
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

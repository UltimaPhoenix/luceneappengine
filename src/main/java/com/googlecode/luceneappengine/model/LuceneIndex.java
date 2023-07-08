package com.googlecode.luceneappengine.model;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

/**
 * Class representing a GAE store index.
 * 
 * @author Fabio Grucci (github: <i>UltimaPhoenix</i>, bitbucket: <i>Dark_Phoenix</i>, googlecode: <i>fabio.grucci</i>)
 *
 */
//@Entity
//@Unindex
//@Cache
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public final class LuceneIndex implements FireStoreEntity {

	@DocumentId
	String name;

	public LuceneIndex(String name) {
		this.name = name;
	}

}

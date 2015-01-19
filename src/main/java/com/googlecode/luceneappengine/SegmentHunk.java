package com.googlecode.luceneappengine;


import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Key;
import com.textquo.twist.annotations.*;
import com.textquo.twist.annotations.Entity;

@Entity
@Unindexed
@Cached
class SegmentHunk {
	
	public static final int MAX_BYTES_LENGTH = 1000 * 1000;
	
	@Id
	Long id;

	@Parent
	Key segment;
	
	byte[] bytes;
	
	@SuppressWarnings("unused")
	private SegmentHunk() {}
	
	SegmentHunk(Key segment, Long id) {
		this.id = id;
		this.segment = segment;
		this.bytes = new byte[0];
	}

	@Override
	public String toString() {
		return segment.getName() + "[" + id + "]";
	}


}

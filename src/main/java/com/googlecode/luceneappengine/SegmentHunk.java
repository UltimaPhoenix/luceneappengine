package com.googlecode.luceneappengine;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Unindex;

@Entity
@Unindex
@Cache
class SegmentHunk {
	
	public static final int MAX_BYTES_LENGTH = 1000 * 1000;
	
	@Id
	Long id;
	
	@Parent
	Key<Segment> segment;
	
	byte[] bytes;
	
	@SuppressWarnings("unused")
	private SegmentHunk() {/* objectify */}
	
	SegmentHunk(Key<Segment> segment, Long id) {
		this.id = id;
		this.segment = segment;
		this.bytes = new byte[0];
	}

	@Override
	public String toString() {
		return segment.getName() + "[" + id + "]";
	}


}

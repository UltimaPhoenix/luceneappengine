package com.googlecode.luceneappengine;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Key;
import com.textquo.twist.annotations.*;
import com.textquo.twist.object.KeyStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.textquo.twist.ObjectStoreService.store;


@Entity
@Unindexed
@Cached
class Segment {
	
	private static final Logger log = LoggerFactory.getLogger(Segment.class);
	
	@Ancestor
	Key index;
	
	@Id
	String name;
	
	long length;
	
	long lastModified;
	
	long hunkCount;

	@SuppressWarnings("unused")
	private Segment() {/* objectify */}
	
	public Segment(Key index, String name) {
		this.name = name;
		this.index = index;
	}
	
	public SegmentHunk getHunk(int index) {//GAE id cannotstart from zero
		return store().get(SegmentHunk.class, buildSegmentHunkKey(index));
	}
	
	public SegmentHunk newHunk() {
		hunkCount++;
		log.debug("Created Hunk '{}-{}-{}'.", new Object[] {index.getName(), name, hunkCount});
		final SegmentHunk newHunk = new SegmentHunk(getKey(), hunkCount);
		
		return newHunk;
	}
	
	List<Key> getHunkKeys(Key currentSegmentKey) {
		List<Key> hunkKeys = new ArrayList<Key>((int) hunkCount);
		for (int i = 0; i < hunkCount; i++) {
			hunkKeys.add(KeyStructure.createKey(currentSegmentKey, SegmentHunk.class, i + 1));
		}
		return hunkKeys;
	}
	
	private Key buildSegmentHunkKey(int index) {
		return KeyStructure.createKey(this.getKey(), SegmentHunk.class, index + 1);
	}
	private Key getKey() {
		return KeyStructure.createKey(index, Segment.class, name);
	}
	
	@Override
	public String toString() {
		return index.getName() + "->" + name;
	}

}

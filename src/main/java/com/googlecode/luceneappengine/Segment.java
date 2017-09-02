package com.googlecode.luceneappengine;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Unindex;

@Entity
@Unindex
@Cache
class Segment {
	
	private static final Logger log = LoggerFactory.getLogger(Segment.class);
	
	@Parent
	Key<LuceneIndex> index;
	
	@Id
	String name;
	
	long length;
	
	long lastModified;
	
	long hunkCount;

	@SuppressWarnings("unused")
	private Segment() {/* objectify */}
	
	public Segment(Key<LuceneIndex> index, String name) {
		this.name = name;
		this.index = index;
	}
	
	public SegmentHunk getHunk(int index) {//GAE id cannotstart from zero
		return ofy().load().key(buildSegmentHunkKey(index)).now();
	}
	
	public SegmentHunk newHunk() {
		hunkCount++;
		log.debug("Created Hunk '{}-{}-{}'.", index.getName(), name, hunkCount);
		return new SegmentHunk(getKey(), hunkCount);
	}
	
	List<Key<SegmentHunk>> getHunkKeys(Key<Segment> currentKey) {
		List<Key<SegmentHunk>> hunkKeys = new ArrayList<Key<SegmentHunk>>((int) hunkCount);
		for (int i = 0; i < hunkCount; i++) {
			hunkKeys.add(Key.create(currentKey, SegmentHunk.class, i + 1));
		}
		return hunkKeys;
	}
	
	private Key<SegmentHunk> buildSegmentHunkKey(int index) {
		return Key.create(getKey(), SegmentHunk.class, index + 1);
	}
	private Key<Segment> getKey() {
		return Key.create(index, Segment.class, name);
	}
	
	@Override
	public String toString() {
		return index.getName() + "->" + name;
	}

}

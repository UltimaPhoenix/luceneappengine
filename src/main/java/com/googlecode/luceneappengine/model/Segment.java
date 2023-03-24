package com.googlecode.luceneappengine.model;


import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.annotation.DocumentId;
import com.googlecode.luceneappengine.model.repository.LaeContext;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;


//@Entity
//@Unindex
//@Cache
@Getter
@Setter
@NoArgsConstructor
public final class Segment implements FireStoreEntity {
	
	private static final Logger log = LoggerFactory.getLogger(Segment.class);


//	@Parent
//	public final LuceneIndex index;

	@DocumentId
	public String name;
	
	public long length;
	
	public long lastModified;
	
	public long hunkCount;

	public Segment(String name) {
		this.name = name;
//		this.index = index;
	}
	
	public SegmentHunk getHunk(LaeContext laeContext, DocumentReference segmentPath, int index) {//GAE id can not start from zero
		try {
			return laeContext.firestore.document(segmentPath.getPath())
					.collection(laeContext.firestoreCollectionMapper.getCollectionName(SegmentHunk.class))
					.document(String.valueOf(index))
					.get().get().toObject(SegmentHunk.class);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
	
	public SegmentHunk newHunk() {
		hunkCount++;
		log.debug("Created Hunk '{}-{}-{}'.", "index.getName()", name, hunkCount);
		return new SegmentHunk(String.valueOf(hunkCount - 1));
	}
	
//	List<Key<SegmentHunk>> getHunkKeys(Key<Segment> currentKey) {
//		List<Key<SegmentHunk>> hunkKeys = new ArrayList<Key<SegmentHunk>>((int) hunkCount);
//		for (int i = 0; i < hunkCount; i++) {
//			hunkKeys.add(Key.create(currentKey, SegmentHunk.class, i + 1));
//		}
//		return hunkKeys;
//	}
//
//	private Key<SegmentHunk> buildSegmentHunkKey(int index) {
//		return Key.create(getKey(), SegmentHunk.class, index + 1);
//	}
//	private Key<Segment> getKey() {
//		return Key.create(index, Segment.class, name);
//	}
//
	@Override
	public String toString() {
		return "->" + name;
	}

}

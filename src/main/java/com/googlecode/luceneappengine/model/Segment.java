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

/**
 * A file in the index.
 */
@Getter
@Setter
@NoArgsConstructor
public final class Segment implements FireStoreEntity {
	
	private static final Logger log = LoggerFactory.getLogger(Segment.class);


//	@Parent
//	public final LuceneIndex index;

	/**
	 * The filename
	 */
	@DocumentId
	public String name;
	/**
	 * The file length
	 */
	public long length;
	/**
	 * The last modified date.
	 */
	public long lastModified;
	/**
	 * The number of hunks.
	 */
	public long hunkCount;

	/**
	 * Create a new segment.
	 * @param name The name of the segment
	 */
	public Segment(String name) {
		this.name = name;
//		this.index = index;
	}

	/**
	 * Return a specific hunk in the segment.
	 * @param laeContext The context
	 * @param segmentPath The document reference
	 * @param index The index of the segment
	 * @return the specific segment hunk
	 */
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

	/**
	 * Create a new segment hunk.
	 * @return create a new hunk in this segment
	 */
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

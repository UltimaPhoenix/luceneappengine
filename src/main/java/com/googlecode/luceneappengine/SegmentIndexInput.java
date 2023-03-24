package com.googlecode.luceneappengine;

import static com.googlecode.luceneappengine.model.SegmentHunk.MAX_BYTES_LENGTH;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.google.cloud.firestore.DocumentReference;
import com.googlecode.luceneappengine.model.LuceneIndex;
import com.googlecode.luceneappengine.model.Segment;
import com.googlecode.luceneappengine.model.SegmentHunk;
import com.googlecode.luceneappengine.model.repository.LaeContext;
import org.apache.lucene.store.IndexInput;

class SegmentIndexInput extends IndexInput {

	private final Segment segment;

	private final DocumentReference segmentRef;

	private final LaeContext laeContext;

	private SegmentHunk hunk;
	
	private long filePointer;

	private int hunkPointer;

	private int hunkIndex;
	
//	public SegmentIndexInput(Segment segment) {
//		super(segment.name);
//		this.segment = segment;
//		this.hunk = segment.getHunk(0);
//	}

	public SegmentIndexInput(LaeContext laeContext, LuceneIndex luceneIndex, String name) throws ExecutionException, InterruptedException {
		super(name);
		this.laeContext = laeContext;
		this.segmentRef = laeContext.firestore
				.collection(laeContext.firestoreCollectionMapper.getCollectionName(LuceneIndex.class))
				.document(luceneIndex.getName())
				.collection(laeContext.firestoreCollectionMapper.getCollectionName(Segment.class))
				.document(name);
		this.segment = segmentRef
				.get().get()
				.toObject(Segment.class);
		this.hunk = laeContext.firestore
				.document(segmentRef.getPath())
				.collection(laeContext.firestoreCollectionMapper.getCollectionName(SegmentHunk.class))
				.document("0")
				.get().get()
				.toObject(SegmentHunk.class);
	}

	/**
	 * Used by {@link #slice(String, long, long)}.
	 * @param sliceParent parent {@link SegmentIndexInput}
	 * @param description
	 * @param offset
	 */
	private SegmentIndexInput(SegmentIndexInput sliceParent, String description, long offset) {
		super(description);
		this.laeContext = sliceParent.laeContext;
		this.segment = sliceParent.segment;
		this.segmentRef = sliceParent.segmentRef;
		this.filePointer = offset;
		
		this.hunkIndex = (int) (offset / MAX_BYTES_LENGTH);
		this.hunkPointer = (int) (offset % MAX_BYTES_LENGTH);
		
		if (sliceParent.hunkIndex == this.hunkIndex) {//avoid loading 2 times from persistence
			this.hunk = sliceParent.hunk;
		} else {
			this.hunk = segment.getHunk(laeContext, segmentRef, hunkIndex);
		}
	}
	/*
	 * (non-Javadoc)
	 * @see org.apache.lucene.store.IndexInput#close()
	 */
	@Override
	public void close() throws IOException {
		hunk = null;
	}
	/*
	 * (non-Javadoc)
	 * @see org.apache.lucene.store.IndexInput#getFilePointer()
	 */
	@Override
	public long getFilePointer() {
		return filePointer;
	}
	/*
	 * (non-Javadoc)
	 * @see org.apache.lucene.store.IndexInput#length()
	 */
	@Override
	public long length() {
		return segment.length;
	}
	/*
	 * (non-Javadoc)
	 * @see org.apache.lucene.store.IndexInput#seek(long)
	 */
	@Override
	public void seek(long pos) throws IOException {
		final int newHunkIndex = (int) (pos/MAX_BYTES_LENGTH);
		if(newHunkIndex != hunkIndex) {
			hunk = segment.getHunk(laeContext, segmentRef, newHunkIndex);
			hunkIndex = newHunkIndex;
		}
		hunkPointer = (int) (pos%MAX_BYTES_LENGTH);
		filePointer = pos;
	}
	/*
	 * (non-Javadoc)
	 * @see org.apache.lucene.store.DataInput#readByte()
	 */
	@Override
	public byte readByte() throws IOException {
		if(hunkPointer >= hunk.bytes.length) {
			hunkPointer = 0;
			hunk = segment.getHunk(laeContext, segmentRef, ++hunkIndex);
		}
		byte b = hunk.bytes[hunkPointer++];
		filePointer++;
		return b;
	}
	/*
	 * (non-Javadoc)
	 * @see org.apache.lucene.store.DataInput#readBytes(byte[], int, int)
	 */
	@Override
	public void readBytes(byte[] b, int offset, int len) throws IOException {
		for (int i = 0; i < len && filePointer < segment.length; i++)
			b[offset++] = this.readByte();
	}
	/*
	 * (non-Javadoc)
	 * @see org.apache.lucene.store.IndexInput#slice(java.lang.String, long, long)
	 */
	@Override
	public IndexInput slice(final String sliceDescription, final long offset, final long length) throws IOException {
		return new SegmentIndexInput(this, sliceDescription, offset) {
			@Override
			public long getFilePointer() {
				return super.getFilePointer() - offset;
			}
			@Override
			public long length() {
				return length;
			}
			@Override
			public void seek(long pos) throws IOException {
				super.seek(offset + pos);
			}
		};
	}
	
}

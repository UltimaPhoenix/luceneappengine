package com.googlecode.luceneappengine;

import static com.googlecode.luceneappengine.SegmentHunk.MAX_BYTES_LENGTH;

import java.io.IOException;

import org.apache.lucene.store.IndexInput;

class SegmentIndexInput extends IndexInput {

	private final Segment segment;
	
	private SegmentHunk hunk;
	
	private long filePointer;

	private int hunkPointer;

	private int hunkIndex;

	public SegmentIndexInput(Segment segment) {
		super(segment.name);
		this.segment = segment;
		this.hunk = segment.getHunk(0);
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
			hunk = segment.getHunk(newHunkIndex);
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
			hunk = segment.getHunk(++hunkIndex);
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

}

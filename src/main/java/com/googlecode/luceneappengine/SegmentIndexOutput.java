package com.googlecode.luceneappengine;

import static com.googlecode.luceneappengine.SegmentHunk.MAX_BYTES_LENGTH;
import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.util.Arrays;
import java.util.zip.CRC32;

import org.apache.lucene.store.IndexOutput;
import com.googlecode.objectify.cache.PendingFutures;

class SegmentIndexOutput extends IndexOutput {

	private final Segment segment;
	
	private long filePointer;
	
	private int hunkIndex;
	
	private int hunkPointer;
	
	private SegmentHunk hunk;
	
	private LimitedByteArrayWriter writer;
	
	private final CRC32 crc = new CRC32();
	
	private int lastFlushIndex;
	
	public SegmentIndexOutput(Segment segment) {
		this.segment = segment;
		this.hunk = segment.getHunk(0);
		this.writer = new LimitedByteArrayWriter(hunk.bytes, SegmentHunk.MAX_BYTES_LENGTH);
	}
	/*
	 * (non-Javadoc)
	 * @see org.apache.lucene.store.IndexOutput#close()
	 */
	@Override
	public void close() throws IOException {
	    flush();
	}
	/*
	 * (non-Javadoc)
	 * @see org.apache.lucene.store.IndexOutput#flush()
	 */
	@Override
	public void flush() throws IOException {
	    segment.lastModified = System.currentTimeMillis();
		writer.flush();
		hunk.bytes = writer.getBytes();
		ofy().save().entity(segment);
		save(hunk);
		PendingFutures.completeAllPendingFutures();
		/* nothing to do */
	}
	/*
	 * (non-Javadoc)
	 * @see org.apache.lucene.store.IndexOutput#getFilePointer()
	 */
	@Override
	public long getFilePointer() {
		return filePointer;
	}
	/*
	 * (non-Javadoc)
	 * @see org.apache.lucene.store.IndexOutput#length()
	 */
	@Override
	public long length() throws IOException {
		return segment.length;
	}
	/*
	 * (non-Javadoc)
	 * @see org.apache.lucene.store.IndexOutput#seek(long)
	 */
	@Override
	public void seek(long pos) throws IOException {// TODO: remove when seek will be removed (probably with Lucene 5 release)
		final int newHunkIndex = (int) (pos / MAX_BYTES_LENGTH);
		if (newHunkIndex != hunkIndex) {
			for (int i = hunkIndex + 1; i <= newHunkIndex; i++) {
				SegmentHunk newHunk = segment.newHunk();
				ofy().save().entity(newHunk);
//				hunk = segment.getHunk(newHunkIndex);
				if (i != hunkIndex) {
				    crc.update(Arrays.copyOf(new byte[0], SegmentHunk.MAX_BYTES_LENGTH));
//					hunk.bytes = new byte[SegmentHunk.MAX_BYTES_LENGTH];//useless thanks to LimitedByteArrayWriter
					//save
				} else {
				    lastFlushIndex = 0;
					hunk = segment.getHunk(newHunkIndex);
					writer = new LimitedByteArrayWriter(hunk.bytes, SegmentHunk.MAX_BYTES_LENGTH);
				}
			}
			hunkIndex = newHunkIndex;
			segment.length = Math.max(segment.length, pos);
		}
		hunkPointer = (int) (pos%MAX_BYTES_LENGTH);
		writer.seek(hunkPointer);
		filePointer = pos;
	}
	/*
	 * (non-Javadoc)
	 * @see org.apache.lucene.store.DataOutput#writeByte(byte)
	 */
	@Override
	public void writeByte(byte b) throws IOException {
		if(hunkPointer >= MAX_BYTES_LENGTH) {
			writer.flush();
			hunk.bytes = writer.getBytes();
			writer.close();
			save(hunk);
			hunk = segment.newHunk();
			writer = new LimitedByteArrayWriter(hunk.bytes, MAX_BYTES_LENGTH);
			hunkPointer = 0;
			lastFlushIndex = 0;
			hunkIndex++;
		}
		writer.write(b);
		filePointer++;
		hunkPointer++;
		if(filePointer > segment.length)
			segment.length = filePointer;
	}
    /*
	 * (non-Javadoc)
	 * @see org.apache.lucene.store.DataOutput#writeBytes(byte[], int, int)
	 */
	@Override
	public void writeBytes(byte[] b, int offset, int len) throws IOException {
		for (int i = 0; i < len; i++)
			writeByte(b[offset++]);
	}
	/*
	 * (non-Javadoc)
	 * @see org.apache.lucene.store.IndexOutput#getChecksum()
	 */
    @Override
    public long getChecksum() throws IOException {
        flush();
        return crc.getValue();
    }
    private void save(final SegmentHunk hunk) {
        ofy().save().entity(hunk);
        crc.update(hunk.bytes, lastFlushIndex, hunk.bytes.length - lastFlushIndex);
        if (writer.position > hunk.bytes.length) {// TODO: remove when seek will be removed (probably with Lucene 5 release)
            crc.update(Arrays.copyOf(new byte[0], writer.position - hunk.bytes.length));// TODO: remove when seek will be removed (probably with Lucene 5 release)
        }// TODO: remove when seek will be removed (probably with Lucene 5 release)
        lastFlushIndex = hunk.bytes.length;
    }

}

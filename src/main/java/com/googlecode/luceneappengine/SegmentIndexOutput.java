package com.googlecode.luceneappengine;

import com.google.cloud.firestore.DocumentReference;
import com.googlecode.luceneappengine.model.LuceneIndex;
import com.googlecode.luceneappengine.model.Segment;
import com.googlecode.luceneappengine.model.SegmentHunk;
import com.googlecode.luceneappengine.model.repository.LaeContext;
import org.apache.lucene.store.IndexOutput;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.zip.CRC32;

import static com.googlecode.luceneappengine.model.SegmentHunk.MAX_BYTES_LENGTH;

class SegmentIndexOutput extends IndexOutput {

	private final Segment segment;

	private final DocumentReference segmentRef;

	private final LaeContext laeContext;
	
	private long filePointer;
	
	private int hunkPointer;
	
	private SegmentHunk hunk;
	
	private LimitedByteArrayWriter writer;
	
	private final CRC32 crc = new CRC32();
	
	private int lastFlushIndex;

	public SegmentIndexOutput(LaeContext laeContext, LuceneIndex luceneIndex, Segment segment) throws ExecutionException, InterruptedException {
		super(segment.name, segment.name);
		this.laeContext = laeContext;
		this.segment = segment;
		this.segmentRef = laeContext.firestore
				.collection(laeContext.firestoreCollectionMapper.getCollectionName(LuceneIndex.class))
				.document(luceneIndex.getName())
				.collection(laeContext.firestoreCollectionMapper.getCollectionName(Segment.class))
				.document(segment.name);

		this.hunk = segment.getHunk(laeContext, segmentRef, 0);
		this.writer = new LimitedByteArrayWriter(hunk.bytes, MAX_BYTES_LENGTH);
	}
	/*
	 * (non-Javadoc)
	 * @see org.apache.lucene.store.IndexOutput#close()
	 */
	@Override
	public void close() throws IOException {
	    flush();
	}
	public void flush() throws IOException {
//	    segment.lastModified = System.currentTimeMillis();
		writer.flush();
		hunk.bytes = writer.getBytes();
		try {
			laeContext.firestore.document(segmentRef.getPath()).set(segment).get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException(e);
		} catch (ExecutionException e) {
			throw new IOException(e);
		}
//		laeContext.segmentRepository.save(segment);
		save(hunk);
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
    private void save(final SegmentHunk hunk) throws IOException {
		try {
			laeContext.firestore
					.document(segmentRef.getPath())
					.collection(laeContext.firestoreCollectionMapper.getCollectionName(SegmentHunk.class))
					.document(hunk.id)
					.set(hunk)
					.get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException(e);
		} catch (ExecutionException e) {
			throw new IOException(e);
		}
        crc.update(hunk.bytes, lastFlushIndex, writer.position - lastFlushIndex);
        lastFlushIndex = writer.position;
    }

}

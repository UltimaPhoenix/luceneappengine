package com.googlecode.luceneappengine.model;


import com.google.cloud.firestore.Blob;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.Exclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A portion of a file in the index.
 */
@Getter
@Setter
@NoArgsConstructor
public final class SegmentHunk implements FireStoreEntity {

	/**
	 * Calculated max number of bytes.
	 */
	public static final int MAX_BYTES_LENGTH = 1000 * 1000;

	/**
	 * The segment id.
	 */
	@DocumentId
	public String id;

	/**
	 * Bytes of the segment
	 */
	@Exclude
	public byte[] bytes;

	/**
	 * Create a segment hunk with a specific id.
	 * @param id The id of the segment
	 */
	public SegmentHunk(String id) {
		this.id = id;
		this.bytes = new byte[0];
	}

	@Override
	public String toString() {
		return  "[" + id + "]";
	}

	/**
	 * Getter for firestore
	 * @return the content in bytes as Blob
	 */
	public Blob getContent() {
		return Blob.fromBytes(bytes);
	}

	/**
	 * Setter for firestore
	 * @param blob the blob
	 */
	public void setContent(Blob blob) {
		this.bytes = blob.toBytes();
	}

	/**
	 * Return the bytes of the segment
	 * @return bytes of segment
	 */
	@Exclude
	public byte[] getBytes() {
		return bytes;
	}

	/**
	 * Setter in bytes.
	 * @param bytes bytes of segment
	 */
	@Exclude
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
}

package com.googlecode.luceneappengine.model;


import com.google.cloud.firestore.Blob;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.Exclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Entity
//@Unindex
//@Cache
@Getter
@Setter
@NoArgsConstructor
public final class SegmentHunk implements FireStoreEntity {
	
	public static final int MAX_BYTES_LENGTH = 1000 * 1000;
	
//	@Id
	@DocumentId
	public String id;

	@Exclude
	public byte[] bytes;
	
	public SegmentHunk(String id) {
		this.id = id;
		this.bytes = new byte[0];
	}

	@Override
	public String toString() {
		return  "[" + id + "]";
	}


	public Blob getContent() {
		return Blob.fromBytes(bytes);
	}

	public void setContent(Blob blob) {
		this.bytes = blob.toBytes();
	}

	@Exclude
	public byte[] getBytes() {
		return bytes;
	}

	@Exclude
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
}

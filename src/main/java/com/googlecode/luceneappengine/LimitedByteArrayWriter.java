package com.googlecode.luceneappengine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

class LimitedByteArrayWriter {

	private final int maxAllowedSize;
	
	byte[] source;
	
	int position;
	
	int length;
	
	ByteArrayOutputStream stream;

	public LimitedByteArrayWriter(byte[] source, int maxAllowedSize) {
		if(source.length > maxAllowedSize)
			throw new IllegalArgumentException("Bytes length cannot be greater than max allowed size.");
		this.maxAllowedSize = maxAllowedSize;
		this.source = source;
		this.length = source.length;
	}
	
	public void write(byte b) throws IOException {
		if(position > length) {//check if seek ahead
			if(stream != null) {
				expandByteArrayToPosition(position);
			} else {
				if(length < position) {
					expandByteArrayToPosition(position);
				}
			}
		}
		if(position + 1 > maxAllowedSize)
			throw new IOException("Size cannot exceed " + maxAllowedSize + " bytes.");
		if(source != null && source.length > position) {
			source[position] = b;
		} else {//there is not enough space in array
			if(stream == null) {
				stream = new ByteArrayOutputStream(source.length);
				stream.write(source, 0, source.length);
				source = null;
			}
			stream.write(b);
			length++;
		}
		position++;
	}
	
	public void write(byte[] b, int offset, int len) throws IOException {
		for (int i = 0; i < len; i++)
			write(b[offset++]);
	}
	
	public void seek(int position) throws IOException {
		if(position < 0)
			throw new IndexOutOfBoundsException("Cannot seek at position " + position + ".");
		if(this.position > position) {//seek back
			if(stream != null) {
				stream.flush();
				stream.close();
				source = stream.toByteArray();
				stream = null;
			}
		} else {//seek forward
			/* do everything when next write occur */
		}
		this.position = position;
	}

	protected void expandByteArrayToPosition(int position) throws IOException {
		int newZeroBytes = position - this.length;
//		System.out.println("exapnd to: " + newZeroBytes);
		this.position = this.length;
		write(new byte[newZeroBytes], 0, newZeroBytes);
	}
	
	public int length() {
		return this.length;
	}
	
	public byte[] getBytes() {
		if(stream != null)
			source = stream.toByteArray();
		return source;
	}
	
	public void close() throws IOException {
		if(stream != null) {
			source = stream.toByteArray();
			stream.close();
		}
	}

	public void flush() throws IOException {
		if(stream != null)
			stream.flush();
	}
	
}

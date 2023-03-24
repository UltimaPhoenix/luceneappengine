package com.googlecode.luceneappengine;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class LimitedByteArrayWriterTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Test
	public void testWriteByte_IllegalArgument() throws IOException {
		
		/* in */
		final byte[] input = {0};
		final byte maxAllowedSize = 0;
		/* do */
		final byte write = 1;
		
		expectedException.expect(IllegalArgumentException.class);
		
		final LimitedByteArrayWriter writer = new LimitedByteArrayWriter(input, maxAllowedSize);
		writer.write(write);
	}
	
	@Test
	public void testWriteByte_IOException() throws IOException {
		/* in */
		final byte[] input = {};
		final byte maxAllowedSize = 0;
		/* do */
		final byte write = 1;
		
		expectedException.expect(IOException.class);
		
		final LimitedByteArrayWriter writer = new LimitedByteArrayWriter(input, maxAllowedSize);
		writer.write(write);
		
	}
	
	@Test
	public void testWriteByte_1() throws IOException {
		/* in */
		final byte[] input = {0};
		final byte maxAllowedSize = 1;
		/* do */
		final byte write = 1;
		/* out */
		final byte[] output = {1};
		final int expectedLength = 1;
		final int expectedPosition = 1;
		
		final LimitedByteArrayWriter writer = new LimitedByteArrayWriter(input, maxAllowedSize);
		writer.write(write);
		
		checkOutput(expectedLength, expectedPosition, output, writer);
	}
	
	@Test
	public void testWriteByte_1More() throws IOException {
		/* in */
		final byte[] input = {};
		final byte maxAllowedSize = 1;
		/* do */
		final byte write = 1;
		/* out */
		final byte[] output = {1};
		final int expectedLength = 1;
		final int expectedPosition = 1;
		
		final LimitedByteArrayWriter writer = new LimitedByteArrayWriter(input, maxAllowedSize);
		writer.write(write);
		
		checkOutput(expectedLength, expectedPosition, output, writer);
	}

	
	@Test
	public void testSeek_BeforeStart() throws IOException {
		/* in */
		final byte[] input = {0};
		final byte maxAllowedSize = 1;
		/* do */
		final int seek = -1;
		
		final LimitedByteArrayWriter writer = new LimitedByteArrayWriter(input, maxAllowedSize);
		
		expectedException.expect(IndexOutOfBoundsException.class);
		writer.seek(seek);
	}
	
	@Test
	public void testSeek_AfterStart() throws IOException {
		/* in */
		final byte[] input = {0, 1, 2};
		final byte maxAllowedSize = 3;
		/* do */
		final int seek = 1;
		/* out */
		final byte[] output = {0, 1, 2};
		final int expectedLength = 3;
		final int expectedPosition = 1;
		
		final LimitedByteArrayWriter writer = new LimitedByteArrayWriter(input, maxAllowedSize);
		writer.seek(seek);
		checkOutput(expectedLength, expectedPosition, output, writer);
		
	}
	
	@Test
	public void testSeek_BeforeEnd() throws IOException {
		/* in */
		final byte[] input = {0, 1, 2};
		final byte maxAllowedSize = 3;
		/* do */
		final int seek = 2;
		/* out */
		final byte[] output = {0, 1, 2};
		final int expectedLength = 3;
		final int expectedPosition = 2;
		
		final LimitedByteArrayWriter writer = new LimitedByteArrayWriter(input, maxAllowedSize);
		writer.seek(seek);
		checkOutput(expectedLength, expectedPosition, output, writer);
	}
	
	@Test
	public void testSeek_AfterEnd() throws IOException {
		/* in */
		final byte[] input = {0, 1, 2};
		final byte maxAllowedSize = 5;
		/* do */
		final int seek = 4;
		/* out */
		final byte[] output = {0, 1, 2};
		final int expectedLength = 3;
		final int expectedPosition = 4;
		
		final LimitedByteArrayWriter writer = new LimitedByteArrayWriter(input, maxAllowedSize);
		writer.seek(seek);
		checkOutput(expectedLength, expectedPosition, output, writer);
	}
	
	@Test
	public void testSeekAndWrite_AfterEnd() throws IOException {
		/* in */
		final byte[] input = {0, 1, 2};
		final byte maxAllowedSize = 5;
		/* do */
		final int seek = 4;
		final byte write = 5;
		/* out */
		final byte[] output = {0, 1, 2, 0, 5};
		final int expectedLength = 5;
		final int expectedPosition = 5;
		
		final LimitedByteArrayWriter writer = new LimitedByteArrayWriter(input, maxAllowedSize);
		writer.seek(seek);
		writer.write(write);
		checkOutput(expectedLength, expectedPosition, output, writer);
	}
	
//	@Test
//	public void testWriteByteArrayIntInt() {
//		fail("Not yet implemented");
//	}

	private void checkOutput(final int expectedLength,
			final int expectedPosition, final byte[] output,
			final LimitedByteArrayWriter writer) {
		assertThat(writer.getBytes(), is(output));
		assertThat(writer.length(), is(expectedLength));
		assertThat(writer.position, is(expectedPosition));
	}
}

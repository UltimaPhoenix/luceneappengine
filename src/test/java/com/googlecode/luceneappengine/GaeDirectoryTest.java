package com.googlecode.luceneappengine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.zip.CRC32;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class GaeDirectoryTest extends LocalDatastoreTest {

    private final Analyzer analyzer = new SimpleAnalyzer(Version.LUCENE_48);
	private final IndexWriterConfig config = GaeLuceneUtil.getIndexWriterConfig(Version.LUCENE_48, analyzer);
    

    @Test
    public void writeAndReadStringInSegment() throws IOException {
        final String input = "Hello World!";
        final String name = "_0.fmt";
        
		final byte[] bs = input.getBytes();

        final Directory directory = new GaeDirectory();
        
		final IndexOutput createOutput = directory.createOutput(name, new IOContext());
		createOutput.writeBytes(bs, 0, bs.length);
        createOutput.close();
        
        final IndexInput openInput = directory.openInput(name, new IOContext());
        byte[] bt = new byte[bs.length];
        openInput.readBytes(bt, 0, bt.length);

        
        assertEquals(new String(bt), input);
        openInput.close();
        directory.close();
    }
    
    @Test
    public void writeAndReadStringInSegment_1Mb() throws IOException {
        final int size = 1024*1024 + 1;
        final String input = RandomStringUtils.random(size + 100, true, true);
        final String name = "_0.fmt";
        
		final byte[] bs = input.getBytes();
		
		assertTrue("Test is not ok! " + bs.length + " <= " + size, bs.length > size);//precondition
		
        final Directory directory = new GaeDirectory();
        
		final IndexOutput createOutput = directory.createOutput(name, new IOContext());
		createOutput.writeBytes(bs, 0, bs.length);
        createOutput.close();
        
        final IndexInput openInput = directory.openInput(name, new IOContext());
        byte[] bt = new byte[bs.length];
        openInput.readBytes(bt, 0, bt.length);

        
        assertEquals(new String(bt), input);
        openInput.close();
        directory.close();
    }
    
    @Test
    public void writeAndReadDocumentInDirectory() throws IOException {
        final String input = "Hello World!";
        
        final Directory directory = new GaeDirectory();
        
        final IndexWriter writer = new IndexWriter(directory, config);
        
        final Document document = new Document();
        document.add(new Field("title", input, TextField.TYPE_STORED));
        
        writer.addDocument(document);
        writer.close();
        
        final IndexReader reader = DirectoryReader.open(directory);
        Document doc = reader.document(0); 
        
        assertEquals(doc.get("title"), input);
        reader.close();
        directory.close();
    }
    
    @Test
    public void testCRC_1SegmentHunk() throws IOException {
        final String input = "Hello World!";
        final String name = "_0.fmt";
        
        final byte[] bs = input.getBytes();

        final Directory directory = new GaeDirectory();
        
        final IndexOutput createOutput = directory.createOutput(name, new IOContext());
        createOutput.writeBytes(bs, 0, bs.length);
        
        CRC32 expected = new CRC32();
        expected.update(bs, 0, bs.length);
        assertEquals(expected.getValue(), createOutput.getChecksum());
        
        createOutput.close();
        
        directory.close();
    }
    
    
    @Test
    @SuppressWarnings("deprecation")
    public void testCRC_SegmentSeekMoreSegment() throws IOException {
        final String name = "_0.fmt";
        
        final byte[] bs = Arrays.copyOf(new byte[0], 1000*1000*3);
        final int noZeroByteIndex = bs.length - 1;
        bs[noZeroByteIndex] = 5;

        final Directory directory = new GaeDirectory();
        
        final IndexOutput createOutput = directory.createOutput(name, new IOContext());
        createOutput.seek(noZeroByteIndex);
        createOutput.writeByte(bs[noZeroByteIndex]);
        
        CRC32 expected = new CRC32();
        expected.update(bs, 0, bs.length);
        assertEquals(expected.getValue(), createOutput.getChecksum());
        
        createOutput.close();
        
        directory.close();
    }
    
    @Test
    @SuppressWarnings("deprecation")
    public void testCRC_SegmentSeekMoreSegmentNoWrite() throws IOException {
        final String name = "_0.fmt";
        
        final byte[] bs = Arrays.copyOf(new byte[0], 1000*1000*3);
        final int noZeroByteIndex = bs.length - 1;
//        bs[noZeroByteIndex] = 0;

        final Directory directory = new GaeDirectory();
        
        final IndexOutput createOutput = directory.createOutput(name, new IOContext());
        createOutput.seek(noZeroByteIndex);
//        createOutput.writeByte(bs[noZeroByteIndex]);
        
        CRC32 expected = new CRC32();
        expected.update(bs, 0, noZeroByteIndex);
        assertEquals(expected.getValue(), createOutput.getChecksum());
        
        createOutput.close();
        
        directory.close();
    }
    
    @Test
    @SuppressWarnings("deprecation")
    public void testCRC_SegmentSeekOneSegment() throws IOException {
        final String name = "_0.fmt";
        
        final byte[] bs = Arrays.copyOf(new byte[0], 1000);
        final int noZeroByteIndex = bs.length - 1;
        bs[noZeroByteIndex] = 5;
        
        final Directory directory = new GaeDirectory();
        
        final IndexOutput createOutput = directory.createOutput(name, new IOContext());
        createOutput.seek(noZeroByteIndex);
        createOutput.writeByte(bs[noZeroByteIndex]);
        
        CRC32 expected = new CRC32();
        expected.update(bs, 0, bs.length);
        assertEquals(createOutput.getChecksum(), expected.getValue());
        
        createOutput.close();
        
        directory.close();
    }
    
    @Test
    public void testCRC_2SegmentHunk() throws IOException {
        final int size = 1024*1024 + 1;
        final String input = RandomStringUtils.random(size + 100, true, true);
        final String name = "_0.fmt";
        
        final byte[] bs = input.getBytes();
        
        assertTrue("Test is not ok! " + bs.length + " <= " + size, bs.length > size);//precondition
        
        final Directory directory = new GaeDirectory();
        
        final IndexOutput createOutput = directory.createOutput(name, new IOContext());
        createOutput.writeBytes(bs, 0, bs.length / 2);
        
        
        CRC32 expected = new CRC32();
        expected = new CRC32();
        expected.update(bs, 0, bs.length / 2);
        assertEquals(expected.getValue(), createOutput.getChecksum());
        
        System.out.println(bs.length);
        createOutput.writeBytes(bs, bs.length / 2, bs.length - bs.length / 2);
        
        expected.update(bs, bs.length / 2, bs.length - bs.length / 2);
        createOutput.close();
        assertEquals(expected.getValue(), createOutput.getChecksum());

        
        directory.close();
    }
    
    @Test
    public void testCRC_2SegmentHunk_Odd() throws IOException {
        final int size = 1024*1024 + 1;
        final String input = RandomStringUtils.random(size + 100, true, true);
        final String name = "_0.fmt";
        
        final byte[] bs = Arrays.copyOf(input.getBytes(), input.getBytes().length + 3);
        
        assertTrue("Test is not ok! " + bs.length + " <= " + size, bs.length > size);//precondition
        
        final Directory directory = new GaeDirectory();
        
        final IndexOutput createOutput = directory.createOutput(name, new IOContext());
        createOutput.writeBytes(bs, 0, bs.length / 2);
        
        
        CRC32 expected = new CRC32();
        expected = new CRC32();
        expected.update(bs, 0, bs.length / 2);
        assertEquals(expected.getValue(), createOutput.getChecksum());
        
        System.out.println(bs.length);
        createOutput.writeBytes(bs, bs.length / 2, bs.length - bs.length / 2 );
        
        expected.update(bs, bs.length / 2, bs.length - bs.length / 2);
        createOutput.close();
        assertEquals(expected.getValue(), createOutput.getChecksum());
        
        
        directory.close();
    }

    
    
}

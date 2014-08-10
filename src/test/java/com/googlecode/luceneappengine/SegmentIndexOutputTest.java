package com.googlecode.luceneappengine;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.store.BufferedChecksumIndexInput;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IndexOutput;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.fail;

public class SegmentIndexOutputTest extends LocalDatastoreTest {
    
    @Test
    public void testGetChecksum_NoFlush() throws IOException {
        final String input1 = "Hello World!";
        final String input2 = "Hello World second string!";
        final String segmentName = "segments.gen";//main segment name
        
        try (Directory directory = new GaeDirectory()) {
            try (IndexOutput createOutput = directory.createOutput(segmentName, null)) {
                createOutput.writeBytes(input1.getBytes(), 0, input1.getBytes().length);
                CodecUtil.writeFooter(createOutput);
            }
            
            try (IndexOutput createOutput = directory.createOutput(segmentName, null)) {
                createOutput.writeBytes(input2.getBytes(), 0, input2.getBytes().length);
                createOutput.writeBytes(input2.getBytes(), 0, input2.getBytes().length);
                CodecUtil.writeFooter(createOutput);
            }
            
            try (BufferedChecksumIndexInput bcii = new BufferedChecksumIndexInput(directory.openInput(segmentName, null))) {
                bcii.readBytes(new byte[input2.getBytes().length], 0, input2.getBytes().length);
                bcii.readBytes(new byte[input2.getBytes().length], 0, input2.getBytes().length);
                try {
                    CodecUtil.checkFooter(bcii);
                } catch (CorruptIndexException e) {
                    fail(e.getMessage());
                }
            }
        }
    }
    
    @Test
    @SuppressWarnings("deprecation")
    public void testGetChecksum_Flush() throws IOException {
        final String input1 = "Hello World!";
        final String input2 = "Hello World second string!";
        final String segmentName = "segments.gen";//main segment name
        
        try (Directory directory = new GaeDirectory()) {
            try (IndexOutput createOutput = directory.createOutput(segmentName, null)) {
                createOutput.writeBytes(input1.getBytes(), 0, input1.getBytes().length);
                createOutput.flush();
                CodecUtil.writeFooter(createOutput);
                createOutput.flush();
            }
            
            try (IndexOutput createOutput = directory.createOutput(segmentName, null)) {
                createOutput.writeBytes(input2.getBytes(), 0, input2.getBytes().length);
                createOutput.flush();
                createOutput.writeBytes(input2.getBytes(), 0, input2.getBytes().length);
                createOutput.flush();
                CodecUtil.writeFooter(createOutput);
                createOutput.flush();
            }
            
            try (BufferedChecksumIndexInput bcii = new BufferedChecksumIndexInput(directory.openInput(segmentName, null))) {
                bcii.readBytes(new byte[input2.getBytes().length], 0, input2.getBytes().length);
                bcii.readBytes(new byte[input2.getBytes().length], 0, input2.getBytes().length);
                try {
                    CodecUtil.checkFooter(bcii);
                } catch (CorruptIndexException e) {
                    fail(e.getMessage());
                }
            }
        }
    }
        
}

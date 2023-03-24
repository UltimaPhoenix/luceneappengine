package com.googlecode.luceneappengine.model;

public sealed interface FireStoreEntity permits GaeLock, LuceneIndex, Segment, SegmentHunk {
}

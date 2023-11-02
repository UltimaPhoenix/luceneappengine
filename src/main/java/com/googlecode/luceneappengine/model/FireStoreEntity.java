package com.googlecode.luceneappengine.model;

/**
 * Sealed interface that collects every Firestore serialized entity.
 */
public sealed interface FireStoreEntity permits GaeLock, LuceneIndex, Segment, SegmentHunk {
}

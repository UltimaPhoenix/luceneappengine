package com.googlecode.luceneappengine.model.repository;

import com.google.cloud.firestore.Firestore;
import com.googlecode.luceneappengine.firestore.FirestoreCollectionMapper;
import com.googlecode.luceneappengine.model.LuceneIndex;

public class LaeContext {

//    public static final String NAMESPACE_LAE = "LAE";

    public final Firestore firestore;

    public final LuceneIndexRepository luceneIndexRepository;
//    public final GaeLockRepository gaeLockRepository;
//    public final SegmentRepository segmentRepository;
//    public final SegmentHunkRepository segmentHunkRepository;

    public final FirestoreCollectionMapper firestoreCollectionMapper;

    public LaeContext(Firestore firestore) {
        this.firestore = firestore;
        this.firestoreCollectionMapper = new FirestoreCollectionMapper();
        this.luceneIndexRepository = new LuceneIndexRepository(firestore, firestoreCollectionMapper);
//        this.gaeLockRepository = new GaeLockRepository(firestore, firestoreCollectionMapper);
//        this.segmentRepository = new SegmentRepository(firestore, firestoreCollectionMapper);
//        this.segmentHunkRepository = new SegmentHunkRepository(firestore, firestoreCollectionMapper);
    }

}

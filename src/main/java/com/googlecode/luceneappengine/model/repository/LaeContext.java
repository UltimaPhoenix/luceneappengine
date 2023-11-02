package com.googlecode.luceneappengine.model.repository;

import com.google.cloud.firestore.Firestore;
import com.googlecode.luceneappengine.firestore.FirestoreCollectionMapper;

/**
 * A context to bind services.
 */
public class LaeContext {

//    public static final String NAMESPACE_LAE = "LAE";
    /**
     * The firestore reference.
     */
    public final Firestore firestore;

    /**
     * The lucene index repository.
     */
    public final LuceneIndexRepository luceneIndexRepository;

    /**
     * The collection mapper.
     */
    public final FirestoreCollectionMapper firestoreCollectionMapper;

    /**
     * Create a context for a given firestore.
     * @param firestore the firestore
     */
    public LaeContext(Firestore firestore) {
        this.firestore = firestore;
        this.firestoreCollectionMapper = new FirestoreCollectionMapper();
        this.luceneIndexRepository = new LuceneIndexRepository(firestore, firestoreCollectionMapper);
    }

}

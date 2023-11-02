package com.googlecode.luceneappengine.firestore;

import com.google.common.collect.ImmutableMap;
import com.googlecode.luceneappengine.model.*;

/**
 * A class that maps the collections in firestore.
 */
public class FirestoreCollectionMapper {

    private static final ImmutableMap<Class<? extends FireStoreEntity>, String> collectionNames = ImmutableMap.of(
            LuceneIndex.class, "lucene_indexes",
            GaeLock.class, "locks",
            Segment.class, "segments",
            SegmentHunk.class, "hunks"
    );

    /**
     * Return the firestore collection given the class.
     * @param clazz the class
     * @return the firestore collection name
     * @param <EI> the classtype
     */
    public <EI extends FireStoreEntity> String getCollectionName(Class<EI> clazz) {
        return collectionNames.getOrDefault(clazz, clazz.getSimpleName());
    }


}

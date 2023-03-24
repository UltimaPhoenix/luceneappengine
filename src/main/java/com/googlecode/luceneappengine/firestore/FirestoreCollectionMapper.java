package com.googlecode.luceneappengine.firestore;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.common.collect.ImmutableMap;
import com.googlecode.luceneappengine.model.*;

public class FirestoreCollectionMapper {

    Firestore firestore;

    private static final ImmutableMap<Class<? extends FireStoreEntity>, String> collectionNames = ImmutableMap.of(
            LuceneIndex.class, "lucene_indexes",
            GaeLock.class, "locks",
            Segment.class, "segments",
            SegmentHunk.class, "hunks"
    );

    public <EI extends FireStoreEntity> String getCollectionName(Class<EI> clazz) {
        return collectionNames.getOrDefault(clazz, clazz.getSimpleName());
    }

    public <EI extends FireStoreEntity> CollectionReference getCollectionReference(EI entity) {
        return switch (entity) {
            case LuceneIndex e -> firestore.collection(getCollectionName(LuceneIndex.class));
//            firestore.document(e.parentCollection.getPath()).collection(getCollectionName(GaeLock.class))
            case GaeLock e -> null;
            case Segment e -> null;
            case SegmentHunk e -> null;
        };
    }


}

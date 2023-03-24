package com.googlecode.luceneappengine.model.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.common.collect.ImmutableMap;
import com.googlecode.luceneappengine.firestore.BaseFirestoreRepository;
import com.googlecode.luceneappengine.firestore.FirestoreCollectionMapper;
import com.googlecode.luceneappengine.model.LuceneIndex;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class LuceneIndexRepository extends BaseFirestoreRepository<LuceneIndex> {

    public LuceneIndexRepository(Firestore firestore, FirestoreCollectionMapper datastoreEntityMapper) {
        super(firestore, LuceneIndex.class, datastoreEntityMapper);
    }

    public DocumentReference findRefById(String id) {
        return firestore.collection(collectionName).document(id);
    }

    public List<LuceneIndex> findAll() {
        return findAll(LuceneIndex.class);
    }

    public DocumentReference saveIndex(LuceneIndex entity) {
        try {
            DocumentReference reference = firestore.collection(collectionName).document(entity.getName());
            reference.create(entity).get();
            return reference;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

//    public List<LuceneIndex> findByName() {
//
//    }

    public LuceneIndex getOrCreate(String indexName) {
        final DocumentReference reference = firestore.collection(collectionName).document(indexName);
        LuceneIndex res;
        try {
            res = reference.get().get().toObject(LuceneIndex.class);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        if (res == null) {
            res = new LuceneIndex(indexName);
            try {
                reference.create(res).get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return res;
    }
}

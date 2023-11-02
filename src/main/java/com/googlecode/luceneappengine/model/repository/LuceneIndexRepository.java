package com.googlecode.luceneappengine.model.repository;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.googlecode.luceneappengine.firestore.BaseFirestoreRepository;
import com.googlecode.luceneappengine.firestore.FirestoreCollectionMapper;
import com.googlecode.luceneappengine.model.LuceneIndex;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A repository to manage CRUD on LuceneIndex.
 */
public class LuceneIndexRepository extends BaseFirestoreRepository<LuceneIndex> {

    /**
     * Create a Repository for LuceneIndex
     * @param firestore the firestore
     * @param datastoreEntityMapper the datastore entity mapper
     */
    public LuceneIndexRepository(Firestore firestore, FirestoreCollectionMapper datastoreEntityMapper) {
        super(firestore, LuceneIndex.class, datastoreEntityMapper);
    }

    /**
     * Return the firestore document reference.
     * @param id the id of the codument in the current index
     * @return The firestore document reference
     */
    public DocumentReference findRefById(String id) {
        return firestore.collection(collectionName).document(id);
    }

    /**
     * Returns the available indexs.
     * @return The indexes available
     */
    public List<LuceneIndex> findAll() {
        return findAll(LuceneIndex.class);
    }

    /**
     * Save the index in the datastore.
     * @param entity The index to be saved
     * @return The document reference in firestore
     */
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

    /**
     * Return and index of a name.
     * @param indexName The name of the index
     * @return a specific index
     */
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

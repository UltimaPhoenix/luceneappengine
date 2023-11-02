package com.googlecode.luceneappengine.firestore;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.googlecode.luceneappengine.model.FireStoreEntity;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Base Firestore repository.
 * @param <EI> Class to be created
 */
public abstract class BaseFirestoreRepository<EI extends FireStoreEntity> {
    /**
     * Firestore
     */
    protected final Firestore firestore;
    /**
     * Firestore collection mapper.
     */
    protected final FirestoreCollectionMapper firestoreCollectionMapper;
    /**
     * Repository class
     */
    protected final Class<EI> clazz;
    /**
     * Firestore collection.
     */
    protected final String collectionName;

    /**
     * Base constructor.
     * @param firestore The firestore
     * @param clazz the reference class
     * @param firestoreCollectionMapper the firestore collection mapper
     */
    protected BaseFirestoreRepository(Firestore firestore, Class<EI> clazz, FirestoreCollectionMapper firestoreCollectionMapper) {
        this.firestore = firestore;
        this.clazz = clazz;
        this.firestoreCollectionMapper = firestoreCollectionMapper;
        this.collectionName = firestoreCollectionMapper.getCollectionName(clazz);
    }

    /**
     * Base method to return all elements of a class.
     * @param clazz the refenrece class
     * @return the list of elements
     * @param <E> the class type.
     */
    protected <E extends EI> List<E> findAll(Class<E> clazz) {
        CollectionReference collection = firestore.collection(collectionName);
        try {
            return runInTransaction(t -> {
                Iterable<DocumentReference> documentReferences = collection.listDocuments();
                return StreamSupport.stream(documentReferences.spliterator(), false)
                        .map(documentReference -> {
                            try {
                                return documentReference.get().get().toObject(clazz);
                            } catch (InterruptedException|ExecutionException e) {
                                Thread.currentThread().interrupt();
                                throw new RuntimeException(e);
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * An utility method to run transactions
     * @param updateFunction the transaction to be executed
     * @return A future
     * @param <T> the return type
     */
    public <T> ApiFuture<T> runInTransaction(@Nonnull final Transaction.Function<T> updateFunction) {
        return firestore.runTransaction(updateFunction);
    }

}

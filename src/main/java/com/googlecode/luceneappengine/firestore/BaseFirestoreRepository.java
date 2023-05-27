package com.googlecode.luceneappengine.firestore;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.googlecode.luceneappengine.model.FireStoreEntity;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class BaseFirestoreRepository<EI extends FireStoreEntity> {

    protected final Firestore firestore;
    protected final FirestoreCollectionMapper firestoreCollectionMapper;
    protected final Class<EI> clazz;

    protected final String collectionName;

    protected BaseFirestoreRepository(Firestore firestore, Class<EI> clazz, FirestoreCollectionMapper firestoreCollectionMapper) {
        this.firestore = firestore;
        this.clazz = clazz;
        this.firestoreCollectionMapper = firestoreCollectionMapper;
        this.collectionName = firestoreCollectionMapper.getCollectionName(clazz);
    }
//
//
//    protected DocumentReference findRefById(String id) {
//        return firestore.collection(collectionName).document(id);
//    }
//
    protected <E extends EI> List<E> findAll(Class<E> clazz) {
        CollectionReference collection = firestore.collection(collectionName);
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
                .collect(Collectors.toList());
    }

//    protected EI findById(String id) {
//        try {
//            return firestore.collection(collectionName).document(id).get().get().toObject(clazz);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            throw new RuntimeException(e);
//        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//
//
//    public void deleteById(String id) {
//        try {
//            firestore.collection(collectionName).document(id).delete().get();
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            throw new RuntimeException(e);
//        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//
//    public String save(EI entity) {
//        try {
//            return firestore.collection(collectionName).add(entity).get().getId();
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            throw new RuntimeException(e);
//        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
    public <T> ApiFuture<T> runInTransaction(@Nonnull final Transaction.Function<T> updateFunction) {
        return firestore.runTransaction(updateFunction);
    }
//
//    public <T> ApiFuture<T> runInTransaction(@Nonnull final Transaction.Function<T> updateFunction, @Nonnull TransactionOptions transactionOptions) {
//        return firestore.runTransaction(updateFunction, transactionOptions);
//    }

}

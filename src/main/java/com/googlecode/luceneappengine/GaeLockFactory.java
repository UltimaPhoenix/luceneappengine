package com.googlecode.luceneappengine;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.cloud.firestore.DocumentReference;
import com.googlecode.luceneappengine.model.GaeLock;
import com.googlecode.luceneappengine.model.LuceneIndex;
import com.googlecode.luceneappengine.model.repository.LaeContext;
import org.apache.lucene.store.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class used as {@link LockFactory} for any {@link GaeDirectory}.
 * 
 * @author Fabio Grucci (github: <i>UltimaPhoenix</i>, bitbucket:
 *         <i>Dark_Phoenix</i>, googlecode: <i>fabio.grucci</i>)
 *
 */
final class GaeLockFactory extends LockFactory {

	private static final Logger log = LoggerFactory.getLogger(GaeLockFactory.class);

	private final LaeContext laeContext;

	public GaeLockFactory(LaeContext laeContext) {
		this.laeContext = laeContext;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lucene.store.LockFactory#makeLock(java.lang.String)
	 */
	@Override
	public Lock obtainLock(final Directory dir, final String lockName) throws LockObtainFailedException, IOException {
		if (!(dir instanceof GaeDirectory)) {
	      throw new UnsupportedOperationException(getClass().getSimpleName() + " can only be used with GaeDirectory subclasses, got: " + dir);
	    }
		final LuceneIndex index = ((GaeDirectory) dir).index;
		final DocumentReference lockRef = laeContext.firestore
				.collection(laeContext.firestoreCollectionMapper.getCollectionName(LuceneIndex.class))
				.document(index.getName())
				.collection(laeContext.firestoreCollectionMapper.getCollectionName(GaeLock.class))
				.document(lockName);
		boolean obtainedLock;
		try {
			obtainedLock = laeContext.firestore.runTransaction(t -> {
				final boolean obtained;
				GaeLock gaeLock = t.get(lockRef).get().toObject(GaeLock.class);
				if (gaeLock == null) {
					log.trace("Creating new Lock '{}.{}'.", index, lockName);
					gaeLock = new GaeLock(index, lockName);
				}
				if (gaeLock.locked) {
					log.debug("Cannot lock Lock '{}.{}'.", index, lockName);
					obtained = false;
				} else {
					log.debug("Locking Lock '{}.{}'.", index, lockName);
					gaeLock.locked = true;
					t.set(lockRef, gaeLock);
					obtained = true;
				}
				return obtained;
			}).get();
		} catch (RuntimeException | ExecutionException e) {
			log.error("Error obtaining lock:{} error:{}", lockName, e.getMessage(), e);
			throw new LockObtainFailedException("Error obtaining lock:" + lockName, e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new LockObtainFailedException("Error obtaining lock:" + lockName, e);
		}
		if (!obtainedLock) {
			throw new LockObtainFailedException("Cannot lock Lock " + index.getName() + "." + lockName);
		}

		return new Lock() {
			boolean closed;
			/*
			 * (non-Javadoc)
			 *
			 * @see org.apache.lucene.store.Lock#close()
			 */
			@Override
			public void close() throws LockReleaseFailedException {
				try {
					laeContext.firestore.runTransaction(t -> {
						final GaeLock gaeLock = t.get(lockRef).get().toObject(GaeLock.class);
						if (gaeLock != null && gaeLock.locked) {
							log.debug("Unlocking Lock '{}'.", lockName);
							gaeLock.locked = false;
							t.update(lockRef, "locked", false);
						} else {
							log.warn("Trying to release a non locked Lock '{}'.", lockName);
						}
						return null;
					}).get();
				} catch (ExecutionException | RuntimeException e) {
					log.error("Error closing lock:{} error:{}", lockName, e.getMessage(), e);
					throw new LockReleaseFailedException("Error closing lock:" + lockName, e);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					throw new LockReleaseFailedException("Error closing lock:" + lockName, e);
				} finally {
					closed = true;
				}
			}
			/*
			 * (non-Javadoc)
			 * @see org.apache.lucene.store.Lock#ensureValid()
			 */
			@Override
			public void ensureValid() throws IOException, AlreadyClosedException {
				/*
				 * Always valid. BTW can be checked if the lock still exists,
				 * but it's guaranteed by Objectify transactions, so nothing to do here.
				 */
				if (closed) {
					throw new AlreadyClosedException("Lock " + index.getName() + "." + lockName + " already closed");
				}
			}
		};
	}

	/**
	 * Method that return every lock created by this {@link GaeLockFactory}.
	 * 
	 * @return The list of {@link GaeLock} created by this
	 *         {@link GaeLockFactory}
	 */
	List<GaeLock> getLocks(GaeDirectory gaeDirectory) throws ExecutionException, InterruptedException {
		return laeContext.firestore
				.collection(laeContext.firestoreCollectionMapper.getCollectionName(LuceneIndex.class))
				.document(gaeDirectory.index.getName())
				.collection(laeContext.firestoreCollectionMapper.getCollectionName(GaeLock.class))
				.get().get().toObjects(GaeLock.class);
	}


}
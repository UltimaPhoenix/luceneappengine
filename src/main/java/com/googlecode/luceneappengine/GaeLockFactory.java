package com.googlecode.luceneappengine;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockFactory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.LockReleaseFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;

/**
 * Class used as {@link LockFactory} for any {@link GaeDirectory}.
 * 
 * @author Fabio Grucci (github: <i>UltimaPhoenix</i>, bitbucket:
 *         <i>Dark_Phoenix</i>, googlecode: <i>fabio.grucci</i>)
 *
 */
final class GaeLockFactory extends LockFactory {

	private static final Logger log = LoggerFactory.getLogger(GaeLockFactory.class);

	private GaeLockFactory() {/* singleton */}

	/**
	 * Return a new {@link GaeLockFactory} specific for the index.
	 * 
	 * @param indexKey
	 * @return A {@link GaeLockFactory} for this index
	 */
	public static GaeLockFactory getInstance() {
		return GaeLockFactoryHolder.INSTANCE;
	}
	
	private static class GaeLockFactoryHolder {
		private static final GaeLockFactory INSTANCE = new GaeLockFactory();
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
		final Key<LuceneIndex> indexKey = ((GaeDirectory) dir).indexKey;
		boolean obtainedLock;
		try {
			obtainedLock = ofy().transactNew(new Work<Boolean>() {
				@Override
				public Boolean run() {
					final boolean obtained;
					GaeLock gaeLock = ofy().load().key(Key.create(indexKey, GaeLock.class, lockName)).now();
					if (gaeLock == null) {
						log.trace("Creating new Lock '{}.{}'.", indexKey, lockName);
						gaeLock = new GaeLock(indexKey, lockName);
					}
					if (gaeLock.locked) {
						log.debug("Cannot lock Lock '{}.{}'.", indexKey, lockName);
						obtained = false;
					} else {
						log.debug("Locking Lock '{}.{}'.", indexKey, lockName);
						gaeLock.locked = true;
						ofy().save().entity(gaeLock).now();
						obtained = true;
					}
					return obtained;
				}
			});
		} catch (RuntimeException e) {
			log.error("Error obtaining lock:{} error:{}", lockName, e.getMessage(), e);
			throw new LockObtainFailedException("Error obtaining lock:" + lockName, e);
		}
		if (!obtainedLock) {
			throw new LockObtainFailedException("Cannot lock Lock " + indexKey + "." + lockName);
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
					ofy().transactNew(3, new Work<Void>() {
						@Override
						public Void run() {
							final GaeLock gaeLock = ofy().load().key(Key.create(indexKey, GaeLock.class, lockName)).now();
							if (gaeLock != null && gaeLock.locked) {
								log.debug("Unlocking Lock '{}'.", lockName);
								gaeLock.locked = false;
								ofy().save().entity(gaeLock).now();
							} else {
								log.warn("Tryng to release a non locked Lock '{}'.", lockName);
							}
							return null;
						}
					});
				} catch (RuntimeException e) {
					log.error("Error closing lock:{} error:{}", lockName, e.getMessage(), e);
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
					throw new AlreadyClosedException("Lock " + indexKey + "." + lockName + " already closed");
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
	List<GaeLock> getLocks(GaeDirectory gaeDirectory) {
		return ofy().load().type(GaeLock.class).ancestor(gaeDirectory.indexKey).list();
	}


}
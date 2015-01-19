package com.googlecode.luceneappengine;

import java.io.IOException;
import java.util.List;

import com.google.appengine.api.datastore.Key;
import com.textquo.twist.object.KeyStructure;
import com.textquo.twist.types.Function;
import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.textquo.twist.ObjectStoreService.store;

/**
 * Class used as {@link LockFactory} for any {@link GaeDirectory}.
 * 
 * @author Fabio Grucci (github: <i>UltimaPhoenix</i>, bitbucket:
 *         <i>Dark_Phoenix</i>, googlecode: <i>fabio.grucci</i>)
 *
 */
final class GaeLockFactory extends LockFactory {

	private static final Logger log = LoggerFactory.getLogger(GaeLockFactory.class);

	private final Key indexKey;

	private GaeLockFactory(Key indexKey) {
		this.indexKey = indexKey;
	}

	/**
	 * Return a new {@link GaeLockFactory} specific for the index.
	 * 
	 * @param indexKey
	 * @return A {@link GaeLockFactory} for this index
	 */
	public static GaeLockFactory getInstance(Key indexKey) {
		return new GaeLockFactory(indexKey);
	}

	/**
	 * Method that return every lock created by this {@link GaeLockFactory}.
	 * 
	 * @return The list of {@link GaeLock} created by this
	 *         {@link GaeLockFactory}
	 */
	List<GaeLock> getLocks() {
		// Ancestor query
		return store().find(GaeLock.class, indexKey).asList().getList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lucene.store.LockFactory#makeLock(java.lang.String)
	 */
	@Override
	public Lock makeLock(final String lockName) {
		return new Lock() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.apache.lucene.store.Lock#close()
			 */
			@Override
			public void close() throws IOException {
				try {
					store().transact(new Function<Object>() {
						@Override
						public Object execute() {
							final GaeLock gaeLock
									= store().get(GaeLock.class, KeyStructure.createKey(indexKey, GaeLock.class, lockName));
							if (gaeLock != null && gaeLock.locked) {
								log.debug("Unlocking Lock '{}'.", lockName);
								gaeLock.locked = false;
								store().put(gaeLock);
							} else {
								log.warn("Tryng to release a non locked Lock '{}'.", lockName);
							}
							return null;
						}
					});
				} catch (RuntimeException e) {
					log.error("Error closing lock:{} error:{}", lockName, e.getMessage(), e);
					throw new IOException("Error closing lock:" + lockName, e);
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.apache.lucene.store.Lock#obtain()
			 */
			@Override
			public boolean obtain() throws IOException {
				try {
					return store().transact(new Function<Boolean>() {
						@Override
						public Boolean execute() {
							final boolean obtained;
							GaeLock gaeLock
									= store().get(GaeLock.class, KeyStructure.createKey(indexKey, GaeLock.class, lockName));
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
								store().put(gaeLock);
								obtained = true;
							}
							return obtained;						}
					});
				} catch (RuntimeException e) {
					log.error("Error obtaining lock:{} error:{}", lockName, e.getMessage(), e);
					throw new IOException("Error obtaining lock:" + lockName, e);
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.apache.lucene.store.Lock#isLocked()
			 */
			@Override
			public boolean isLocked() throws IOException {
				final GaeLock find = store()
						.get(GaeLock.class, KeyStructure.createKey(indexKey, GaeLock.class, lockName));
				return find != null && find.locked;
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lucene.store.LockFactory#clearLock(java.lang.String)
	 */
	@Override
	public void clearLock(String lockName) throws IOException {
		store().delete(KeyStructure.createKey(indexKey, GaeLock.class, lockName));
	}

}
package com.googlecode.luceneappengine;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockFactory;
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

	private final Key<LuceneIndex> indexKey;

	private GaeLockFactory(Key<LuceneIndex> indexKey) {
		this.indexKey = indexKey;
	}

	/**
	 * Return a new {@link GaeLockFactory} specific for the index.
	 * 
	 * @param indexKey
	 * @return A {@link GaeLockFactory} for this index
	 */
	public static GaeLockFactory getInstance(Key<LuceneIndex> indexKey) {
		return new GaeLockFactory(indexKey);
	}

	/**
	 * Method that return every lock created by this {@link GaeLockFactory}.
	 * 
	 * @return The list of {@link GaeLock} created by this
	 *         {@link GaeLockFactory}
	 */
	List<GaeLock> getLocks() {
		return ofy().load().type(GaeLock.class).ancestor(indexKey).list();
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
					return ofy().transactNew(new Work<Boolean>() {
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
				final GaeLock find = ofy().load().key(Key.create(indexKey, GaeLock.class, lockName)).now();
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
		ofy().delete().key(Key.create(GaeLock.class, lockName));
	}

}
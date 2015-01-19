package com.googlecode.luceneappengine;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.appengine.api.datastore.Key;
import com.textquo.twist.ObjectStore;
import com.textquo.twist.common.ObjectNotFoundException;
import com.textquo.twist.object.KeyStructure;
import com.textquo.twist.types.Function;
import org.apache.lucene.store.BaseDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.util.RamUsageEstimator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.luceneappengine.objectify.util.TwistBuilder;
import com.googlecode.luceneappengine.objectify.util.TwistUtil;

import static com.textquo.twist.ObjectStoreService.store;

/**
 * Lucene {@link Directory} working in google app engine (GAE) environment.
 * Using this {@link Directory} you can create multiple indexes, each one identified by
 * a name specified in constructor {@link GaeDirectory#GaeDirectory(String)}, 
 * for details read constructor documentation.
 * <b>In order to open an index writer is highly recommended the usage of configuration provided by 
 * {@link GaeLuceneUtil#getIndexWriterConfig(org.apache.lucene.util.Version, org.apache.lucene.analysis.Analyzer)}</b>.
 * <pre>
 * {@code
 * GaeDirectory directory = new GaeDirectory();
 * IndexWriterConfig config = GaeLuceneUtil.getIndexWriterConfig(Version.LUCENE_4_10_0, analyzer);
 * IndexWriter writer = new IndexWriter(directory, config);
 * }
 * </pre>
 * <i>
 * If your application throws {@link NoClassDefFoundError} while using {@link GaeDirectory} 
 * in order to make it work, into your GAE web application, the modified {@link RamUsageEstimator} 
 * (<a href="http://luceneappengine.googlecode.com/hg/src/main/java/org/apache/lucene/util/RamUsageEstimator.java">link to source</a>) 
 * in a package named <code>org.apache.lucene.util</code>.
 * </i>
 * 
 * @author Fabio Grucci (github: <i>UltimaPhoenix</i>, bitbucket: <i>Dark_Phoenix</i>, googlecode: <i>fabio.grucci</i>)
 * @see GaeLuceneUtil
 * @see RamUsageEstimator
 */
public class GaeDirectory extends BaseDirectory {

	private static final Logger log = LoggerFactory.getLogger(GaeDirectory.class);
	
	private static final String DEFAULT_GAE_LUCENE_INDEX_NAME = "defaultIndex";
	
	private final Key indexKey;
	
	static {
	}
	 
	/**
	 * Create a {@link GaeDirectory} with default name <code>"defaultIndex"</code>.
	 * Same as {@link GaeDirectory#GaeDirectory(String)} with <code>"defaultIndex"</code>.
	 */
	public GaeDirectory() {
		this(DEFAULT_GAE_LUCENE_INDEX_NAME);
	}
	/**
	 * Create a {@link GaeDirectory} with specified name, 
	 * <b>the name specified must be a legal {@link com.google.appengine.api.datastore.Key} name</b>.
	 * @param indexName The name of the index
	 */
	public GaeDirectory(String indexName) {
		if(indexName == null) {
			indexName = DEFAULT_GAE_LUCENE_INDEX_NAME;
		}
		this.indexKey = createIndexIfNotExist(indexName);
		try {
			setLockFactory(GaeLockFactory.getInstance(indexKey));
		} catch (IOException e) {
			// Cannot happen.
			log.error("Unhandled Exception please report this stack trace to code mantainer, " +
					"this index can be corrupted with concurrent indexing operations.", e);
		}
	}
	/**
	 * Create a {@link GaeDirectory} for existing index.
	 * @param luceneIndex The existing index
	 */
	public GaeDirectory(LuceneIndex luceneIndex) {
		this(luceneIndex.getName());
	}
	
	/**
	 * Returns every available indexes created into your GAE application.
	 * With {@link LuceneIndex} you can build {@link GaeDirectory} 
	 * using constructor {@link GaeDirectory#GaeDirectory(LuceneIndex)}.
	 * @return A list of available indexes
	 */
	public static List<LuceneIndex> getAvailableIndexes() {
		return store().find(LuceneIndex.class).asList().getList();
	}
	/**
	 * Delete this directory.
	 * @throws IOException If an error occurs
	 */
	public void delete() throws IOException {
		store().transact(new Function<Void>() {
			@Override
			public Void execute() {
				final ObjectStore objectStore = store();
				for(String name : listAll()){
					deleteSegment(objectStore, name);
				}
				objectStore.delete(indexKey);
				objectStore.delete(((GaeLockFactory) getLockFactory()).getLocks());
				return null;
			}
		});
	}
	/**
	 * Delete the segment specified.
	 * @param name The name of the segment
	 */
	protected void deleteSegment(final String name) {
		store().transact(new Function<Object>() {
			@Override
			public Object execute() {
				deleteSegment(store(), name);
				return null;
			}
		});
	}
	/**
	 * Delete the segment using the specified {@link ObjectStore} usefull for transaction.
	 * @param store The {@link ObjectStore} to use
	 * @param name The name of the segment to delete
	 */
	protected void deleteSegment(final ObjectStore store, final String name) {
		final Key segmentKey = newSegmentKey(name);
		
		final Segment segment = store.get(Segment.class, segmentKey);
		store.delete(segment.getHunkKeys(segmentKey));
		store.delete(segmentKey);
	}
	
	/**
	 * Method used for testing purpose useful for printing segment information.
	 * @param segment The segment to print
	 * @param name The name of the hunk to print
	 * @param index The index of the hunk to print
	 */
	protected void logSegment(Segment segment, String name, int index) {
		ObjectStore objectStore = store();
		final SegmentHunk hunk =  objectStore.get(SegmentHunk.class, newSegmentHunkKey(name, index));
		byte[] content = Arrays.copyOfRange(hunk.bytes, 0, (int) (hunk.bytes.length % (segment.length / hunk.id)));
		hunk.bytes = content;
		log.info("Hunk '{}-{}-{}' with length {}, Value={}", 
				indexKey.getName(), name, hunk.id, hunk.bytes.length, new String(hunk.bytes));
	}
	/*
	 * (non-Javadoc)
	 * @see org.apache.lucene.store.Directory#close()
	 */
	@Override
	public void close() throws IOException {
	    isOpen = false;
		//TODO: refactor for caching. Doh! There's something to do!
	}
	/*
	 * (non-Javadoc)
	 * @see org.apache.lucene.store.Directory#openInput(java.lang.String, org.apache.lucene.store.IOContext)
	 */
	@Override
	public IndexInput openInput(String name, IOContext context) throws IOException {
	    ensureOpen();
		try {
			return new SegmentIndexInput(store().get(Segment.class, newSegmentKey(name)));
		} catch (ObjectNotFoundException e) {
			throw new IOException(name, e);
		}
	}
	/*
	 * (non-Javadoc)
	 * @see org.apache.lucene.store.Directory#createOutput(java.lang.String, org.apache.lucene.store.IOContext)
	 */
	@Override
	public IndexOutput createOutput(String name, IOContext context) throws IOException {
	    ensureOpen();
		ObjectStore objectStore = store();
		Segment segment = objectStore.get(Segment.class, newSegmentKey(name));
		if(segment == null) {
			segment = newSegment(name);
		}
		return new SegmentIndexOutput(segment);
	}
	/*
	 * (non-Javadoc)
	 * @see org.apache.lucene.store.Directory#deleteFile(java.lang.String)
	 */
	@Override
	public void deleteFile(String name) throws IOException {
		ObjectStore objectStore = store();
		Segment segment = objectStore.get(Segment.class, newSegmentKey(name));
		
		final long hunkCount = segment.hunkCount;
		for (int i = 1; i <= hunkCount; i++) {
			objectStore.delete(newSegmentHunkKey(name, i));
		}
		objectStore.delete(newSegmentKey(name));
	}
	/*
	 * (non-Javadoc)
	 * @see org.apache.lucene.store.Directory#fileExists(java.lang.String)
	 */
	@Override
	public boolean fileExists(String name) throws IOException {
		final Segment bigTableIndexFile =  store().get(Segment.class, newSegmentKey(name));
		return bigTableIndexFile != null;
	}
	/*
	 * (non-Javadoc)
	 * @see org.apache.lucene.store.Directory#fileLength(java.lang.String)
	 */
	@Override
	public long fileLength(String name) throws IOException {
		final Segment bigTableIndexFile =  store().get(Segment.class, newSegmentKey(name));
		return bigTableIndexFile.length;
	}
	/*
	 * (non-Javadoc)
	 * @see org.apache.lucene.store.Directory#listAll()
	 */
	@Override
	public String[] listAll() {
		ObjectStore objectStore = store();
		final List<Key> keys = store().find(Segment.class, indexKey).keysOnly().asList().getList();
		String[] names = new String[keys.size()];
		int i = 0;
		for (Key name : keys)
			names[i++] = name.getName();
		return names;
	}
	/**
	 * Create a new segment with the specified name using the specified {@link com.textquo.twist.ObjectStore}.
	 * The {@link Segment} contains one empty {@link SegmentHunk}.
	 * @param name The name of the segment to create
	 * @return A new {@link Segment} with one {@link SegmentHunk} 
	 */
	protected Segment newSegment(String name) {
		Segment segment = new Segment(indexKey, name);
		SegmentHunk newHunk = segment.newHunk();//at least one segment
		segment.lastModified = System.currentTimeMillis();
		store().put(segment, newHunk);
		log.debug("Created segment '{}'.", name);
		return segment;
	}
	
	private Key newSegmentHunkKey(final String name, long count) {
		return KeyStructure.createKey(newSegmentKey(name), SegmentHunk.class, count);
	}
	private Key newSegmentKey(final String name) {
		return KeyStructure.createKey(indexKey, Segment.class, name);
	}
	private static Key createIndexIfNotExist(String indexName) {
		Key key = KeyStructure.createKey(LuceneIndex.class, indexName);
		TwistUtil.getOrCreate(LuceneIndex.class, key, new LuceneIndexBuilder());
		return key;
	}
	
	private static class LuceneIndexBuilder implements TwistBuilder<LuceneIndex> {
		@Override
		public LuceneIndex newIstance(Key key) {
			return new LuceneIndex(key.getName());
		}
	}

	@Override
	public void sync(Collection<String> names) throws IOException {
		//PendingFutures.completeAllPendingFutures();
	}
}

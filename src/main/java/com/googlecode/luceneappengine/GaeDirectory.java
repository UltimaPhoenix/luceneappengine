package com.googlecode.luceneappengine;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.StreamSupport;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.TransactionOptions;
import com.google.cloud.firestore.WriteBatch;
import com.googlecode.luceneappengine.model.GaeLock;
import com.googlecode.luceneappengine.model.LuceneIndex;
import com.googlecode.luceneappengine.model.Segment;
import com.googlecode.luceneappengine.model.SegmentHunk;
import com.googlecode.luceneappengine.model.repository.LaeContext;
import org.apache.lucene.store.BaseDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.util.RamUsageEstimator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static com.google.common.base.MoreObjects.*;

/**
 * Lucene {@link Directory} working in google app engine (GAE) environment.
 * Using this {@link Directory} you can create multiple indexes, each one identified by
 * a name specified in constructor {@link GaeDirectory#GaeDirectory(LaeContext, String)},
 * for details read constructor documentation.
 * <b>In order to open an index writer is highly recommended the usage of configuration provided by
 * {@link GaeLuceneUtil#getIndexWriterConfig(org.apache.lucene.analysis.Analyzer)}</b>.
 * <pre>
 * {@code
 * GaeDirectory directory = new GaeDirectory();
 * IndexWriterConfig config = GaeLuceneUtil.getIndexWriterConfig(Version.LATEST, analyzer);
 * IndexWriter writer = new IndexWriter(directory, config);
 * }
 * </pre>
 * <br >
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

	private final LaeContext laeContext;

	final LuceneIndex index;

	/**
	 * Create a {@link GaeDirectory} with default name <code>"defaultIndex"</code>.
	 * Same as {@link GaeDirectory#GaeDirectory(LaeContext, String)} with <code>"defaultIndex"</code>.
	 * @param laeContext the laecontext
	 */
	public GaeDirectory(LaeContext laeContext) {
		this(laeContext, DEFAULT_GAE_LUCENE_INDEX_NAME);
	}
	/**
	 * Create a {@link GaeDirectory} with specified name,
	 * <b>the name specified must be a legal {@link com.google.cloud.firestore.annotation.DocumentId} name</b>.
	 * @param laeContext The lae context
	 * @param indexName The name of the index
	 */
	public GaeDirectory(LaeContext laeContext, String indexName) {
		super(new GaeLockFactory(laeContext));
		this.laeContext = laeContext;
		this.index = laeContext.luceneIndexRepository.getOrCreate(firstNonNull(indexName, DEFAULT_GAE_LUCENE_INDEX_NAME));
	}
	/**
	 * Create a {@link GaeDirectory} for existing index.
	 * @param laeContext The laecontext
	 * @param luceneIndex The existing index
	 */
	public GaeDirectory(LaeContext laeContext, LuceneIndex luceneIndex) {
		this(laeContext, luceneIndex.getName());
	}

	/**
	 * Returns every available indexes created into your GAE application.
	 * With {@link LuceneIndex} you can build {@link GaeDirectory}
	 * using constructor {@link GaeDirectory#GaeDirectory(LaeContext, LuceneIndex)}.
	 * @param context The lae context
	 * @return A list of available indexes
	 */
	public static List<LuceneIndex> getAvailableIndexes(LaeContext context) {
		return context.luceneIndexRepository.findAll();
	}
	/**
	 * Delete this directory.
	 * @throws IOException If an error occurs
	 */
	public void delete() throws IOException {
		laeContext.luceneIndexRepository.runInTransaction(datastoreReaderWriter -> {
			Iterable<DocumentReference> allByIndex = segments()
					.listDocuments();
			allByIndex.forEach(segment -> segment.collection(laeContext.firestoreCollectionMapper.getCollectionName(SegmentHunk.class)).listDocuments().forEach(datastoreReaderWriter::delete));
			allByIndex.forEach(datastoreReaderWriter::delete);

			laeContext.firestore.collection(laeContext.firestoreCollectionMapper.getCollectionName(LuceneIndex.class))
					.document(index.getName())
					.collection(laeContext.firestoreCollectionMapper.getCollectionName(GaeLock.class))
					.listDocuments()
					.forEach(datastoreReaderWriter::delete);

			datastoreReaderWriter.delete(laeContext.luceneIndexRepository.findRefById(index.getName()));
			return null;
		});
	}

	private CollectionReference segments() {
		return laeContext.firestore
				.collection(laeContext.firestoreCollectionMapper.getCollectionName(LuceneIndex.class))
				.document(index.getName())
				.collection(laeContext.firestoreCollectionMapper.getCollectionName(Segment.class));
	}

//	/**
//	 * Method used for testing purpose useful for printing segment information.
//	 * @param segment The segment to print
//	 * @param name The name of the hunk to print
//	 * @param index The index of the hunk to print
//	 */
//	protected void logSegment(Segment segment, String name, int index) {
//		Objectify objectify = ofy();
//		final SegmentHunk hunk = objectify.load().key(newSegmentHunkKey(name, index)).now();
//		hunk.bytes = Arrays.copyOfRange(hunk.bytes, 0, (int) (hunk.bytes.length % (segment.length / hunk.id)));
//		log.info("Hunk '{}-{}-{}' with length {}, Value={}",
//				this.index.getName(), name, hunk.id, hunk.bytes.length, new String(hunk.bytes));
//	}
	/*
	 * (non-Javadoc)
	 * @see org.apache.lucene.store.Directory#close()
	 */
	@Override
	public void close() throws IOException {
	    isOpen = false;
		//TODO: refactor for caching. Doh! There's something to do!
	}

	@Override
	public Set<String> getPendingDeletions() {
		return Collections.emptySet();
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.lucene.store.Directory#openInput(java.lang.String, org.apache.lucene.store.IOContext)
	 */
	@Override
	public IndexInput openInput(String name, IOContext context) throws IOException {
	    ensureOpen();
		try {
			return new SegmentIndexInput(laeContext, index, name);
		} catch (RuntimeException | ExecutionException | InterruptedException e) {
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
		try {
			Segment segment = segments().document(name).get().get().toObject(Segment.class);
			if (segment == null) {
				segment = newSegment(name);
			}
			return new SegmentIndexOutput(laeContext, index, segment);
		} catch (ExecutionException e) {
			throw new IOException(e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException(e);
		}
	}

	@Override
	public IndexOutput createTempOutput(String prefix, String suffix, IOContext context) throws IOException {
		final String tempName = prefix + "_" + UUID.randomUUID().toString() + "_" + suffix;
		ensureOpen();
		try {
			Segment segment = segments().document(tempName).get().get().toObject(Segment.class);
			if(segment == null) {
				segment = newSegment(tempName);
			}
			return new SegmentIndexOutput(laeContext, index, segment);
		} catch (ExecutionException e) {
			throw new IOException(e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.lucene.store.Directory#deleteFile(java.lang.String)
	 */
	@Override
	public void deleteFile(String name) {
		WriteBatch batch = laeContext.firestore.batch();

		DocumentReference segmentRef = segments().document(name);
		batch.delete(segmentRef);
		segmentRef.collection(laeContext.firestoreCollectionMapper.getCollectionName(SegmentHunk.class)).listDocuments().forEach(batch::delete);
		batch.commit();
	}
	@Override
	public void rename(final String source, final String dest) throws IOException {
		try {
			laeContext.firestore.runTransaction(t -> {
				DocumentReference segmentRef = segments().document(source);
				Segment sourceSegment = segmentRef.get().get().toObject(Segment.class);
				Iterable<DocumentReference> allBySegment = segmentRef.collection(laeContext.firestoreCollectionMapper.getCollectionName(SegmentHunk.class)).listDocuments();

				Segment destSegment = new Segment(dest);
				destSegment.length = sourceSegment.length;
				destSegment.lastModified = sourceSegment.lastModified;
				destSegment.hunkCount = sourceSegment.hunkCount;
				DocumentReference destSegmentRef = laeContext.firestore.collection(laeContext.firestoreCollectionMapper.getCollectionName(LuceneIndex.class))
						.document(index.getName())
						.collection(laeContext.firestoreCollectionMapper.getCollectionName(Segment.class))
						.document(destSegment.name);
				destSegmentRef
						.set(destSegment)
						.get();


				for (DocumentReference segmentHunk : allBySegment) {
					SegmentHunk sourceHunk = segmentHunk.get().get().toObject(SegmentHunk.class);
					SegmentHunk destHunk = new SegmentHunk(sourceHunk.id);
					destHunk.bytes = sourceHunk.bytes;
					destSegmentRef.collection(laeContext.firestoreCollectionMapper.getCollectionName(SegmentHunk.class))
							.document(destHunk.id)
							.set(destHunk);
				}

				deleteFile(source);
				return null;
			}, TransactionOptions.createReadWriteOptionsBuilder().build())
					.get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
	/*
	 * (non-Javadoc)
	 * @see org.apache.lucene.store.Directory#fileLength(java.lang.String)
	 */
	@Override
	public long fileLength(String name) throws IOException {
		try {
			final Segment bigTableIndexFile = laeContext.firestore
					.collection(laeContext.firestoreCollectionMapper.getCollectionName(LuceneIndex.class))
					.document(index.getName())
					.collection(laeContext.firestoreCollectionMapper.getCollectionName(Segment.class))
					.document(name).get().get().toObject(Segment.class);
			return bigTableIndexFile.length;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException(e);
		} catch (ExecutionException e) {
			throw new IOException(e);
		}
	}
	/*
	 * (non-Javadoc)
	 * @see org.apache.lucene.store.Directory#listAll()
	 */
	@Override
	public String[] listAll() {
		List<String> files = StreamSupport.stream(
			laeContext.firestore
					.collection(laeContext.firestoreCollectionMapper.getCollectionName(LuceneIndex.class))
					.document(index.getName())
					.collection(laeContext.firestoreCollectionMapper.getCollectionName(Segment.class))
					.listDocuments()
					.spliterator(), false)
				.map(DocumentReference::getId).toList();
		return files.toArray(new String[0]);
	}

	/**
	 * Create a new file in the index.
	 * @param name The name of the segment/file
	 * @return the instance of the Segment
	 * @throws ExecutionException If an error occurs
	 * @throws InterruptedException If the thread is interrupted
	 */
	protected Segment newSegment(String name) throws ExecutionException, InterruptedException {
		Segment segment = new Segment(name);
		SegmentHunk newHunk = segment.newHunk();//at least one segment
		segment.lastModified = System.currentTimeMillis();
		//TODO; bulk?
		DocumentReference segmentRef = segments()
				.document(segment.name);

		segmentRef.create(segment).get();

		laeContext.firestore.document(segmentRef.getPath())
				.collection(laeContext.firestoreCollectionMapper.getCollectionName(SegmentHunk.class))
				.document(newHunk.id)
				.create(newHunk)
				.get();

		log.debug("Created segment '{}'.", name);
		return segment;
	}

	@Override
	public void sync(Collection<String> names) throws IOException {
//		PendingFutures.completeAllPendingFutures();
	}
	@Override
	public void syncMetaData() throws IOException {
//		PendingFutures.completeAllPendingFutures();
	}
	private Segment copySegment(Segment sourceSegment) {
		Segment copy = new Segment(sourceSegment.name);
		copy.lastModified = System.currentTimeMillis();
		copy.length = sourceSegment.length;
		copy.hunkCount = sourceSegment.hunkCount;
		return copy;
	}
	private SegmentHunk copySegmentHunk(SegmentHunk hunk) {
		SegmentHunk copy = new SegmentHunk(hunk.id);
		copy.bytes = hunk.bytes;
		return copy;
	}

}

package com.googlecode.luceneappengine;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.SerialMergeScheduler;

/**
 * Class that provides configuration utility in order to use {@link GaeDirectory}.
 * 
 * @author Fabio Grucci (github: <i>UltimaPhoenix</i>, bitbucket: <i>Dark_Phoenix</i>, googlecode: <i>fabio.grucci</i>)
 *
 */
public final class GaeLuceneUtil {

	/**
	 * Method that return {@link IndexWriterConfig} properly configured in order to 
	 * work in google app engine environment.
	 * @param analyzer The analyzer to use
	 * @return An {@link IndexWriterConfig} properly configured
	 */
	@SuppressWarnings("resource")//SerialMergeScheduler is Closable
	public static IndexWriterConfig getIndexWriterConfig(Analyzer analyzer) {
		final IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setMergeScheduler(new SerialMergeScheduler());
		return config;
	}
	
}

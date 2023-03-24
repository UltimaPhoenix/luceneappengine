package com.googlecode.luceneappengine;

import com.google.cloud.firestore.DocumentReference;
import com.googlecode.luceneappengine.model.LuceneIndex;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LuceneIndexRepositoryTest extends LocalFirestoreTest {

    @Test
    public void testSaveAndLoad() {
        DocumentReference foo = laeContext.luceneIndexRepository.saveIndex(new LuceneIndex("foo"));
        LuceneIndex index = laeContext.luceneIndexRepository.getOrCreate("foo");
        assertThat(index.getName()).isEqualTo("foo");

        List<LuceneIndex> availableIndexes = GaeDirectory.getAvailableIndexes(laeContext);
        assertThat(availableIndexes).containsOnly(new LuceneIndex("foo"));
    }
}

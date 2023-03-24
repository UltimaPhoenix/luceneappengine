package com.googlecode.luceneappengine;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.googlecode.luceneappengine.firestore.FirestoreCollectionMapper;
import com.googlecode.luceneappengine.model.repository.LaeContext;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeoutException;

public abstract class LocalFirestoreTest {

    protected static final Firestore firestore = FirestoreOptions.getDefaultInstance()
            .toBuilder().build().getService();

    protected static final LaeContext laeContext = new LaeContext(firestore);

    @BeforeClass
    public static void setUpOnce() throws IOException, InterruptedException {

    }

    @AfterClass
    public static void tearDownOnce() throws IOException, InterruptedException, TimeoutException {
    }

    @Before
    public void setUp() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
        String response = httpClient.send(HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/emulator/v1/projects/demo-boh/databases/(default)/documents"))
                        .DELETE().build(), HttpResponse.BodyHandlers.ofString())
                .body();
    }
}

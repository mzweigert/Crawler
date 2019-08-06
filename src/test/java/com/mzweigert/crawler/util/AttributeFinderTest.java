package com.mzweigert.crawler.util;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.mzweigert.crawler.FakeServerCreator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Java6Assertions.assertThat;


public class AttributeFinderTest {

    private static WireMockServer server;
    private static final String url = "http://localhost:8080";
    private static final int websiteStructureDepth = 3;

    @BeforeClass
    public static void setUpClass() {
        server = new FakeServerCreator(8080).createBinaryTreeStructureWebsite(websiteStructureDepth);
    }

    @AfterClass
    public static void clean() {
        server.stop();
    }

    @Test
    public void givenDOMDocument_whenFind_thenSuccessFindLinks() throws IOException {
        //GIVEN
        Document document = Jsoup.connect(url).get();
        HashSet<String> visitedBefore = new HashSet<>();

        //WHEN
        Set<String> result = AttributeFinder.getInstance(document).find(visitedBefore);

        //THEN
        assertThat(result).isNotEmpty();
    }
}
package com.mzweigert.crawler.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Java6Assertions.assertThat;


public class AttributeFinderTest {

    @Test
    public void givenDOMDocument_whenFind_thenSuccessFindLinks() throws IOException {
        //GIVEN
        Document document = Jsoup.connect("http://wiprodigital.com").get();
        HashSet<String> visitedBefore = new HashSet<>();

        //WHEN
        Set<String> result = AttributeFinder.getInstance(document).find(visitedBefore);

        //THEN
        assertThat(result).isNotEmpty();
    }
}
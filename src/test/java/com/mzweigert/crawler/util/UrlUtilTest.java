package com.mzweigert.crawler.util;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;


public class UrlUtilTest {

    @Test
    public void extractRootUrl() throws MalformedURLException {
        //GIVEN
        String root = "http://example.com/";
        URL url = new URL(root + "/page/to/sub/domain");

        //WHEN
        String rootUrl = UrlUtil.extractRootUrl(url);

        //THEN
        assertThat(rootUrl).isEqualTo(root.substring(0, root.length() - 1));

    }

    @Test
    public void normalizeLink_removeSlashAtEnd() {
        //GIVEN
        String root = "http://example.com/";

        //WHEN
        String result = UrlUtil.normalizeLink(root, root);

        //THEN
        assertThat(result).isEqualTo(root.substring(0, root.length() - 1));
    }

    @Test
    public void normalizeLink_addRootUrlBefore() {
        //GIVEN
        String root = "http://example.com";
        String sub = "/sub/page";

        //WHEN
        String result = UrlUtil.normalizeLink(root, sub);

        //THEN
        assertThat(result).isEqualTo(root + sub);
    }

    @Test
    public void givenUrlToPNG_whenIsFileResource_thenReturnTrue() {
        //GIVEN
        String url = "http://example.com/file.png";

        //WHEN
        boolean result = UrlUtil.isFileResource(url);

        //THEN
        assertThat(result).isTrue();
    }

    @Test
    public void givenUrlToHtmlPage_whenIsFileResource_thenReturnTrue() {
        //GIVEN
        String url = "http://example.com/page.html";

        //WHEN
        boolean result = UrlUtil.isFileResource(url);

        //THEN
        assertThat(result).isFalse();
    }

    @Test
    public void givenUrlToPage_whenIsFileResource_thenReturnTrue() {
        //GIVEN
        String url = "http://example.com";

        //WHEN
        boolean result = UrlUtil.isFileResource(url);

        //THEN
        assertThat(result).isFalse();
    }
}
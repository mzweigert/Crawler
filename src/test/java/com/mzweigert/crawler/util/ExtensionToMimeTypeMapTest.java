package com.mzweigert.crawler.util;

import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ExtensionToMimeTypeMapTest {

    @Test
    public void givenPDFExtension_whenHasExtension_thenReturnTrue() {
        //GIVEN
        String extension = "pdf";

        //WHEN
        boolean result = ExtensionToMimeTypeMap.hasExtension(extension);

        //THEN
        assertThat(result).isTrue();
    }

    @Test
    public void givenNotExistingExtension_whenHasExtension_thenReturnTrue() {
        //GIVEN
        String extension = "abc";

        //WHEN
        boolean result = ExtensionToMimeTypeMap.hasExtension(extension);

        //THEN
        assertThat(result).isFalse();
    }

    @Test
    public void whenCallGet_thenReturnNotEmptyMap() {

        //WHEN
        Map<String, String> map = ExtensionToMimeTypeMap.get();

        //THEN
        assertThat(map).isNotEmpty();
    }
}
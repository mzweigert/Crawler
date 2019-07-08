package com.mzweigert.crawler.model.node;

import com.mzweigert.crawler.model.VisitedLinks;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Java6Assertions.assertThat;


public class PageNodeMapperTest {

    @Test
    public void givenManyUrls_whenMapMany_thenReturnDifferentPageNodes() {
        //GIVEN
        VisitedLinks links = new VisitedLinks("http://www.example.com");
        Set<String> toMap = IntStream.range(0, 5)
                .mapToObj(i -> links.getRootUrl() + "/i")
                .collect(Collectors.toSet());
        toMap.add(links.getRootUrl());
        toMap.add("http://www.unknown.com");
        toMap.add("http://www.external.com/file.js");
        toMap.add("http://www.example.com/file.js");
        toMap.add("invalid_link");

        //THEN
        Set<PageNode> result = PageNodeMapper.mapMany(links, toMap);

        //THEN
        assertThat(result).isNotEmpty();
        Map<PageLinkType, List<PageNode>> groupedByType = result.stream()
                .collect(Collectors.groupingBy(PageNode::getType));
        Arrays.stream(PageLinkType.values())
                .forEach(type -> {
                    List<PageNode> linksByType = groupedByType.get(type);
                    assertThat(linksByType).isNotEmpty();
                });
    }

    @Test
    public void givenUrl_whenMap_thenReturnNodeWithInternalRootDomainType() {
        String root = "http://www.example.com";

        PageNode result = PageNodeMapper.map(root, root);

        assertThat(result.getType()).isEqualTo(PageLinkType.INTERNAL_ROOT_DOMAIN);
    }

    @Test
    public void givenUrlAndSubDomainUrl_whenMap_thenReturnNodeWithInternalSubDomainType() {
        String root = "http://www.example.com";
        String link = "http://www.example.com/subdomain/";

        PageNode result = PageNodeMapper.map(root, link);

        assertThat(result.getType()).isEqualTo(PageLinkType.INTERNAL_SUB_DOMAIN);
    }

    @Test
    public void givenUrlAndSubDomainUrlToFile_whenMap_thenReturnNodeWithInternalResourcesType() {
        String root = "http://www.example.com";
        String link = "http://www.example.com/subdomainfile.png";

        PageNode result = PageNodeMapper.map(root, link);

        assertThat(result.getType()).isEqualTo(PageLinkType.INTERNAL_RESOURCES);
    }

    @Test
    public void givenUrlAndUrlToExternalFile_whenMap_thenReturnNodeWithExternalResourcesType() {
        String root = "http://www.example.com";
        String link = "http://www.external.com/file.png";

        PageNode result = PageNodeMapper.map(root, link);

        assertThat(result.getType()).isEqualTo(PageLinkType.EXTERNAL_RESOURCES);
    }

    @Test
    public void givenUrlAndExternalUrl_whenMap_thenReturnNodeWithExternalDomainType() {
        String root = "http://www.example.com";
        String link = "http://www.external.com";

        PageNode result = PageNodeMapper.map(root, link);

        assertThat(result.getType()).isEqualTo(PageLinkType.EXTERNAL_DOMAIN);
    }

    @Test
    public void givenUrlAndInvalidUrl_whenMap_thenReturnNodeWithInvalidLinkType() {
        String root = "http://www.example.com";
        String link = "invalid_link";

        PageNode result = PageNodeMapper.map(root, link);

        assertThat(result.getType()).isEqualTo(PageLinkType.INVALID_LINK);
    }
}
package com.mzweigert.crawler.model.link;

import com.mzweigert.crawler.model.VisitedLinks;
import com.mzweigert.crawler.util.UrlUtil;
import org.apache.commons.validator.routines.UrlValidator;

import java.util.Set;
import java.util.stream.Collectors;

public class PageLinkMapper {

    public static Set<PageLink> mapMany(VisitedLinks visitedLinks, Set<String> toMap) {
        return toMap.stream()
                .map(link -> UrlUtil.normalizeLink(visitedLinks.getRootUrl(), link))
                .filter(normalizedLink -> !visitedLinks.contains(normalizedLink))
                .map(link -> map(visitedLinks.getRootUrl(), link))
                .collect(Collectors.toSet());
    }

    public static PageLink map(String rootUrl, String link) {
        PageLinkType type = PageLinkTypeDeterminant.determine(rootUrl, link);
        return new PageLink(link, type);
    }
}

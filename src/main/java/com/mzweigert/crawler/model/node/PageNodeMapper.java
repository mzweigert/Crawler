package com.mzweigert.crawler.model.node;

import com.mzweigert.crawler.model.VisitedLinks;
import com.mzweigert.crawler.util.UrlUtil;
import org.apache.commons.validator.routines.UrlValidator;

import java.util.Set;
import java.util.stream.Collectors;

public class PageNodeMapper {

    private static final UrlValidator validator = new UrlValidator();

    public static Set<PageNode> mapMany(VisitedLinks visitedLinks, Set<String> toMap) {
        return toMap.stream()
                .map(link -> UrlUtil.normalizeLink(visitedLinks.getRootUrl(), link))
                .filter(normalizedLink -> !visitedLinks.contains(normalizedLink))
                .map(link -> map(visitedLinks.getRootUrl(), link))
                .collect(Collectors.toSet());
    }

    public static PageNode map(String rootUrl, String link) {
        if (!validator.isValid(link)) {
            return new PageNode(link, PageLinkType.INVALID_LINK);
        }

        boolean isFromRootDomain = link.startsWith(rootUrl);
        if (UrlUtil.isFileResource(link)) {
            return new PageNode(link, isFromRootDomain ? PageLinkType.INTERNAL_RESOURCES : PageLinkType.EXTERNAL_RESOURCES);
        }

        if (!isFromRootDomain) {
            return new PageNode(link, PageLinkType.EXTERNAL_DOMAIN);
        }

        if (link.length() == rootUrl.length()) {
            return new PageNode(link, PageLinkType.INTERNAL_ROOT_DOMAIN);
        }

        return new PageNode(link, PageLinkType.INTERNAL_SUB_DOMAIN);

    }
}

package com.mzweigert.crawler.model.link;

import com.mzweigert.crawler.util.UrlUtil;
import org.apache.commons.validator.routines.UrlValidator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

class PageLinkTypeDeterminant {

    private static final UrlValidator validator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);

    private static LinkedHashMap<Rule, PageLinkType> orderedRules = new LinkedHashMap<>();

    static {
        orderedRules.put(new Rule((root, link) -> !validator.isValid(link)), PageLinkType.INVALID_LINK);
        orderedRules.put(new Rule((root, link) -> UrlUtil.isFileResource(link) && link.startsWith(root)),
                PageLinkType.INTERNAL_RESOURCES);
        orderedRules.put(new Rule((root, link) -> UrlUtil.isFileResource(link) && !link.startsWith(root)),
                PageLinkType.EXTERNAL_RESOURCES);
        orderedRules.put(new Rule((root, link) -> !link.startsWith(root)), PageLinkType.EXTERNAL_DOMAIN);
        orderedRules.put(new Rule((root, link) -> link.length() == root.length()), PageLinkType.INTERNAL_ROOT_DOMAIN);
        orderedRules.put(new Rule((root, link) -> true), PageLinkType.INTERNAL_SUB_DOMAIN);

    }

    static PageLinkType determine(String rootUrl, String link) {
        return orderedRules.entrySet()
                .stream()
                .filter(entry -> entry.getKey().evaluate(rootUrl, link))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseThrow(IllegalStateException::new);
    }

    private static class Rule {
        private BiFunction<String, String, Boolean> expression;

        Rule(BiFunction<String, String, Boolean> expression) {
            this.expression = expression;
        }

        boolean evaluate(String rootUrl, String link) {
            return expression.apply(rootUrl, link);
        }
    }

}

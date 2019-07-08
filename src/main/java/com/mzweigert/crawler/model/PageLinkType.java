package com.mzweigert.crawler.model;

import java.util.Arrays;

public enum PageLinkType {

    INTERNAL_ROOT_DOMAIN,
    INTERNAL_SUB_DOMAIN,
    INTERNAL_RESOURCES,
    INVALID_LINK,
    EXTERNAL_DOMAIN;

    public boolean isOneOf(PageLinkType... types) {
        return Arrays.stream(types)
                .anyMatch(type -> type == this);
    }

}

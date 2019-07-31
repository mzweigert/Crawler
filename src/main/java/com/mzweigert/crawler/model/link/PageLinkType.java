package com.mzweigert.crawler.model.link;

import java.util.Arrays;

public enum PageLinkType {

    INTERNAL_ROOT_DOMAIN,
    INTERNAL_SUB_DOMAIN,
    INTERNAL_RESOURCES,
    EXTERNAL_RESOURCES,
    EXTERNAL_DOMAIN,
    INVALID_LINK;

    public boolean isOneOf(PageLinkType... types) {
        return Arrays.stream(types)
                .anyMatch(type -> type == this);
    }

}

package com.mzweigert.crawler.service;

import com.mzweigert.crawler.model.PageLinkType;

class DomainVisitedData {

    private String currentLink;
    private PageLinkType currentType;

    public DomainVisitedData(String currentLink) {
        this.currentLink = currentLink;
        this.currentType = PageLinkType.INTERNAL_MAIN_DOMAIN;
    }

    public DomainVisitedData(String currentLink, PageLinkType type) {
        this.currentLink = currentLink;
        this.currentType = type;
    }

    public String getCurrentLink() {
        return currentLink;
    }

    public PageLinkType getCurrentType() {
        return currentType;
    }

}

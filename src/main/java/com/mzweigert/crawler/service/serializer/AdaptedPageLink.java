package com.mzweigert.crawler.service.serializer;

import com.mzweigert.crawler.model.link.PageLink;
import com.mzweigert.crawler.model.link.PageLinkType;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "page-link")
public class AdaptedPageLink {

    private String url;
    private PageLinkType type;

    AdaptedPageLink() { }

    AdaptedPageLink(PageLink pageLink) {
        this.url = pageLink.getUrl();
        this.type = pageLink.getType();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public PageLinkType getType() {
        return type;
    }

    public void setType(PageLinkType type) {
        this.type = type;
    }
}

package com.mzweigert.crawler.service.serializer.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;

@XmlRootElement(name = "page-links")
@XmlAccessorType(XmlAccessType.FIELD)
public class LinksWrapper<T> {

    @XmlAnyElement(lax = true)
    private Collection<T> links;

    public LinksWrapper() { }

    LinksWrapper(Collection<T> links) {
        this.links = links;
    }

    public Collection<T> getLinks() {
        return links;
    }

    public void setLinks(Collection<T> links) {
        this.links = links;
    }

}

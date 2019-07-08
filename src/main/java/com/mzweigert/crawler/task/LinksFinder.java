package com.mzweigert.crawler.task;

import com.mzweigert.crawler.model.PageLinkType;
import com.mzweigert.crawler.model.PageNode;
import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.nodes.Document;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.stream.Collectors;

public class LinksFinder {

    private final Document document;

    private LinksFinder(Document document){
        this.document = document;
    }

    public static LinksFinder getInstance(Document document) {
        return new LinksFinder(document);
    }

    Set<String> find(Set<String> visitedBefore) {
        return document.select("a[href]")
                .stream()
                .map(a -> a.attr("href"))
                .filter(this::isValidLink)
                .filter(node -> !visitedBefore.contains(node))
                .collect(Collectors.toSet());
    }

    private boolean isValidLink(String link) {
        return link.length() > 1 && link.lastIndexOf("#") < 0;
    }

}

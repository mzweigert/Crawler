package com.mzweigert.crawler.service;

import com.mzweigert.crawler.model.PageLinkType;
import com.mzweigert.crawler.model.PageNode;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class RecursiveCrawlerServiceImpl implements CrawlerService {

    @Override
    public PageNode crawl(String url) throws IOException {
        if(url.endsWith("/")){
            url = url.substring(0, url.length() - 1);
        }
        DomainVisitedData visitedData = new DomainVisitedData(url);
        GlobalVisitedData globalVisitedData = new GlobalVisitedData(url.substring(url.indexOf("/")));

        PageNode node = createNode(visitedData, globalVisitedData, 0);
        return node;
    }

    private PageNode createNode(DomainVisitedData data, GlobalVisitedData globalData, int depth) throws IOException {

        PageNode node = new PageNode(data.getCurrentLink(), data.getCurrentType());
        globalData.putNode(node);

        Collection<PageNode> subDomains = new HashSet<>();
        if(depth < MAX_DEPTH){
            subDomains = findSubDomains(data, globalData, depth);
        } else {
            System.out.println("MAX_DEPTH_REACHED");
        }
        node.getSubLinks().addAll(subDomains);

        return node;
    }

    private Collection<PageNode> findSubDomains(DomainVisitedData data, GlobalVisitedData globalData, int depth) throws IOException {
        Connection connect = Jsoup.connect(data.getCurrentLink()).ignoreContentType(true);
        Set<PageNode> links = findDownloadOrErrorLink(data, connect);
        if (!links.isEmpty()) {
            return links;
        }
        Document doc = connect.get();

        Collection<String> hrefs = extractLinks(doc, globalData.getMainDomainUrl());

        for (String href : hrefs){
            if(globalData.containsNode(href)){
                links.add(globalData.getNode(href));
                continue;
            }

            int indexOfSlash = href.indexOf("/");
            if (indexOfSlash < 0) {
                System.out.println(href);
                continue;
            }
            String hrefWithoutProtocol = href.substring(href.indexOf("/"));
            if(!href.equals(data.getCurrentLink()) &&
                hrefWithoutProtocol.startsWith(globalData.getMainDomainUrl()) &&
                hrefWithoutProtocol.length() != globalData.getMainDomainUrl().length()){

                depth++;
                DomainVisitedData subData = new DomainVisitedData(href, PageLinkType.INTERNAL_SUB_DOMAIN);
                PageNode node = createNode(subData, globalData, depth);
                links.add(node);
            }
        }
        return links;
    }

    private Set<PageNode> findDownloadOrErrorLink(DomainVisitedData data, Connection connect) throws IOException {
        Set<PageNode> links = new HashSet<>();
        Connection.Response response;
        try {
            response = connect.execute();
        } catch (HttpStatusException e) {
            PageNode pageNode = new PageNode(data.getCurrentLink(), PageLinkType.INTERNAL_ERROR_PAGE);
            links.add(pageNode);
            return links;
        }

        if(!response.contentType().startsWith("text")){
            PageNode pageNode = new PageNode(data.getCurrentLink(), PageLinkType.INTERNAL_RESOURCES);
            links.add(pageNode);
        }
        return links;
    }

    private Collection<String> extractLinks(Document doc, String mainDomainUrl) {
        return doc.select("a[href]")
                .stream()
                .map(a -> a.attr("href"))
                .filter(link -> link.lastIndexOf("#") < 0)
                .filter(link -> link.lastIndexOf("?") < 0)
                .filter(link -> link.length() > 1)
                .map(link -> {
                    if(link.startsWith("/")){
                      return "http:" + mainDomainUrl + link;
                    } else if (link.charAt(link.length() - 1) == '/'){
                        return link.substring(0, link.length() - 1);
                    }
                    return link;
                })
                .collect(Collectors.toSet());
    }

}

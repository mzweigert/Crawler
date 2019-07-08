package com.mzweigert.crawler.service;

import com.mzweigert.crawler.model.PageNode;

import java.util.HashMap;

public class GlobalVisitedData {

    private String mainDomainUrl;
    private HashMap<String, PageNode> savedNodes;

    public GlobalVisitedData(String mainDomainUrl) {
        this.mainDomainUrl = mainDomainUrl;
        this.savedNodes = new HashMap<>();
    }

    public String getMainDomainUrl() {
        return mainDomainUrl;
    }


    public void putNode(PageNode node){
        this.savedNodes.put(node.getDomainUrl(), node);
    }

    public PageNode getNode(String key){
        return this.savedNodes.get(key);
    }

    public boolean containsNode(String key) {
        return this.savedNodes.containsKey(key);
    }

}

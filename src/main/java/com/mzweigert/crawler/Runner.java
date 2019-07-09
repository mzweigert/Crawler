package com.mzweigert.crawler;

import com.mzweigert.crawler.model.node.PageLink;
import com.mzweigert.crawler.service.CrawlerService;
import com.mzweigert.crawler.service.CrawlerServiceImpl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Runner {

    private final RunnerArgs args;
    private final CrawlerService crawlerService;

    Runner(RunnerArgs args, CrawlerService crawlerService) {
        this.args = args;
        this.crawlerService = crawlerService;
    }

    void run() {
        if (!args.isSuccess()) {
            System.out.println("Url argument not found. Try run with [-u http://example_domain.com!]");
            return;
        }

        Collection<PageLink> collection;
        if(args.getMaxDepth() > 0) {
            System.out.println("Start crawling url: " + args.getUrl() + " with depth = " + args.getMaxDepth());
            collection = crawlerService.crawl(args.getUrl(), args.getMaxDepth());
        } else {
            System.out.println("Start crawling url: " + args.getUrl());
            collection = crawlerService.crawl(args.getUrl());
        }
        System.out.println("Found " + collection.size() + " links.");
    }

    public static void main(String[] args) {
        List<String> argList = Arrays.stream(args).collect(Collectors.toList());
        RunnerArgs runnerArgs = new RunnerArgs(argList);
        CrawlerService crawlerService = new CrawlerServiceImpl();
        new Runner(runnerArgs, crawlerService).run();
    }

}

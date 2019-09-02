package com.mzweigert.crawler;

import com.mzweigert.crawler.model.link.PageLink;
import com.mzweigert.crawler.service.crawler.CrawlerArgs;
import com.mzweigert.crawler.service.crawler.CrawlerService;
import com.mzweigert.crawler.service.crawler.CrawlerServiceImpl;
import com.mzweigert.crawler.service.serializer.FileSerializationService;
import com.mzweigert.crawler.service.serializer.FileSerializationServiceFactory;
import com.mzweigert.crawler.service.serializer.SerializationType;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Runner {

    private final RunnerArgs args;
    private final CrawlerService crawlerService;
    private final FileSerializationService serializerService;

    Runner(RunnerArgs args, CrawlerService crawlerService, FileSerializationService serializerService) {
        this.args = args;
        this.crawlerService = crawlerService;
        this.serializerService = serializerService;
    }

    void run() {
        if (!args.isSuccess()) {
            System.out.println("Url argument not found. Try run with [-u http://example_domain.com!]");
            return;
        }
        serialize();
    }

    private void serialize() {
        String fileName;
        try {
            fileName = new URL(args.getUrl()).getHost();
            Optional<String> directory = createOrFindDirectory(fileName);
            if (!directory.isPresent()) {
                System.out.println("Cannot find directory: " + fileName);
                return;
            }
            if (args.isGrouped()) {
                serializerService.serializeGrouped(directory.get(), fileName, findPageLinks());
            } else {
                File file = new File(directory.get() + fileName + ".xml");
                if (file.exists()) {
                    System.out.println("File : " + file.getName() + " exists in directory: " + directory.get());
                } else {
                    serializerService.serialize(file, findPageLinks());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Collection<PageLink> findPageLinks() {
        long start, end;
        Collection<PageLink> crawl;
        start = System.currentTimeMillis();
		CrawlerArgs crawlerArgs = args.mapToCrawlerArgs();
		System.out.println("Start crawling url: " + crawlerArgs.getStartUrl() + " with depth = " + crawlerArgs.getMaxDepth());
		crawl = crawlerService.crawl(crawlerArgs);
        end = System.currentTimeMillis();

        float sec = (end - start) / 1000F;
        System.out.println("Crawling took " + sec + " seconds");
        System.out.println("Found " + crawl.size() + " links.");
        return crawl;
    }

    private Optional<String> createOrFindDirectory(String fileName) {
        String path = "./output/" + fileName + "/";
        boolean success = true;
        if (!Files.isDirectory(Paths.get(path))) {
            success = new File(path).mkdirs();
        }
        return Optional.ofNullable(success ? path : null);
    }

    public static void main(String[] args) {
        List<String> argList = Arrays.stream(args).collect(Collectors.toList());
        RunnerArgs runnerArgs = new RunnerArgs(argList);

        SerializationType type = runnerArgs.getSerializationType();
        FileSerializationService serializationService = FileSerializationServiceFactory.getInstance(type);

        CrawlerService crawlerService = new CrawlerServiceImpl();
        new Runner(runnerArgs, crawlerService, serializationService).run();
    }

}

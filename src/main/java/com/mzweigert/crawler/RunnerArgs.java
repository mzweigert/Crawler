package com.mzweigert.crawler;

import com.mzweigert.crawler.service.crawler.CrawlerArgs;
import com.mzweigert.crawler.service.serializer.SerializationType;
import org.apache.commons.validator.routines.UrlValidator;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunnerArgs {

    private static final Logger logger = LoggerFactory.getLogger(RunnerArgs.class);

    private int maxDepth;
    private String url;
    private SerializationType serializationType = SerializationType.XML;
    private boolean grouped;
    private boolean parsedStatus;

    public RunnerArgs(){}
    public RunnerArgs(List<String> argList) {
        this.parseArgs(argList);
    }

    int getMaxDepth() {
        return maxDepth;
    }

    String getUrl() {
        return url;
    }

    SerializationType getSerializationType() {
        return serializationType;
    }

    public boolean isGrouped() {
        return grouped;
    }

    boolean isSuccess() {
        return parsedStatus;
    }

    private void parseArgs(List<String> argList) {
        int i = 0;
        while (i < argList.size()) {
            String arg = argList.get(i);
            switch (arg) {
                case "--url":
                case "-u":
                    if (tryInitUrl(argList, i)) {
                        i++;
                    } else {
                        return;
                    }
                    break;
                case "--depth":
                case "-d":
                    if (tryInitDepth(argList, i)) {
                        i++;
                    }
                    break;
                //todo : uncomment when more serialization types will be available
                /*case "--serialization":
                case "-s":
                    if (tryInitSerializationType(argList, i)) {
                        i++;
                    }
                    break;*/
                case "-g":
                case "--grouped":
                    grouped = true;
                    break;
                default:
                    logger.warn(arg + " argument not recognized!");
                    break;
            }
            i++;
        }
    }

    private boolean tryInitSerializationType(List<String> argList, int i) {
        String type = null;
        boolean initialized = false;
        if (argList.size() >= i + 2) {
            type = argList.get(i + 1);
        }
        if (type == null) {
            logger.warn("No serialization type found! Default serialization: xml");
        } else {
            try {
                serializationType = SerializationType.valueOf(type);
                initialized = true;
            } catch (IllegalArgumentException e) {
                logger.error("Cannot found " + type + " serialization. Default serialization xml");
            }
        }
        return initialized;
    }

    private boolean tryInitDepth(List<String> argList, int i) {
        String depth = null;
        boolean maxDepthInitialized = false;
        if (argList.size() >= i + 2) {
            depth = argList.get(i + 1);
        }
        if (depth == null) {
            logger.warn("No depth value found!");
        } else {
            try {
                maxDepth = Integer.parseInt(depth);
                maxDepthInitialized = true;
            } catch (NumberFormatException e) {
                logger.warn(depth + " must be a number between 0 and 100!");
            }
            if (maxDepth < 0 || maxDepth > 100) {
                maxDepth = 0;
                maxDepthInitialized = true;
                logger.warn(depth + " must be a number between 0 and 100!");
            }
        }
        return maxDepthInitialized;
    }

    private boolean tryInitUrl(List<String> argList, int i) {
        if (argList.size() >= i + 2) {
            url = argList.get(i + 1);
            parsedStatus = true;
        } else {
            logger.error("No url value found!");
        }
        if (!UrlValidator.getInstance().isValid(url)) {
            parsedStatus = false;
            logger.error("Url is invalid!");
        }
        return parsedStatus;
    }

    public CrawlerArgs mapToCrawlerArgs() {
        return CrawlerArgs.initBuilder()
                .withStartUrl(url)
                .withMaxDepth(maxDepth)
                .build();
    }
}
package com.mzweigert.crawler.service.serializer;

import com.mzweigert.crawler.model.link.PageLink;

import com.mzweigert.crawler.model.link.PageLinkType;
import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface FileSerializationService {

    Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Method serialize given links to file (depends on implementation)
     */
    default void serialize(File file, Collection<PageLink> links) {
        if (links.isEmpty()) {
            logger.warn("Found collections links is empty.");
        } else if (file.exists()) {
            logger.warn("File : " + file.getName() + " exists in directory: " + file.getPath());
        } else {
            Set<AdaptedPageLink> mapped = links.stream()
                    .map(AdaptedPageLink::new)
                    .collect(Collectors.toSet());
            saveToFile(mapped, file);
        }
    }

    /**
     * Method split given links by {@link com.mzweigert.crawler.model.link.PageLinkType}
     * and save grouped in separate files
     */
    default void serializeGrouped(String directoryFile, String prefixFileName, Collection<PageLink> links) {
        if (links.isEmpty()) {
            logger.warn("Found collections links is empty.");
            return;
        }
        links.parallelStream()
                .collect(Collectors.groupingByConcurrent(PageLink::getType, Collectors.toSet()))
                .entrySet()
                .parallelStream()
                .forEach(entry -> saveGroupedToFile(directoryFile, prefixFileName, entry.getKey(), entry.getValue()));
    }

    void saveGroupedToFile(String directoryFile, String prefixFileName, PageLinkType type, Collection<PageLink> links);

    <T> void saveToFile(Collection<T> links, File file);
}

package com.mzweigert.crawler.service.serializer;

import com.mzweigert.crawler.model.node.PageLink;

import java.io.File;
import java.util.Collection;

public interface FileSerializationService {

    /**
     * Method serialize given links to file (depends on implementation)
     */
    void serialize(File file, Collection<PageLink> links);

    /**
     * Method split given links by {@link com.mzweigert.crawler.model.node.PageLinkType}
     * and save grouped in separate files
     */
    void serializeGrouped(String directoryFile, String prefixFileName, Collection<PageLink> links);
}

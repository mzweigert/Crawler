package com.mzweigert.crawler.service.serializer.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mzweigert.crawler.model.link.PageLink;
import com.mzweigert.crawler.model.link.PageLinkType;
import com.mzweigert.crawler.service.serializer.FileSerializationService;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONFileSerializationService implements FileSerializationService {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void saveGroupedToFile(String directoryFile, String prefixFileName, PageLinkType type, Collection<PageLink> links) {
        List<String> linkStrings = links.stream()
                .map(PageLink::getUrl)
                .collect(Collectors.toList());
        File file = new File(directoryFile + prefixFileName + "_" + type.toString().toLowerCase() + ".json");
        saveToFile(linkStrings, file);
    }

    @Override
    public <T> void saveToFile(Collection<T> links, File file) {
        try {
            mapper.writer()
                    .withDefaultPrettyPrinter()
                    .writeValue(file, links);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}

package com.mzweigert.crawler.service.serializer.xml;

import com.mzweigert.crawler.model.link.PageLink;
import com.mzweigert.crawler.service.serializer.FileSerializationService;
import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlFileSerializationService implements FileSerializationService {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public void serialize(File file, Collection<PageLink> links) {
        if (links.isEmpty()) {
            logger.warn("Found collections links is empty.");
        } else if (file.exists()) {
            logger.warn("File : " + file.getName() + " exists in directory: " + file.getPath());
        } else {
            Set<AdaptedPageLink> mapped = links.stream()
                    .map(AdaptedPageLink::new)
                    .collect(Collectors.toSet());
            createXmlFile(mapped, file);
        }
    }

    @Override
    public void serializeGrouped(String directoryFile, String prefixFileName, Collection<PageLink> links) {
        if (links.isEmpty()) {
            logger.warn("Found collections links is empty.");
            return;
        }
        links.parallelStream()
                .collect(Collectors.groupingByConcurrent(PageLink::getType, Collectors.toSet()))
                .entrySet()
                .parallelStream()
                .forEach(entry -> {
                    File file = new File(directoryFile + prefixFileName + "_" + entry.getKey().toString().toLowerCase() + ".xml");
                    Set<JAXBElement<String>> mapped = entry.getValue()
                            .stream()
                            .map(link -> new JAXBElement<>(new QName("url"), String.class, link.getUrl()))
                            .collect(Collectors.toSet());
                    createXmlFile(mapped, file);
                });
    }

    private <T> void createXmlFile(Collection<T> links, File file) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(LinksWrapper.class, AdaptedPageLink.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            LinksWrapper<T> wrapper = new LinksWrapper<>(links);

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(wrapper, file);
            logger.warn("File : " + file.getName() + " created in directory: " + file.getPath());

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

}

package com.mzweigert.crawler.service.serializer.xml;

import com.mzweigert.crawler.model.link.PageLink;
import com.mzweigert.crawler.model.link.PageLinkType;
import com.mzweigert.crawler.service.serializer.AdaptedPageLink;
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
    public void saveGroupedToFile(String directoryFile, String prefixFileName, PageLinkType type, Collection<PageLink> links) {
        File file = new File(directoryFile + prefixFileName + "_" + type.toString().toLowerCase() + ".xml");
        Set<JAXBElement<String>> mapped = links.stream()
                .map(link -> new JAXBElement<>(new QName("url"), String.class, link.getUrl()))
                .collect(Collectors.toSet());
        saveToFile(mapped, file);
    }

    public <T> void saveToFile(Collection<T> links, File file) {
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

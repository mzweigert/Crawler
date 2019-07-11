package com.mzweigert.crawler.service.serializer.xml;

import com.mzweigert.crawler.model.node.PageLink;
import com.mzweigert.crawler.model.node.PageLinkType;
import com.mzweigert.crawler.service.serializer.FileSerializationService;
import com.mzweigert.crawler.service.serializer.FileSerializationServiceFactory;
import com.mzweigert.crawler.service.serializer.SerializationType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class XmlFileSerializationServiceTest {

    private FileSerializationService service = FileSerializationServiceFactory.getInstance(SerializationType.XML);

    private String directory = "./output_test/";
    private String fileName = "file";
    private String filePath = directory + fileName + ".xml";

    @Before
    public void setUp() {
        new File(directory).mkdir();
    }

    @After
    public void after() throws IOException {
        delete(new File(directory));
    }

    private void delete(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : Objects.requireNonNull(f.listFiles()))
                delete(c);
        }
        if (!f.delete())
            throw new FileNotFoundException("Failed to delete file: " + f);
    }

    @Test
    public void givenFile_whenSerialize_ThenSuccessCreatingXmlFile() {
        //GIVEN
        File file = new File(filePath);
        Collection<PageLink> pageLinks = createPageLinks();

        //WHEN
        service.serialize(file, pageLinks);

        //THEN
        try (FileReader fileReader = new FileReader(filePath);
             BufferedReader reader = new BufferedReader(fileReader)) {
            String line = reader.readLine();
            assertThat(line).isNotEmpty();
            assertThat(line).contains("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void serializeGroped() throws IOException {
        //GIVEN
        Collection<PageLink> pageLinks = createPageLinks();

        //WHEN
        service.serializeGrouped(directory, fileName, pageLinks);

        //THEN
        Set<String> suffixNameFiles = pageLinks.stream()
                .map(link -> "_" + link.getType().toString().toLowerCase() + ".xml")
                .collect(Collectors.toSet());
        for (String suffixNameFile : suffixNameFiles) {
            String line = null;
            try (FileReader fileReader = new FileReader(directory + fileName + suffixNameFile);
                 BufferedReader reader = new BufferedReader(fileReader)) {
                line = reader.readLine();
            }
            assertThat(line).isNotNull();
            assertThat(line).contains("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        }
    }


    private Collection<PageLink> createPageLinks() {
        Set<PageLink> links = IntStream.range(0, 5)
                .mapToObj(i -> new PageLink("http://www.example.com/i" + i, PageLinkType.INTERNAL_SUB_DOMAIN))
                .collect(Collectors.toSet());
        links.add(new PageLink("http://www.example.com", PageLinkType.INTERNAL_ROOT_DOMAIN));
        links.add(new PageLink("http://www.unknown.com", PageLinkType.EXTERNAL_DOMAIN));
        links.add(new PageLink("http://www.external.com/file.js", PageLinkType.EXTERNAL_RESOURCES));
        links.add(new PageLink("http://www.example.com/file.js", PageLinkType.INTERNAL_RESOURCES));
        links.add(new PageLink("invalid link", PageLinkType.INVALID_LINK));

        return links;
    }

}
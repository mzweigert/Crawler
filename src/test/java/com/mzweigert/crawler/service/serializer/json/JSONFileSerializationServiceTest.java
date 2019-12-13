package com.mzweigert.crawler.service.serializer.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mzweigert.crawler.model.link.PageLink;
import com.mzweigert.crawler.model.link.PageLinkType;
import com.mzweigert.crawler.service.serializer.AdaptedPageLink;
import com.mzweigert.crawler.service.serializer.FileSerializationService;
import com.mzweigert.crawler.service.serializer.FileSerializationServiceFactory;
import com.mzweigert.crawler.service.serializer.SerializationType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static org.assertj.core.api.Java6Assertions.assertThat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JSONFileSerializationServiceTest {

    private FileSerializationService service = FileSerializationServiceFactory.getInstance(SerializationType.JSON);
    private ObjectMapper mapper = new ObjectMapper();

    private String directory = "./output_test/";
    private String fileName = "file";
    private String filePath = directory + fileName + ".json";

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
    public void givenFile_whenSerialize_ThenSuccessCreatingJSONFile() throws Exception {
        //GIVEN
        File file = new File(filePath);
        Collection<PageLink> pageLinks = createPageLinks();

        //WHEN
        service.serialize(file, pageLinks);

        //THEN
        String line = "";
        try (FileReader fileReader = new FileReader(filePath);
             BufferedReader reader = new BufferedReader(fileReader)) {
            line = reader.lines().collect(Collectors.joining());
            assertThat(line).isNotEmpty();
        }
        Collection<AdaptedPageLink> results = mapper.readValue(line, new TypeReference<Collection<AdaptedPageLink>>() {
        });
        assertThat(results).hasSize(10);
        results.forEach(result -> assertThat(result).isNotNull());
    }

    @Test
    public void serializeGrouped() throws IOException {
        //GIVEN
        Collection<PageLink> pageLinks = createPageLinks();

        //WHEN
        service.serializeGrouped(directory, fileName, pageLinks);

        //THEN
        Set<String> suffixNameFiles = pageLinks.stream()
                .map(link -> "_" + link.getType().toString().toLowerCase() + ".json")
                .collect(Collectors.toSet());
        for (String suffixNameFile : suffixNameFiles) {
            String line = null;
            try (FileReader fileReader = new FileReader(directory + fileName + suffixNameFile);
                 BufferedReader reader = new BufferedReader(fileReader)) {
                line = reader.lines().collect(Collectors.joining());
                assertThat(line).isNotEmpty();
            }
            Collection<String> results = mapper.readValue(line, new TypeReference<Collection<String>>() {
            });
            results.forEach(result -> assertThat(result).isNotEmpty());
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
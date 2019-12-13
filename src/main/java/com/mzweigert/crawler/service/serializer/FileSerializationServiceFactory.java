package com.mzweigert.crawler.service.serializer;

import com.mzweigert.crawler.service.serializer.json.JSONFileSerializationService;
import com.mzweigert.crawler.service.serializer.xml.XmlFileSerializationService;

public class FileSerializationServiceFactory {

    public static FileSerializationService getInstance(SerializationType type) {
        switch (type) {
            case XML:
                return new XmlFileSerializationService();
            case JSON:
                return new JSONFileSerializationService();
            default:
                throw new IllegalArgumentException();

        }
    }
}

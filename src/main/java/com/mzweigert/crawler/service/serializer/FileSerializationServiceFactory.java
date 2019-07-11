package com.mzweigert.crawler.service.serializer;

import com.mzweigert.crawler.service.serializer.xml.XmlFileSerializationService;

public class FileSerializationServiceFactory {

    public static FileSerializationService getInstance(SerializationType type) {
        switch (type) {
            case XML:
                return new XmlFileSerializationService();
            default:
                throw new IllegalArgumentException();

        }
    }
}

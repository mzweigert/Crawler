package com.mzweigert.crawler.util;

import com.mzweigert.crawler.model.PageLinkType;
import com.mzweigert.crawler.model.PageNode;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

public class UrlUtil {

    public static String extractRootUrl(URL url) {
        if(url == null){
            return "";
        }
        return url.getProtocol() +
                (url.getPort() > 0 ? ":" + url.getPort() : "") +
                "://" +
                url.getHost();
    }

    public static String extract(String link) {
        if(link == null){
            return "";
        }
        URL url = null;
        try {
            url = new URL(link);
            return extractRootUrl(url) +
                    (url.getPath().length() == 1 ? "" : url.getPath());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static Optional<PageNode> findDownloadOrErrorLink(String url, Connection connect) throws IOException {
        Connection.Response response;
        try {
            response = connect.execute();
        } catch (HttpStatusException e) {
            return Optional.of(new PageNode(url, PageLinkType.INVALID_LINK));
        }

        if (!response.contentType().startsWith("text")) {
            return Optional.of(new PageNode(url, PageLinkType.INTERNAL_RESOURCES));
        }
        return Optional.empty();
    }
}

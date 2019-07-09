package com.mzweigert.crawler.util;

import java.net.URL;
import java.util.regex.Pattern;

public class UrlUtil {

    private UrlUtil() {
    }

    public static String extractRootUrl(URL url) {
        if (url == null) {
            return "";
        }
        return url.getProtocol() +
                (url.getPort() > 0 ? ":" + url.getPort() : "") +
                "://" +
                url.getHost();
    }

    public static String normalizeLink(String rootUrl, String link) {
        if (!link.startsWith("http") && link.charAt(0) == '/') {
            link = rootUrl + link;
        }
        if (link.endsWith("/")) {
            link = link.substring(0, link.length() - 1);
        }
        return link;
    }

    public static boolean isFileResource(String link) {
        String fileExtensionFromUrl = getFileExtensionFromUrl(link);
        return ExtensionToMimeTypeMap.hasExtension(fileExtensionFromUrl) &&
                !fileExtensionFromUrl.equals("html");
    }

    private static String getFileExtensionFromUrl(String url) {
        if (url != null && url.length() > 0) {
            int query = url.lastIndexOf('?');
            if (query > 0) {
                url = url.substring(0, query);
            }
            int filenamePos = url.lastIndexOf('/');
            String filename =
                    0 <= filenamePos ? url.substring(filenamePos + 1) : url;
            // if the filename contains special characters, we don't
            // consider it valid for our matching purposes:
            if (filename.length() > 0 &&
                    Pattern.matches("[a-zA-Z_0-9.\\-()%]+", filename)) {
                int dotPos = filename.lastIndexOf('.');
                if (0 <= dotPos) {
                    return filename.substring(dotPos + 1);
                }
            }
        }
        return "";
    }
}

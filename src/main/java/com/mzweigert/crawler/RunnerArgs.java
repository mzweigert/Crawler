package com.mzweigert.crawler;

import org.apache.commons.validator.routines.UrlValidator;

import java.util.List;

public class RunnerArgs {

    RunnerArgs(List<String> argList) {
        this.parseArgs(argList);
    }

    private int maxDepth;
    private String url;
    private boolean parsedStatus;

    int getMaxDepth() {
        return maxDepth;
    }

    String getUrl() {
        return url;
    }

    boolean isSuccess() {
        return parsedStatus;
    }

    private void parseArgs(List<String> argList) {
        int i = 0;
        while (i < argList.size()) {
            String arg = argList.get(i);
            if (arg.equals("--url") || arg.equals("-u")) {
                if (tryInitUrl(argList, i)) {
                    i++;
                } else {
                    return;
                }
            } else if (arg.equals("--depth") || arg.equals("-d")) {
                if (tryInitDepth(argList, i)) {
                    i++;
                }
            } else {
                System.out.println(arg + " argument not recognized!");
            }
            i++;
        }
    }

    private boolean tryInitDepth(List<String> argList, int i) {
        String depth = null;
        boolean maxDepthInitialized = false;
        if (argList.size() >= i + 2) {
            depth = argList.get(i + 1);
        }
        if (depth == null) {
            System.out.println("No depth value found!");
        } else {
            try {
                maxDepth = Integer.parseInt(depth);
                maxDepthInitialized = true;
            } catch (NumberFormatException e) {
                System.out.println(depth + " must be a number between 0 and 100!");
            }
            if (maxDepth < 0 || maxDepth > 100) {
                maxDepth = 0;
                maxDepthInitialized = true;
                System.out.println(depth + " must be a number between 0 and 100!");
            }
        }
        return maxDepthInitialized;
    }

    private boolean tryInitUrl(List<String> argList, int i) {
        if (argList.size() >= i + 2) {
            url = argList.get(i + 1);
            parsedStatus = true;
        } else {
            System.out.println("No url value found!");
        }
        if (!UrlValidator.getInstance().isValid(url)) {
            parsedStatus = false;
            System.out.println("Url is invalid!");
        }
        return parsedStatus;
    }
}
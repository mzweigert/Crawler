package com.mzweigert.crawler.task;

import com.mzweigert.crawler.model.PageNode;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

import static org.assertj.core.api.Assertions.assertThat;

public class RecursiveCrawlerTest {

    private static ForkJoinPool forkJoinPool = new ForkJoinPool(16);

    @Test
    public void testInvokedManyTime() throws IOException {
        //GIVEN

        String url = "https://wiprodigital.com";

        //WHEN
        long startTime = System.currentTimeMillis();

        Set<PageNode> invokedFirst = forkJoinPool.invoke(new MultithreadingRecursiveCrawler(url));
        long endTime = System.currentTimeMillis();

        System.out.println("Crawling took " + (endTime - startTime) + " milliseconds");

        //THEN
        for(int i=0 ; i<3; i++){
            Set<PageNode> invokedAgain = forkJoinPool.invoke(new MultithreadingRecursiveCrawler(url));
            assertThat(invokedFirst).hasSize(invokedAgain.size());
            assertThat(invokedFirst).containsAll(invokedAgain);
        }
    }
}

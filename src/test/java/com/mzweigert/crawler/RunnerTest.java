package com.mzweigert.crawler;

import com.mzweigert.crawler.service.crawler.CrawlerService;
import com.mzweigert.crawler.service.serializer.FileSerializationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RunnerTest {

    @Mock
    private CrawlerService crawlerService;

    @Mock
    private FileSerializationService serializerService;

    @Test
    public void givenInvalidArgs_whenRun_thenNotRunningCrawl() {
        //GIVEN
        RunnerArgs args = generateArgs("-u", "invalid_url");
        Runner runner = new Runner(args, crawlerService, serializerService);

        //WHEN
        runner.run();

        //THEN
        verifyZeroInteractions(crawlerService);
        verifyZeroInteractions(serializerService);
    }

    @Test
    public void givenArgsWithUrl_whenRun_thenNotRunningCrawl() {
        //GIVEN
        RunnerArgs args = generateArgs("-u", "http://www.example.com");
        Runner runner = new Runner(args, crawlerService, serializerService);

        //WHEN
        runner.run();

        //THEN
        verify(crawlerService).crawl(any());
        verify(serializerService).serialize(any(), anyList());
    }

    @Test
    public void givenArgsWithUrlAndDepth_whenRun_thenNotRunningCrawl() {
        //GIVEN
        RunnerArgs args = generateArgs("-u", "http://www.example.com", "-d", "15");
        Runner runner = new Runner(args, crawlerService, serializerService);

        //WHEN
        runner.run();

        //THEN
        verify(crawlerService).crawl(any());
        verify(serializerService).serialize(any(), anyList());
    }

    @Test
    public void givenArgsWithUrlAndGrouped_whenRun_thenNotRunningCrawl() {
        //GIVEN
		RunnerArgs args = generateArgs("-u", "http://www.example.com", "-g");
        Runner runner = new Runner(args, crawlerService, serializerService);

        //WHEN
        runner.run();

        //THEN
        verify(crawlerService).crawl(any());
        verify(serializerService).serializeGrouped(any(), any(), anyList());
    }

    private RunnerArgs generateArgs(String... args) {
        return new RunnerArgs(Arrays.stream(args).collect(Collectors.toList()));
    }
}
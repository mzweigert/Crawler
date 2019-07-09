package com.mzweigert.crawler;

import com.mzweigert.crawler.service.CrawlerService;
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
    private CrawlerService service;

    @Test
    public void givenInvalidArgs_whenRun_thenNotRunningCrawl() {
        //GIVEN
        RunnerArgs args = generateArgs("-u", "invalid_url");
        Runner runner = new Runner(args, service);

        //WHEN
        runner.run();

        //THEN
        verifyZeroInteractions(service);
    }

    @Test
    public void givenArgsWithUrl_whenRun_thenNotRunningCrawl() {
        //GIVEN
        RunnerArgs args = generateArgs("-u", "http://www.example.com");
        Runner runner = new Runner(args, service);

        //WHEN
        runner.run();

        //THEN
        verify(service).crawl(any());
    }

    @Test
    public void givenArgsWithUrlAndDepth_whenRun_thenNotRunningCrawl() {
        //GIVEN
        RunnerArgs args = generateArgs("-u", "http://www.example.com", "-d", "15");
        Runner runner = new Runner(args, service);

        //WHEN
        runner.run();

        //THEN
        verify(service).crawl(any(), eq(15));
    }

    private RunnerArgs generateArgs(String... args) {
        return new RunnerArgs(Arrays.stream(args).collect(Collectors.toList()));
    }
}
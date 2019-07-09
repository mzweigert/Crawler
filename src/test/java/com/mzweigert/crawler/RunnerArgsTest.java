package com.mzweigert.crawler;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class RunnerArgsTest {

    @Test
    public void givenUrlArgWithCorrectUrl_whenInit_thenSuccessInit() {
        //GIVEN
        List<String> args = new ArrayList<>();
        String arg = "-u";
        String url = "http://example.com";
        args.add(arg);
        args.add(url);

        //WHEN
        RunnerArgs result = new RunnerArgs(args);

        //THEN
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getUrl()).isEqualTo(url);
        assertThat(result.getMaxDepth()).isZero();
    }

    @Test
    public void givenUrlArgWithIncorrectUrl_whenInit_thenFailedInit() {
        //GIVEN
        List<String> args = new ArrayList<>();
        String arg = "-u";
        String url = "incorrect_url";
        args.add(arg);
        args.add(url);

        //WHEN
        RunnerArgs result = new RunnerArgs(args);

        //THEN
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getUrl()).isEqualTo(url);
        assertThat(result.getMaxDepth()).isZero();
    }


    @Test
    public void givenUrlArgWithoutUrl_whenInit_thenFailedInit() {
        //GIVEN
        List<String> args = new ArrayList<>();
        String arg = "-u";
        args.add(arg);

        //WHEN
        RunnerArgs result = new RunnerArgs(args);

        //THEN
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getUrl()).isNull();
        assertThat(result.getMaxDepth()).isZero();
    }

    @Test
    public void givenUrlWithoutUrlArg_whenInit_thenFailedInit() {
        //GIVEN
        List<String> args = new ArrayList<>();
        String url = "http://example.com";
        args.add(url);

        //WHEN
        RunnerArgs result = new RunnerArgs(args);

        //THEN
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getUrl()).isNull();
        assertThat(result.getMaxDepth()).isZero();
    }


    @Test
    public void givenUrlWithUrlArgWithDepthWithDepthArg_whenInit_thenSuccessInit() {
        //GIVEN
        List<String> args = new ArrayList<>();
        String urlArg = "-u";
        String url = "http://example.com";
        String depthArg = "-d";
        String depth = "12";
        args.add(urlArg);
        args.add(url);
        args.add(depthArg);
        args.add(depth);

        //WHEN
        RunnerArgs result = new RunnerArgs(args);

        //THEN
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getUrl()).isEqualTo(url);
        assertThat(result.getMaxDepth()).isGreaterThan(0);
    }

    @Test
    public void givenDepthWithDepthArgWithUrlWithUrlArg_whenInit_thenSuccessInit() {
        //GIVEN
        List<String> args = new ArrayList<>();
        String urlArg = "-u";
        String url = "http://example.com";
        String depthArg = "-d";
        String depth = "13";
        args.add(depthArg);
        args.add(depth);
        args.add(urlArg);
        args.add(url);

        //WHEN
        RunnerArgs result = new RunnerArgs(args);

        //THEN
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getUrl()).isEqualTo(url);
        assertThat(result.getMaxDepth()).isGreaterThan(0);
    }

    @Test
    public void givenUrlWithUrlArgWithDepthWithoutDepthArg_whenInit_thenSuccessInit() {
        //GIVEN
        List<String> args = new ArrayList<>();
        String urlArg = "-u";
        String url = "http://example.com";
        String depth = "12";
        args.add(urlArg);
        args.add(url);
        args.add(depth);

        //WHEN
        RunnerArgs result = new RunnerArgs(args);

        //THEN
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getUrl()).isEqualTo(url);
        assertThat(result.getMaxDepth()).isZero();
    }

    @Test
    public void givenUrlWithUrlArgWithoutDepthWithDepthArg_whenInit_thenSuccessInit() {
        //GIVEN
        List<String> args = new ArrayList<>();
        String urlArg = "-u";
        String url = "http://example.com";
        String depthArg = "-d";
        args.add(urlArg);
        args.add(url);
        args.add(depthArg);


        //WHEN
        RunnerArgs result = new RunnerArgs(args);

        //THEN
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getUrl()).isEqualTo(url);
        assertThat(result.getMaxDepth()).isZero();
    }

    @Test
    public void givenUrlWithUrlArgWithIncorrectDepthWithDepthArg_whenInit_thenSuccessInit() {
        //GIVEN
        List<String> args = new ArrayList<>();
        String urlArg = "-u";
        String url = "http://example.com";
        String depthArg = "-d";
        String depth = "incorrect_depth";
        args.add(urlArg);
        args.add(url);
        args.add(depthArg);
        args.add(depth);

        //WHEN
        RunnerArgs result = new RunnerArgs(args);

        //THEN
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getUrl()).isEqualTo(url);
        assertThat(result.getMaxDepth()).isZero();
    }

    @Test
    public void givenUrlWithUrlArgWithDepthLessThanZeroWithDepthArg_whenInit_thenSuccessInit() {
        //GIVEN
        List<String> args = new ArrayList<>();
        String urlArg = "-u";
        String url = "http://example.com";
        String depthArg = "-d";
        String depth = "-123";
        args.add(urlArg);
        args.add(url);
        args.add(depthArg);
        args.add(depth);

        //WHEN
        RunnerArgs result = new RunnerArgs(args);

        //THEN
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getUrl()).isEqualTo(url);
        assertThat(result.getMaxDepth()).isZero();
    }

    @Test
    public void givenUrlWithUrlArgWithDepthLessThanOneHundredWithDepthArg_whenInit_thenSuccessInit() {
        //GIVEN
        List<String> args = new ArrayList<>();
        String urlArg = "-u";
        String url = "http://example.com";
        String depthArg = "-d";
        String depth = "123";
        args.add(urlArg);
        args.add(url);
        args.add(depthArg);
        args.add(depth);

        //WHEN
        RunnerArgs result = new RunnerArgs(args);

        //THEN
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getUrl()).isEqualTo(url);
        assertThat(result.getMaxDepth()).isZero();
    }
}
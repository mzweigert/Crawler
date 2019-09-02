package com.mzweigert.crawler.configuration;

public class ConfigurationNotFoundException extends RuntimeException {
	public ConfigurationNotFoundException(String reason) {
		super(reason);
	}
}
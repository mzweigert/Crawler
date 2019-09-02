package com.mzweigert.crawler.service.crawler;

import com.mzweigert.crawler.configuration.Configuration;

public class CrawlerArgs {

	private CrawlerArgs() {}

	private String startUrl;

	private int maxDepth;

	private int documentsPerThread;

	public static Start initBuilder() {
		return new Builder();
	}

	public String getStartUrl() {
		return startUrl;
	}

	public int getMaxDepth() {
		return maxDepth;
	}

	public int getDocumentsPerThread() {
		return documentsPerThread;
	}

	private static class Builder implements Start, Build {

		private CrawlerArgs instance = new CrawlerArgs();

		@Override
		public Build withStartUrl(String url) {
			this.instance.startUrl = url;
			return this;
		}

		@Override
		public CrawlerArgs withAll(String url, int maxDepth, int documentsPerThread) {
			this.instance.startUrl = url;
			this.instance.maxDepth = maxDepth;
			this.instance.documentsPerThread = documentsPerThread;
			return this.instance;
		}

		@Override
		public Build withMaxDepth(int maxDepth) {
			this.instance.maxDepth = maxDepth;
			return this;
		}

		@Override
		public Build withDocumentsPerThread(int documentsPerThread) {
			this.instance.documentsPerThread = documentsPerThread;
			return this;
		}

		@Override
		public CrawlerArgs build() {
			if (this.instance.maxDepth <= 0) {
				this.instance.maxDepth = Integer.valueOf(Configuration.getProperty("max_depth"));
			}
			if (this.instance.documentsPerThread <= 0) {
				this.instance.documentsPerThread = Integer.valueOf(Configuration.getProperty("documents_per_thread"));
			}
			return instance;
		}
	}

	public interface Start {
		Build withStartUrl(String url);
		CrawlerArgs withAll(String url, int maxDepth, int documentsPerThread);
	}

	public interface Build {
		Build withMaxDepth(int maxDepth);
		Build withDocumentsPerThread(int documentsPerThread);
		CrawlerArgs build();
	}
}

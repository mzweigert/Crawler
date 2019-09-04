package com.mzweigert.crawler.service.crawler;

import com.mzweigert.crawler.configuration.Configuration;

import java.util.List;
import java.util.Objects;

public class CrawlerArgs {

	private String startUrl;
	private Integer maxDepth;
	private Integer documentsPerThread;
	private List<String> additionSelectors;

	private CrawlerArgs() {
	}

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

	public List<String> getAdditionSelectors() {
		return additionSelectors;
	}

	public String[] getAdditionSelectorsAsArray() {
		return additionSelectors == null ? new String[0] : additionSelectors.toArray(new String[0]);
	}

	public interface Start {
		Build withStartUrl(String url);

		CrawlerArgs withAll(String url, int maxDepth, int documentsPerThread);
	}

	public interface Build {
		Build withMaxDepth(int maxDepth);

		Build withDocumentsPerThread(int documentsPerThread);

		Build withSelectors(List<String> selectors);

		CrawlerArgs build();
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
		public Build withSelectors(List<String> selectors) {
			this.instance.additionSelectors = selectors;
			return this;
		}

		@Override
		public CrawlerArgs build() {
			if (this.instance.maxDepth == null) {
				this.instance.maxDepth = Integer.valueOf(Configuration.getProperty("max_depth"));
			}
			if (Objects.isNull(this.instance.documentsPerThread) || this.instance.documentsPerThread <= 0) {
				this.instance.documentsPerThread = Integer.valueOf(Configuration.getProperty("documents_per_thread"));
			}
			return instance;
		}
	}
}

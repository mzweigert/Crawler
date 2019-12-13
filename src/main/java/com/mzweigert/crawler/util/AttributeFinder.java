package com.mzweigert.crawler.util;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AttributeFinder {

	private final Document document;

	private AttributeFinder(Document document) {
		this.document = document;
	}

	public static AttributeFinder getInstance(Document document) {
		return new AttributeFinder(document);
	}

	public Set<String> find(Set<String> visitedBefore, String... selectors) {
		if (document == null) {
			return new HashSet<>();
		}
		return getElements(selectors)
				.select("a[href], [src]")
				.stream()
				.map(this::extractAttribute)
				.filter(this::isValid)
				.filter(node -> !visitedBefore.contains(node))
				.collect(Collectors.toSet());
	}

	private Elements getElements(String... selectors) {
		Elements elements;
		if (selectors.length > 0) {
			String selector = String.join(", ", selectors).trim();
			elements = document.select(selector);
		} else {
			elements = document.getAllElements();
		}
		return elements;
	}

	private String extractAttribute(Element element) {
		String link = element.attr("href");
		if (link == null || link.isEmpty()) {
			link = element.attr("src");
		}
		return link;
	}

	private boolean isValid(String link) {
		return link.length() > 1 && link.lastIndexOf("#") < 0;
	}

}

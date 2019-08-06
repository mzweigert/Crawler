package com.mzweigert.crawler;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.mzweigert.crawler.model.link.PageLinkType;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class FakeServerCreator {

	private WireMockConfiguration configuration;

	public FakeServerCreator(int port) {
		this.configuration = WireMockConfiguration.options().port(port);
	}

	public WireMockServer createBinaryTreeStructureWebsite(int depth) {
		return create(2, depth);
	}

	public WireMockServer create(int size, int depth) {
		WireMockServer wireMockServer = new WireMockServer(configuration);
		wireMockServer.start();
		configureFor(wireMockServer.port());
		stubFor(get(urlEqualTo("/"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "text/html; charset=utf-8")
						.withBody(createPages(size, depth, ""))));

		return wireMockServer;
	}

	private String createPages(int size, int depth, String parentPath) {
		StringBuilder body = new StringBuilder("<!DOCTYPE html><html><body>\n");
		for (int i = 1; i <= size && depth - 1 > 0; i++) {
			String pagePath = String.format(parentPath + "/page_%s", i);
			String link = String.format("<a href=\"" + pagePath + "\" >link_%s</a>\n", i);
			String pages = createPages(size, depth - 1, pagePath);
			stubFor(get(urlEqualTo(pagePath))
					.willReturn(aResponse()
							.withStatus(200)
							.withHeader("Content-Type", "text/html; charset=utf-8")
							.withBody(pages)));
			body.append(link);
		}
		body.append("</body></html>");
		return body.toString();
	}

	public WireMockServer createFlatStructureWebsiteWithAllResources() {
		WireMockServer wireMockServer = new WireMockServer(configuration);
		wireMockServer.start();
		StringBuilder body = new StringBuilder("<!DOCTYPE html><html><body>\n");
		for (PageLinkType type : PageLinkType.values()) {
			body.append(generateLinkByType(type));
		}
		body.append("</body></html>");

		stubFor(get(urlEqualTo("/"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "text/html; charset=utf-8")
						.withBody(body.toString())));

		return wireMockServer;
	}

	private String generateLinkByType(PageLinkType type) {
		String link = "<a href=\"%s\" >link_%s</a>\n";
		switch (type) {
			case INTERNAL_SUB_DOMAIN:
				String subDomainLink = String.format(link, "/sub_domain", "link to sub domain");
				stubFor(get(urlEqualTo("/sub_domain"))
						.willReturn(aResponse().withStatus(200)
								.withHeader("Content-Type", "text/html; charset=utf-8")
								.withBody("<!DOCTYPE html><html><body></body></html>")));
				return subDomainLink;
			case INVALID_LINK:
				return String.format(link, "/? invalid ?", "invalid link");
			case INTERNAL_ROOT_DOMAIN:
				return String.format(link, "/", "internal root domain link");
			case EXTERNAL_DOMAIN:
				return String.format(link, "http://www.google.com", "external domain link");
			case INTERNAL_RESOURCES:
				return String.format(link, "/resource_file.js", "internal resources link");
			case EXTERNAL_RESOURCES:
				return String.format(link, "http://www.google.com/resource_file.js", "internal root domain link");
		}

		return "";
	}
}

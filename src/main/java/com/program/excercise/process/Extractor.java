package com.program.excercise.process;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.program.exercise.constants.Constants;

/**
 * @author Jayashree
 *
 */
public class Extractor {

	private static final Logger log = LoggerFactory.getLogger(Extractor.class);

	private HashSet<String> links;
	private HashSet<HashSet<String>> articles;

	public Extractor() {
		links = new HashSet<>();
		articles = new HashSet<>();
	}

	/**
	 * 1)check if URL doesn't exist in set and depth < maxdepth
	 * 2)validate url,if valid then
	 * 3)get document of url and links present based on selector
	 * 4)recursively do for all urls from above
	 * @param url
	 * @param depth
	 * @return
	 */
	public boolean getPageLinks(String url, int depth) {
		Elements linksOnPage = null;
		if (!links.contains(url) && (depth < Constants.MAX_DEPTH)) {
			try {
				if (isValid(url)) {
					log.debug("Valid url to get Document");
					Document document = Jsoup.connect(url).userAgent(Constants.USER_AGENT).referrer(Constants.REFER)
							.get();
					linksOnPage = document.select(Constants.LINK_SELECTOR);
					depth++;
					for (Element page : linksOnPage) {
						getPageLinks(page.attr(Constants.ATTR_SELECTOR), depth);
					}
					links.add(url);
				}
			} catch (Exception e) {
				return true;
			}

		}
		return false;
	}

	/**
	 * 1)iterate links and get the document 2)get all elements from the document
	 * 3)filter elements based on regex 4)if present , add to set
	 * 
	 * @param textSearch
	 */
	public void getArticles(String textSearch) {
		if (!links.isEmpty()) {
			links.forEach(link -> {
				Document document;
				try {
					document = Jsoup.connect(link).userAgent(Constants.USER_AGENT).referrer(Constants.REFER).get();
					Elements elements = document.getAllElements();
					elements.parallelStream().filter(element -> element.hasText()).forEach(element -> {
						if (element.text().matches("^.*?(" + textSearch + ").*$")) {
							HashSet<String> temporary = new HashSet<>();
							temporary.add(link);
							articles.add(temporary);
						}
					});
				} catch (IOException e) {
					log.error("Exception occured in getArticles() for link >>" + link, e.getMessage());
					e.printStackTrace();
				}
			});
			log.info("Total no. of articles containing search Text {}", articles.size());
		}else {
			log.info("Links are not available");
		}
		
	}

	/**
	 * 1)iterate set of articles containing links 2)write the value to file
	 * 
	 * @param filename
	 */
	public void writeToFile(String filename) {
		FileWriter writer;
		try {
			if (!articles.isEmpty()) {
				writer = new FileWriter(filename);
				articles.forEach(article -> {
					try {
						article.forEach(link -> {
							String temp = " (link: " + link + ")\n";
							// save to file
							try {
								writer.write(temp);
							} catch (IOException e) {
								log.error(e.getMessage());
								e.printStackTrace();
							}
						});

					} catch (Exception e) {
						log.error(e.getMessage());
					}
				});
				writer.close();
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * validate URL by creating URL object
	 * 
	 * @param url
	 * @return
	 */
	public boolean isValid(String url) {
		try {
			new URL(url).toURI();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}

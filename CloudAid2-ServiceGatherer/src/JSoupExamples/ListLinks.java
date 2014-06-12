package JSoupExamples;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Example program to list links from a URL.
 */
public class ListLinks {
	private static ArrayList<String> links = new ArrayList<String>();
    public static void main(String[] args) throws IOException {
		String url = "http://cloud-computing.findthebest.com";
		print("Fetching %s...", url);

		Document doc = Jsoup.connect(url).userAgent("Chrome")
				.referrer("http://www.google.com").get();

		Elements table = doc.select("table");
		Elements rows = table.select("tr");
		for (Element row : rows) {
			Elements tds = row.select("td");
			Elements link = tds.select("[href]");
			for (Element el : link) {
				if (!el.attr("href").contains(url))
				{
					if (!links.contains(url + el.attr("href")))
						links.add(url + el.attr("href"));
				}
				else {
					if (!links.contains(el.attr("href")))
						links.add(el.attr("href"));
				}
			}
		}
		
		
		for(String l : links)
			System.out.println(l);
    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

}

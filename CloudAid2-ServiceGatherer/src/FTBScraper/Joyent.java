package FTBScraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

public class Joyent {

	public Joyent() {
	}

	public void parse() throws IOException {
		String query = "";
		String url = "http://cloud-computing.findthebest.com/l/6/Joyent-Inc";
		print("Fetching %s...", url);

		Document doc = Jsoup.connect(url).userAgent("Chrome")
				.referrer("http://www.google.com").get();

		query = "#detail-sections > div.id-7.detail-section.linkable.perma-linkable > div > div > div.detail-split-right > div > table > tbody > tr:nth-child(1) > td.fdata > a > span";

		Elements divs = doc.select(query);
		System.out.println("Provider - " + divs.get(0).text());
		
		divs = doc.select("#detail-sections > div.id-7.detail-section.linkable.perma-linkable > div > div > div.detail-split-right > div > table > tbody > tr:nth-child(2) > td.fdata");
		System.out.println("Year Founded - " + divs.get(0).text());
		
		divs = doc.select("#detail-sections > div.id-7.detail-section.linkable.perma-linkable > div > div > div.detail-split-right > div > table > tbody > tr.component.two-column.fieldediturl.odd > td.fdata > a");
		System.out.println("Website: " + divs.attr("href"));

		divs = doc.select("#rep_access_program");
		System.out.println("\nAccess control:");
		for (Element e : divs.select("tr"))
			System.out.println(e.text());

		divs = doc.select("#rep_support");
		System.out.println("\nSupport services:");
		for (Element e : divs.select("tr"))
			System.out.println(e.text());

		try {
			Thread.sleep(3000);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

	private void print(String msg, Object... args) {
		System.out.println(String.format(msg, args));
	}

}

package FTBScraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

public class Lunacloud {

	public Lunacloud() {
	}

	public void parse() throws IOException {
		String query = "";
		String url = "http://cloud-computing.findthebest.com/l/150/Lunacloud";
		print("Fetching %s...", url);

		Document doc = Jsoup.connect(url).userAgent("Chrome")
				.referrer("http://www.google.com").get();

		query = "#detail-sections > div.id-7.detail-section.linkable.perma-linkable > div > div > div > table > tbody > tr.component.two-column.fieldedit.even > td.fdata > b";

		Elements divs = doc.select(query);
		System.out.println("Provider - " + divs.get(0).text());

		divs = doc
				.select("#detail-sections > div.id-7.detail-section.linkable.perma-linkable > div > div > div > table > tbody > tr.component.two-column.fieldediturl.odd > td.fdata > a");
		
		System.out.println("Website: " + divs.attr("href"));

		divs = doc.select("#rep_access_program");
		System.out.println("\nAccess control:");
		for (Element e : divs.select("tr"))
			System.out.println(e.text());
		// System.out.println(doc);
		divs = doc.select("#rep_freesecurity");
		System.out.println("\nFree Security Features:");
		for (Element e : divs.select("tr"))
			System.out.println(e.text());

		divs = doc
				.select("#detail-sections > div.id-1.detail-section.open.linkable.perma-linkable > div > div.split-2.detail-col.split-row.clearfix > div.detail-split-right > div > table > tbody > tr.component.two-column.first.fieldeditnumber.odd > td.fdata");
		System.out.println("\nAvailability - " + divs.get(0).text());

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

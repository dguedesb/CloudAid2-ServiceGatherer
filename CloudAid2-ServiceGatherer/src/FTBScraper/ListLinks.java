package FTBScraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Example program to list links from a URL.
 */

public class ListLinks {
	private static ArrayList<String> links = new ArrayList<String>();
	
    public ListLinks() throws IOException {
		@SuppressWarnings("resource")
		String content = new Scanner(new File("C://Users//daniel//Desktop//er.html")).useDelimiter("\\Z").next();
		
		//Document doc = Jsoup.connect(url).userAgent("Chrome").referrer("http://www.google.com").get();
		Document doc = Jsoup.parse(content);
		Elements table = doc.select("table[id=srp-results]");
		Elements rows = table.select("tr");
		for (Element row : rows) {
			if(row.hasAttr("data-href"))
				links.add(row.attr("data-href"));
		}
    }
    
    public ArrayList<String>getLinks()
    {
    	return links;
    }
    
    public void printLinks()
    {
    	int k=0;
		for(String s : links)
			System.out.println("["+ ++k +"]"+s);
    }
}

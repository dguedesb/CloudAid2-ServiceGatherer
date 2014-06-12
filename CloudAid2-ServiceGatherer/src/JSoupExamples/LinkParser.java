package JSoupExamples;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * Example program to list links from a URL.
 */
public class LinkParser {
    public static void main(String[] args) throws IOException {
		String url = "http://cloud-computing.findthebest.com/l/150/Lunacloud";
		print("Fetching %s...", url);

		Document doc = Jsoup.connect(url).userAgent("Chrome")
				.referrer("http://www.google.com").get();
		
		Elements divs = doc.select("div[data-section-id=2]");
		for(Element div : divs)
		{
			System.out.println(div + "\n*******************************\n");
		}
		//System.out.println(doc);
		PrintWriter outb = new PrintWriter("filename.txt");
		outb.println(doc.toString());
		outb.close();
    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

}

package GScraper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class GScraper {

	// Recurring Resource Pooling
	// PrePaid Credit
	// PrePaid Subscription Credit
	// On-Demand
	// Spot Pring
	// Reserved Instance
	// PrePaid VM
	// Recurring PrePaid VM
	private static final Map<String, Integer> rrp;
	private static final Map<String, Integer> ppc;
	private static final Map<String, Integer> ppsc;
	private static final Map<String, Integer> od;
	private static final Map<String, Integer> sp;
	private static final Map<String, Integer> ri;
	private static final Map<String, Integer> ppvm;
	private static final Map<String, Integer> rppvm;
	private static ArrayList<HashMap<String, Integer>> pricingMethods;
	static {
		pricingMethods = new ArrayList<HashMap<String, Integer>>();

		rrp = new HashMap<String, Integer>();
		rrp.put("%22Bluelock%22+%22Virtual+Data+Center%22", 0);
		rrp.put("%22Carrenza%22+%22cloud%22", 0);
		rrp.put("%22colt%22+%22IaaS/Flexible+vCloud%22", 0);
		rrp.put("%22iLand%22+%22Cloud%22", 0);
		rrp.put("%22Windstream%22+%22Public+Cloud%22", 0);
		rrp.put("%22Peak+10%22+%22Enterprise+Cloud%22", 0);
		rrp.put("%22IBM%22+%22SmartCloud+Enterprise%22", 0);
		rrp.put("%22ElasticHosts%22+%22Cloud+Servers%22", 0);
		rrp.put("%22VMWare%22+%22vCloud+Hybrid%22", 0);
		rrp.put("%22GDS+Services%22+%22HiA+Cloud%22", 0);
		rrp.put("%22Cloudsigma%22+%22Cloud%22", 0);
		rrp.put("%22Orange+Business+Services%22+%22Flexible+Computing%22", 0);
		rrp.put("%22LeaseWeb%22+%22Virtual+Servers%22", 0);
		rrp.put("%22GoGrid%22+%22Cloud+Servers%22", 0);
		rrp.put("%22NTT+Communications%22+%22Enterprise+Cloud%22", 0);
		rrp.put("%22CSC%22+%22BizCloud%22", 0);
		rrp.put("%22Claranet%22+%22virtual+datacenter%22", 0);
		rrp.put("%22Singtel%22+%22PowerON+Compute%22", 0);
		pricingMethods.add((HashMap<String, Integer>) rrp);

		ppc = new HashMap<String, Integer>();
		ppc.put("%22Gandi%22+%22Cloud+Servers%22", 0);
		ppc.put("%22Internap%22+%22Public+Cloud%22", 0);
		ppc.put("%22LunaCloud%22+%22Cloud+Servers%22", 0);
		ppc.put("%22ElasticHosts%22+%22Cloud+Servers%22", 0);
		ppc.put("%22Microsoft%22+%22Azure+Virtual+Machines%22", 0);
		pricingMethods.add((HashMap<String, Integer>) ppc);

		ppsc = new HashMap<String, Integer>();
		ppsc.put("%22Internap%22+%22Public+Cloud%22", 0);
		ppsc.put("%22LunaCloud%22+%22Cloud+Server%22", 0);
		ppsc.put("%22GoGrid%22+%22Cloud+Servers%22", 0);
		ppsc.put("%22ElasticHosts%22+%22Cloud+Servers%22", 0);
		ppsc.put("%22CloudSigma%22+%22Cloud%22", 0);
		ppsc.put("%22Microsoft%22+%22Azure+Virual+Machines%22", 0);
		pricingMethods.add((HashMap<String, Integer>) ppsc);

		od = new HashMap<String, Integer>();
		od.put("%22IIJ%22+%22GIO%22", 0);
		od.put("%22Carrenza%22+%22Cloud%22", 0);
		od.put("%22Internap%22+%22Public+Cloud%22", 0);
		od.put("%22NTT+Communications%22+%22Enterprise+Cloud%22", 0);
		od.put("%22GoGrid%22+%22Cloud+Servers%22", 0);
		od.put("%22Hewlett+Packard%22+%22Cloud+Compute%22", 0);
		od.put("%22Markley+Group%22+%22Public+Cloud%22", 0);
		od.put("%22Bluelock%22+%22Virtual+DataCenter%22", 0);
		od.put("%22LunaCloud%22+%22Cloud+Server%22", 0);
		od.put("%22Tier+3%22+%22Virtual+Servers%22", 0);
		od.put("%22Dimension+Data%22+%22Public+CaaS%22", 0);
		od.put("%22IDC+Frontier%22+%22Cloud%22", 0);
		od.put("%22XO+Communications%22+%22Compute%22", 0);
		od.put("%22Amazon%22+%22EC2%22", 0);
		od.put("%22IBM%22+%22SmartCloud+Enterprise%22", 0);
		od.put("%22Windstream%22+%22Public+Cloud%22", 0);
		od.put("%22AT+T%22+%22Synaptic%22", 0);
		od.put("%22Colt%22+%22IaaS/Flexible+vCloud%22", 0);
		od.put("%22Fujitsu%22+%22IaaS+Public%22", 0);
		od.put("%22Verizon+Terremark%22+%22vCloud+Hybrid%22", 0);
		od.put("%22CenturyLink%22+%22Savvis%22+%22Cloud+Servers%22", 0);
		od.put("%22Google%22+%22Compute+Engine%22", 0);
		od.put("%22Arsys%22+%22CloudBuilder%22", 0);
		od.put("%22GDS+Services%22+%22HiA+Cloud%22", 0);
		od.put("%22Calligo%22+%22CloudCore%22", 0);
		od.put("%22Atlantic.net%22+%22Cloud+Servers%22", 0);
		od.put("%22Orange+Business+Services%22+%22Flexible+Computing%22", 0);
		od.put("%22Digital+Ribbon%22+%22Hybrid+Cloud%22", 0);
		od.put("%22CSC%22+%22BizCloud%22", 0);
		od.put("%22Joyent%22+%22Compute%22", 0);
		od.put("%22Singtel%22+%22PowerON%22", 0);
		od.put("%22Microsft%22+%22Azure+Virtual+Machines%22", 0);
		od.put("%22Rackspace%22+%22CloudServers%22", 0);
		od.put("%22LeaseWeb%22+%22Virtual+Servers%22", 0);
		od.put("%22Greenclouds%22+%22IaaS%22", 0);
		od.put("%22SoftLayer%22+%22CloudLayer%22", 0);
		od.put("%22Claranet%22+%22Virtual+DataCenter%22", 0);
		od.put("%22Swisscom%22+%22Dynamic+Server/DC%22", 0);
		od.put("%22ProfitBricks%22+%22Cloud%22", 0);
		od.put("%22iLand%22+%22Cloud%22", 0);
		pricingMethods.add((HashMap<String, Integer>) od);

		sp = new HashMap<String, Integer>();
		sp.put("%22Amazon%22+%22EC2%22", 0);
		sp.put("%22CloudSigma%22+%22Cloud%22", 0);
		pricingMethods.add((HashMap<String, Integer>) sp);

		ri = new HashMap<String, Integer>();
		ri.put("%22Colt%22+%22IaaS/Flexible+vCloud%22", 0);
		ri.put("%22Amazon%22+%22EC2%22", 0);
		ri.put("%22Fujitsu%22+%22IaaS+Public%22", 0);
		ri.put("%22Joyent%22+%22Compute%22", 0);
		ri.put("%22CSC%22+%22BizCloud%22", 0);
		pricingMethods.add((HashMap<String, Integer>) ri);

		ppvm = new HashMap<String, Integer>();
		ppvm.put("%22GoGrid%22+%22Cloud+Servers%22", 0);
		ppvm.put("%22Peak+10%22+%22Enterprise+Cloud%22", 0);
		ppvm.put("%22IDC%22+%22Frontier+CLoud%22", 0);
		pricingMethods.add((HashMap<String, Integer>) ppvm);

		rppvm = new HashMap<String, Integer>();
		rppvm.put("%22IIJ%22+%22GIO%22", 0);
		rppvm.put("%22XO+Communications%22+%22Compute%22", 0);
		rppvm.put("%22Windstream%22+%22Public+Cloud%22", 0);
		rppvm.put("%22Gandi%22+%22Cloud+Servers%22", 0);
		rppvm.put("%22Markley+Group%22+%22Public+Cloud%22", 0);
		rppvm.put("%22Peak+10%22+%22Enterprise+Cloud%22", 0);
		rppvm.put("%22IDC+Frontier%22+%22Cloud%22", 0);
		rppvm.put("%22Hosting.com%22+%22Cloud+Hosting%22", 0);
		rppvm.put("%22Arsys%22+%22CloudBuilder%22", 0);
		rppvm.put("%22CenturyLink%22+%22Savvis%22+%22Cloud+Servers%22", 0);
		rppvm.put("%22Bit+Refinery%22+%22Cloud+Hosting%22", 0);
		rppvm.put("%22KDDI%22+%22Virtual+DC%22", 0);
		rppvm.put("%22Numergy%22+%22Cloud%22", 0);
		rppvm.put("%22UK2+Group%22+%22Web+Hosting%22", 0);
		rppvm.put("%22Connectria%22+%22Cloud+Servers%22", 0);
		rppvm.put("%22SoftLayer%22+%22CloudLayer%22", 0);
		rppvm.put("%22CloudCentral%22+%22Compute%22", 0);
		rppvm.put("%22Colt%22+%22IaaS/Flexible+vCloud%22", 0);
		rppvm.put("%22Cyberindo%22+%22IaaS%22", 0);
		pricingMethods.add((HashMap<String, Integer>) rppvm);
	}

	public void parse() throws IOException, InterruptedException {
		String url = "https://www.google.com/search?q=";
		for (HashMap<String, Integer> map : pricingMethods) {
			for (String key : map.keySet()) {
				String searchURL = url + key;
				System.out.println("Fetching: " + searchURL);

				Document doc = Jsoup
						.connect(searchURL)
						.userAgent("Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36")
						.referrer("http://www.google.com").get();

				Elements div = doc.select("#resultStats");
				String[] temp = div.get(0).text().split(" ");
				
				
				System.out.println("" + (key.replaceAll("\\+", " ")).replaceAll("%22", "'") + " - "+ Integer.parseInt(temp[1].replaceAll("\\D+", "")));
				map.put(key, Integer.parseInt(temp[1].replaceAll("\\D+", "")));

				
				Thread.sleep(10000);
			}

//			try (PrintWriter out = new PrintWriter(
//					new BufferedWriter(new FileWriter(
//							"C:/Users/daniel/Desktop/results.txt", true)))) {
//
//				for (Entry<String, Integer> pair : entriesSortedByValues(map))
//					out.write("" + (pair.getKey().replaceAll("\\+", " ")).replaceAll("%22", "'")+ "             " + pair.getValue() + "\n");
//
//				out.write("******************************************************\n");
//				out.close();
//			} catch (IOException e) {
//				System.out.println("Got an error while writing to  the file..\n"+ e.toString());
//			}
			System.out.println("Finished a pricing method!");
		}
		System.out.println("Done!");
	}

	static <K, V extends Comparable<? super V>> List<Entry<K, V>> entriesSortedByValues(
			Map<K, V> map) {

		List<Entry<K, V>> sortedEntries = new ArrayList<Entry<K, V>>(
				map.entrySet());

		Collections.sort(sortedEntries, new Comparator<Entry<K, V>>() {
			@Override
			public int compare(Entry<K, V> e1, Entry<K, V> e2) {
				return e2.getValue().compareTo(e1.getValue());
			}
		});

		return sortedEntries;
	}

	public static void main(String[] args) {
		GScraper gs = new GScraper();
		try {
			gs.parse();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

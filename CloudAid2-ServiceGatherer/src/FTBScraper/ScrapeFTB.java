package FTBScraper;

import java.io.IOException;

public class ScrapeFTB {
	
	public static void main(String[] args)
	{
		Joyent s = new Joyent();
		try {
			s.parse();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

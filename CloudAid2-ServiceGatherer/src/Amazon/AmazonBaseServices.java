package Amazon;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import usdl.constants.enums.CLOUDEnum;
import usdl.servicemodel.QualitativeValue;
import usdl.servicemodel.QuantitativeValue;
import usdl.servicemodel.Service;

public class AmazonBaseServices {
	
	private static String tech_url = "https://aws.amazon.com/ec2/instance-types/";
	public ArrayList<Service> AmazonBServices() throws IOException
	{
		
		ArrayList<Service> basicServices = new ArrayList<Service>();
		
		//Document doc = Jsoup
			//	.connect(tech_url)
				//.userAgent("Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36")
				//.referrer("http://www.google.com").get();
		
		File in = new File("./HTMLCopies/amazon_instance_types.html");
		Document doc = org.jsoup.Jsoup.parse(in, null);
		
		/************************************************************/
		//Keep a copy on disk
		
		/*File file = new File("./HTMLCopies/amazon_instance_types.html");
		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(doc.toString());
		bw.close();*/
		/************************************************************/
		
		
		Element table = doc.select("#page-content > div > div > section > div.content.parsys > div:nth-child(37) > table").first();

		Iterator<Element> ite = table.select("tr").iterator();

		//ite.next(); // first one is the names, skip it
		ite.next();
		while(ite.hasNext())
		{
			Element ez = ite.next();
			if(ite.hasNext())
			{
				Iterator<Element> itempz = ez.children().iterator();
				Service s = new Service();
				Service s2 = null;
				int col=0;
				while(itempz.hasNext())//iterate over
				{
					if(col==0)
					{
						s.setComment(itempz.next().text());
						col++;
					}
					else if(col==1)
					{
						s.setName(itempz.next().text());
						col++;
					}
					else if(col==2)
					{
						String data = itempz.next().text();
						String[] dt=data.split("or");
						if(dt.length > 1)
						{
							s2 = new Service(s);
							QualitativeValue qv = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
							qv.addType(CLOUDEnum.BIT64.getConceptURI());
							qv.setHasLabel(dt[1]);
							s2.addQualitativeFeature(qv);
							
							QualitativeValue qvb = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
							qvb.addType(CLOUDEnum.BIT32.getConceptURI());
							qvb.setHasLabel(dt[0]);
							s.addQualitativeFeature(qvb);
						}
						else
						{
							if(dt[0].contains("64"))
							{
								QualitativeValue qv = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
								qv.addType(CLOUDEnum.BIT64.getConceptURI());
								qv.setHasLabel(dt[0]);
								s.addQualitativeFeature(qv);
							}
							else
							{
								QualitativeValue qvb = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
								qvb.addType(CLOUDEnum.BIT32.getConceptURI());
								qvb.setHasLabel(dt[0]);
								s.addQualitativeFeature(qvb);
							}
						}
						col++;
					}
					else if(col==3)
					{
						String val = itempz.next().text();
						QuantitativeValue qv = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
						qv.addType(CLOUDEnum.CPUCORES.getConceptURI());
						String[] dt = val.split("\\*");
						qv.setValue(Double.parseDouble(dt[0]));
						s.addQuantitativeFeature(qv);
						if(s2 != null)
							s2.addQuantitativeFeature(new QuantitativeValue(qv));
						
						col++;
					}
					else if(col==4)
					{
						String val = itempz.next().text();
						QuantitativeValue qv = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
						qv.addType(CLOUDEnum.CPUSPEED.getConceptURI());
						qv.setValue(Double.parseDouble(val));
						qv.setUnitOfMeasurement("ECU");
						s.addQuantitativeFeature(qv);
						if(s2 != null)
							s2.addQuantitativeFeature(new QuantitativeValue(qv));
						
						col++;
					}
					
					else if(col==5)
					{
						String val = itempz.next().text();
						QuantitativeValue qv = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
						qv.addType(CLOUDEnum.MEMORYSIZE.getConceptURI());
						qv.setValue(Double.parseDouble(val));
						qv.setUnitOfMeasurement("E34");//GB
						s.addQuantitativeFeature(qv);
						if(s2 != null)
							s2.addQuantitativeFeature(new QuantitativeValue(qv));
						
						col++;
					}
					
					else if(col==6)
					{
						String val = itempz.next().text();
						String[] dt = val.split("x");
						int n = Integer.parseInt(dt[0].replaceAll(" ", ""));
						
						String[] dt2=dt[1].trim().split(" ");
						if(dt2.length > 2)
						{
							QuantitativeValue DiskSize = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
							DiskSize.addType(CLOUDEnum.DISKSIZE.getConceptURI());
							DiskSize.setValue(n * Double.parseDouble(dt2[0]));
							DiskSize.setUnitOfMeasurement("E34");
							
							QualitativeValue StorageType = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
							StorageType.addType(CLOUDEnum.STORAGETYPE.getConceptURI());
							StorageType.setHasLabel(dt2[1]);
							s.addQuantitativeFeature(DiskSize);
							s.addQualitativeFeature(StorageType);
							
							if(s2 != null)
							{
								s2.addQuantitativeFeature(new QuantitativeValue(DiskSize));
								s2.addQualitativeFeature(new QualitativeValue(StorageType));
							}
						}
						else
						{
							QuantitativeValue DiskSize = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
							DiskSize.addType(CLOUDEnum.DISKSIZE.getConceptURI());
							dt2[0] = dt2[0].replaceAll(",", "");
							String[] dt3 = dt2[0].split("\\*");
							DiskSize.setValue(n * Double.parseDouble(dt3[0]));
							DiskSize.setUnitOfMeasurement("E34");
							
							QualitativeValue StorageType = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
							StorageType.addType(CLOUDEnum.STORAGETYPE.getConceptURI());
							StorageType.setHasLabel("HDD");
							s.addQuantitativeFeature(DiskSize);
							s.addQualitativeFeature(StorageType);
							
							if(s2 != null)
							{
								s2.addQuantitativeFeature(new QuantitativeValue(DiskSize));
								s2.addQualitativeFeature(new QualitativeValue(StorageType));
							}
						}
						col++;
					}
					
					else if(col==7)
					{
						String val = itempz.next().text();
						QualitativeValue qv = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
						qv.addType(CLOUDEnum.FEATURE.getConceptURI());
						qv.setComment("EBS-optimized Available");
						if(val.equals("-"))
						{
							qv.setHasLabel("No");
							s.addQualitativeFeature(qv);
						}
						else
						{
							qv.setHasLabel("Yes");
							s.addQualitativeFeature(qv);
						}
						
						if(s2 != null)
							s2.addQualitativeFeature(new QualitativeValue(qv));
						
						col++;
					}
					
					else if(col==8)
					{
						String val = itempz.next().text();
						QualitativeValue qv = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
						qv.addType(CLOUDEnum.PERFORMANCE.getConceptURI());
						qv.setHasLabel(val);
						s.addQualitativeFeature(qv);
						if(s2 != null)
							s2.addQualitativeFeature(new QualitativeValue(qv));
						
						col++;
					}
				}
				basicServices.add(s);
				if(s2 != null)
					basicServices.add(s2);
			}
		}
		
		
		Element tableb = doc.select("#page-content > div > div > section > div.content.parsys > div:nth-child(41) > table").first();

		Iterator<Element> iteb = tableb.select("tr").iterator();
		
		// ite.next(); // first one is the names, skip it
		iteb.next();
		while (iteb.hasNext()) {
			Element ez = iteb.next();
			if (iteb.hasNext()) {
				Iterator<Element> itempz = ez.children().iterator();
				itempz.next();
				String inst_name = itempz.next().text();
				itempz.next();itempz.next();itempz.next();
				String processor = itempz.next().text();
				for(Service s : basicServices)
				{
					if(s.getName().equals(inst_name))
					{
						QualitativeValue qv = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
						qv.addType(CLOUDEnum.CPUTYPE.getConceptURI());
						qv.setHasLabel(processor);
						s.addQualitativeFeature(qv);
					}
				}
			}
		}
		
		return basicServices;
	}
}

package Amazon;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import usdl.constants.enums.CLOUDEnum;
import usdl.servicemodel.QualitativeValue;
import usdl.servicemodel.QuantitativeValue;
import usdl.servicemodel.Service;

public class AmazonRDSBaseServices {
	private static String tech_url = "https://aws.amazon.com/rds/details/";
	
	public ArrayList<Service> baseServices() throws IOException
	{
		ArrayList<Service> services = new ArrayList<Service>();
		
//		Document doc = Jsoup
//			.connect(tech_url)
//			.userAgent("Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36")
//			.referrer("http://www.google.com").get();
		
		File in = new File("./HTMLCopies/amazonRDS_instance_types.html");
		Document doc = org.jsoup.Jsoup.parse(in, null);
		
		/************************************************************/
		//Keep a copy on disk
		
//		File file = new File("./HTMLCopies/amazonRDS_instance_types.html");
//		// if file doesnt exists, then create it
//		if (!file.exists()) {
//			file.createNewFile();
//		}
//
//		FileWriter fw = new FileWriter(file.getAbsoluteFile());
//		BufferedWriter bw = new BufferedWriter(fw);
//		bw.write(doc.toString());
//		bw.close();
		/************************************************************/
		
		//info extracted from https://aws.amazon.com/rds/mysql/,https://aws.amazon.com/rds/oracle/ and https://aws.amazon.com/rds/details/
		QuantitativeValue IO = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
		IO.addType(CLOUDEnum.IOOPERATIONS.getConceptURI());
		IO.setValue(30000);
		IO.setComment("I/O Operations per Second");
		
		QuantitativeValue StorageCapacity = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
		StorageCapacity.addType(CLOUDEnum.STORAGECAPACITY.getConceptURI());
		StorageCapacity.setUnitOfMeasurement("E34");//GB
		StorageCapacity.setValue(3000);
		
		QualitativeValue Recovery = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
		Recovery.addType(CLOUDEnum.BACKUP_RECOVERY.getConceptURI());
		Recovery.setHasLabel("Automatic Host Replacement");
		Recovery.setComment("Amazon RDS will automatically replace the compute instance powering your deployment in the event of a hardware failure.");
		
		QualitativeValue Monitoring = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
		Monitoring.addType(CLOUDEnum.MONITORING.getConceptURI());
		Monitoring.setHasLabel("Amazon CloudWatch");
		
		QualitativeValue Snapshot = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
		Snapshot.addType(CLOUDEnum.BACKUP_RECOVERY.getConceptURI());
		Snapshot.setHasLabel("DB Snapshots");
		Snapshot.setComment("DB Snapshots are user-initiated backups of your DB Instance. These full database backups will be stored by Amazon RDS until you explicitly delete them. You can create a new DB Instance from a DB Snapshot whenever you desire.");
		
		
		Element table = doc.select("#page-content > div > div > section > div.content.parsys > div.aws-table.section > table").first();

		Iterator<Element> ite = table.select("tr").iterator();

		ite.next(); // first one is the names, skip it
		ite.next(); // skip micro instances
//		ite.next();
		
		while(ite.hasNext())
		{
			Element ez = ite.next();
			if(ite.hasNext())//skip db.cr1.8xlarge
			{
				Iterator<Element> itempz = ez.children().iterator();
				Service s = new Service();
				s.addQualFeature(new QualitativeValue(Snapshot));
				s.addQualFeature(new QualitativeValue(Monitoring));
				s.addQualFeature(new QualitativeValue(Recovery));
				s.addQuantFeature(new QuantitativeValue(StorageCapacity));
				s.addQuantFeature(new QuantitativeValue(IO));
				int col=0;
				
				while(itempz.hasNext())//iterate over
				{
//					itempz.next();//skip first element of the column
					String data = itempz.next().text();
					if(col==0)
					{
						s.setComment(data);
						col++;
					}
					else if (col==1)
					{
						s.setName(data);
						col++;
					}
					else if(col==2)
					{
						QuantitativeValue qv = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
						qv.addType(CLOUDEnum.CPUCORES.getConceptURI());
						qv.setValue(Double.parseDouble(data));
						s.addQuantitativeFeature(qv);
						
						col++;
					}
					else if(col==3)
					{
						QuantitativeValue qv = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
						qv.addType(CLOUDEnum.CPUSPEED.getConceptURI());
						qv.setValue(Double.parseDouble(data));
						qv.setUnitOfMeasurement("ECU");
						s.addQuantitativeFeature(qv);
						
						col++;
					}
					else if(col==4)
					{
						QuantitativeValue qv = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
						qv.addType(CLOUDEnum.MEMORYSIZE.getConceptURI());
						qv.setValue(Double.parseDouble(data));
						qv.setUnitOfMeasurement("E34");//GB
						s.addQuantitativeFeature(qv);

						col++;
					}
					else if(col==5)
					{
						QualitativeValue qv = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
						qv.addType(CLOUDEnum.FEATURE.getConceptURI());
						if(data.equalsIgnoreCase("PIOPS Optimized - YES"))
							qv.setHasLabel(data);
						else
							qv.setHasLabel("PIOPS Optimized - NO");
						
						s.addQualFeature(qv);

						col++;
					}
					else if(col==6)
					{

						QualitativeValue qv = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
						qv.addType(CLOUDEnum.PERFORMANCE.getConceptURI());
						qv.setHasLabel(data);
						s.addQualitativeFeature(qv);
						
						col++;
					}
				}
				services.add(s);
			}
		}
		
		
		return services;
	}
	
//	public static void main(String[] args) throws IOException
//	{
//		AmazonRDSBaseServices db = new AmazonRDSBaseServices();
//		
//		ArrayList<Service> serv = db.baseServices();
//		
//		System.out.println(serv.size());
//	}
}

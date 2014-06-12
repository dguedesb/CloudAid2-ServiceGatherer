/**
 * @author Daniel Guedes Barrigas - danielgbarrigas@hotmail.com / danielgbarrigas@gmail.com
 * 
 * Uses the LinkedUSDL Pricing API to model Service Offerings provided by Arsys.
 * Info about their offerings can be seen at: http://www.arsys.net/servers/dedicated.html
 * The pricing method adopted by arsys is a fully bundled Recurring VM Access
 *
 */

package Arsys;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.hp.hpl.jena.rdf.model.Model;

import exceptions.InvalidLinkedUSDLModelException;
import usdl.servicemodel.*;
import usdl.constants.enums.*;
public class Arsys {
	public  void parse(LinkedUSDLModel jmodel) throws IOException
	{
		ArrayList<Offering> offs = new ArrayList<Offering>();
		int i;
		//first, create the services that we're going to populate with the scrapped information. The website presents the features of 4 different service offerings through several html table. 
		Service s1 = new Service();
		Service s2 = new Service();
		Service s3 = new Service();
		Service s4 = new Service();

		double s1price = 0,s2price=0,s3price=0,s4price=0;
		ArrayList<QuantitativeValue> s1QuantFeat = new ArrayList<QuantitativeValue>();
		ArrayList<QualitativeValue> s1QualFeat = new ArrayList<QualitativeValue>();

		ArrayList<QuantitativeValue> s2QuantFeat = new ArrayList<QuantitativeValue>();
		ArrayList<QualitativeValue> s2QualFeat = new ArrayList<QualitativeValue>();

		ArrayList<QuantitativeValue> s3QuantFeat = new ArrayList<QuantitativeValue>();
		ArrayList<QualitativeValue> s3QualFeat = new ArrayList<QualitativeValue>();

		ArrayList<QuantitativeValue> s4QuantFeat = new ArrayList<QuantitativeValue>();
		ArrayList<QualitativeValue> s4QualFeat = new ArrayList<QualitativeValue>();
		
		//Document doc = Jsoup
			//	.connect("http://www.arsys.net/servers/dedicated.html")
			//	.userAgent("Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36")
			//	.referrer("http://www.google.com").get();
		
		File in = new File("./HTMLCopies/arsys.html");
		Document doc = org.jsoup.Jsoup.parse(in, null);
		
		/************************************************************/
		//Keep a copy on disk
		
		/*File file = new File("./HTMLCopies/arsys.html");
		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(doc.toString());
		bw.close();*/
		/************************************************************/
		
		/////////////////////////////////////////////////////////////////// FIRST TABLE //////////////////////////////////////////////////////////////////
		Element table = doc.select("table[class=HostingChars]").first();

		Iterator<Element> ite = table.select("tr").iterator();

		//ite.next(); // first one is the names, skip it
		
		Element ez = ite.next();
		Iterator<Element> itempz = ez.children().iterator();
		itempz.next();//first is the name
		int counter = 0;
		while(itempz.hasNext())
		{
			String dataz = itempz.next().text();
			if(itempz.hasNext())//skip the last column, it describes their on demand service
			{
				String[] s =  dataz.split("/");
				String[] hp = s[0].split(" ");

				if (counter == 0) {
					s1.setName(hp[0] + hp[1] +"_TIME"+System.nanoTime());
					String t = hp[2].substring(1);
					s1price = Double.parseDouble(t);
					counter++;
				} else if (counter == 1) {
					s2.setName(hp[4] + hp[5] + "_TIME"+System.nanoTime());
					String t = hp[7].substring(1);
					s2price = Double.parseDouble(t);
					counter++;
				} else if (counter == 2) {
					s3.setName(hp[0] + hp[1] +  "_TIME"+System.nanoTime());
					String t = hp[2].substring(1);
					s3price = Double.parseDouble(t);
					counter++;
				} else if (counter == 3) {
					s4.setName(hp[0] + hp[1] + "_TIME"+System.nanoTime());
					String t = hp[2].substring(1);
					s4price = Double.parseDouble(t);
					counter++;
				}

			}
		}
		
		
		
		
		

		//1st line - DATAINEXTERNAL,DATAININTERNAL,DATAOUTEXTERNAL,DATAOUTINTERNAL
		String[] text = ite.next().text().split(" ");
		QuantitativeValue DATAINEXTERNAL;
		QuantitativeValue DATAININTERNAL;
		QuantitativeValue DATAOUTEXTERNAL;
		QuantitativeValue DATAOUTINTERNAL;
		if(text[2].equals("Unlimited"))//Data transferral s1
		{
			
			DATAINEXTERNAL = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
			DATAINEXTERNAL.addType(CLOUDEnum.DATAINEXTERNAL.getConceptURI());
			DATAINEXTERNAL.setValue(Double.MAX_VALUE);
			s1QuantFeat.add(DATAINEXTERNAL);
			
			DATAININTERNAL = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
			DATAININTERNAL.addType(CLOUDEnum.DATAININTERNAL.getConceptURI());
			DATAININTERNAL.setValue(Double.MAX_VALUE);
			s1QuantFeat.add(DATAININTERNAL);
			
			DATAOUTEXTERNAL = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
			DATAOUTEXTERNAL.addType(CLOUDEnum.DATAOUTEXTERNAL.getConceptURI());
			DATAOUTEXTERNAL.setValue(Double.MAX_VALUE);
			s1QuantFeat.add(DATAOUTEXTERNAL);
			
			DATAOUTINTERNAL = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
			DATAOUTINTERNAL.addType(CLOUDEnum.DATAOUTINTERNAL.getConceptURI());
			DATAOUTINTERNAL.setValue(Double.MAX_VALUE);
			s1QuantFeat.add(DATAOUTINTERNAL);
		}
		
		if(text[3].equals("Unlimited"))//Data transferral s2
		{
			
			DATAINEXTERNAL = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
			DATAINEXTERNAL.addType(CLOUDEnum.DATAINEXTERNAL.getConceptURI());
			DATAINEXTERNAL.setValue(Double.MAX_VALUE);
			s2QuantFeat.add(DATAINEXTERNAL);
			
			DATAININTERNAL = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
			DATAININTERNAL.addType(CLOUDEnum.DATAININTERNAL.getConceptURI());
			DATAININTERNAL.setValue(Double.MAX_VALUE);
			s2QuantFeat.add(DATAININTERNAL);
			
			DATAOUTEXTERNAL = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
			DATAOUTEXTERNAL.addType(CLOUDEnum.DATAOUTEXTERNAL.getConceptURI());
			DATAOUTEXTERNAL.setValue(Double.MAX_VALUE);
			s2QuantFeat.add(DATAOUTEXTERNAL);
			
			DATAOUTINTERNAL = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
			DATAOUTINTERNAL.addType(CLOUDEnum.DATAOUTINTERNAL.getConceptURI());
			DATAOUTINTERNAL.setValue(Double.MAX_VALUE);
			s2QuantFeat.add(DATAOUTINTERNAL);
			
		}
		
		if(text[4].equals("Unlimited"))//Data transferral s3
		{
			
			DATAINEXTERNAL = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
			DATAINEXTERNAL.addType(CLOUDEnum.DATAINEXTERNAL.getConceptURI());
			DATAINEXTERNAL.setValue(Double.MAX_VALUE);
			s3QuantFeat.add(DATAINEXTERNAL);
			
			DATAININTERNAL = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
			DATAININTERNAL.addType(CLOUDEnum.DATAININTERNAL.getConceptURI());
			DATAININTERNAL.setValue(Double.MAX_VALUE);
			s3QuantFeat.add(DATAININTERNAL);
			
			DATAOUTEXTERNAL = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
			DATAOUTEXTERNAL.addType(CLOUDEnum.DATAOUTEXTERNAL.getConceptURI());
			DATAOUTEXTERNAL.setValue(Double.MAX_VALUE);
			s3QuantFeat.add(DATAOUTEXTERNAL);
			
			DATAOUTINTERNAL = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
			DATAOUTINTERNAL.addType(CLOUDEnum.DATAOUTINTERNAL.getConceptURI());
			DATAOUTINTERNAL.setValue(Double.MAX_VALUE);
			s3QuantFeat.add(DATAOUTINTERNAL);
			
		}
		
		if(text[5].equals("Unlimited"))//Data transferral s4
		{
			
			DATAINEXTERNAL = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
			DATAINEXTERNAL.addType(CLOUDEnum.DATAINEXTERNAL.getConceptURI());
			DATAINEXTERNAL.setValue(Double.MAX_VALUE);
			s4QuantFeat.add(DATAINEXTERNAL);
			
			DATAININTERNAL = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
			DATAININTERNAL.addType(CLOUDEnum.DATAININTERNAL.getConceptURI());
			DATAININTERNAL.setValue(Double.MAX_VALUE);
			s4QuantFeat.add(DATAININTERNAL);
			
			DATAOUTEXTERNAL = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
			DATAOUTEXTERNAL.addType(CLOUDEnum.DATAOUTEXTERNAL.getConceptURI());
			DATAOUTEXTERNAL.setValue(Double.MAX_VALUE);
			s4QuantFeat.add(DATAOUTEXTERNAL);
			
			DATAOUTINTERNAL = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
			DATAOUTINTERNAL.addType(CLOUDEnum.DATAOUTINTERNAL.getConceptURI());
			DATAOUTINTERNAL.setValue(Double.MAX_VALUE);
			s4QuantFeat.add(DATAOUTINTERNAL);
			
		}


		//2nd line  - TRANSFERRATE
		QuantitativeValue TRANSFERRATE = null;
		text = ite.next().text().split(" ");
		for(i=1;i<text.length-2;i+=2)
		{
			
			if(isNumeric(text[i]))
			{
				TRANSFERRATE = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
				TRANSFERRATE.addType(CLOUDEnum.TRANSFERRATE.getConceptURI());
				TRANSFERRATE.setValue(Double.parseDouble(text[i]));
				if(text[i+1].equals("Mbps"))
					TRANSFERRATE.setUnitOfMeasurement("4L");
			}
			
			if(i==1)
				s1QuantFeat.add(TRANSFERRATE);
			else if(i==3)
				s2QuantFeat.add(TRANSFERRATE);
			else if(i==5)
				s3QuantFeat.add(TRANSFERRATE);
			else if(i==7)
				s4QuantFeat.add(TRANSFERRATE);
		}
		
		//3rd line, CPUCores CPUSpeed
		QuantitativeValue CPUCores=null,CPUSpeed=null;
		Element e = ite.next();
		Iterator<Element> itemp = e.children().iterator();
		itemp.next();//first is the name
		counter=0;
		while(itemp.hasNext())
		{
			String data = itemp.next().text();
			if(itemp.hasNext())//skip the last column, it describes their on demand service
			{
				String[] datasplit = data.split(" x ");
				CPUCores = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
				CPUCores.addType(CLOUDEnum.CPUCORES.getConceptURI());
				
				String[] dstem = datasplit[1].split(" ");

				CPUCores.setValue(Double.parseDouble(datasplit[0])  * Double.parseDouble(dstem[0]) );
				
				CPUSpeed = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
				CPUSpeed.addType(CLOUDEnum.CPUSPEED.getConceptURI());
				
				String[] dstem2 = datasplit[2].split(" ");
				CPUSpeed.setValue(Double.parseDouble(dstem2[0]));
				if(dstem2[1].equals("GHz"))
					CPUSpeed.setUnitOfMeasurement("A86");
				
				if(counter == 0)
				{
					s1QuantFeat.add(CPUCores);
					s1QuantFeat.add(CPUSpeed);
					counter++;
				}
				else if(counter == 1)
				{
					s2QuantFeat.add(CPUCores);
					s2QuantFeat.add(CPUSpeed);
					counter++;
				}
				else if(counter == 2)
				{
					s3QuantFeat.add(CPUCores);
					s3QuantFeat.add(CPUSpeed);
					counter++;
				}
				else if(counter == 3)
				{
					s4QuantFeat.add(CPUCores);
					s4QuantFeat.add(CPUSpeed);
					counter++;
				}
			}
		}

		///4th line,	MEMORYSIZE
		QuantitativeValue MemorySize=null;
		Element e2 = ite.next();
		Iterator<Element> itemp2 = e2.children().iterator();
		itemp2.next();//first is the name
		counter = 0;
		while(itemp2.hasNext())
		{
			String datab=itemp2.next().text();
			if(itemp2.hasNext())
			{
				String[] ds = datab.split(" ");
				MemorySize = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
				MemorySize.addType(CLOUDEnum.MEMORYSIZE.getConceptURI());
				MemorySize.setValue(Double.parseDouble(ds[0]));
				if(ds[1].equals("GB"))
					MemorySize.setUnitOfMeasurement("E34");
				
				
				if(counter == 0)
				{
					s1QuantFeat.add(MemorySize);
					counter++;
				}
				else if(counter == 1)
				{
					s2QuantFeat.add(MemorySize);
					counter++;
				}
				else if(counter == 2)
				{
					s3QuantFeat.add(MemorySize);
					counter++;
				}
				else if(counter == 3)
				{
					s4QuantFeat.add(MemorySize);
					counter++;
				}
			}
		}
		
		//5th line, DiskSize, StorageType
		
		QuantitativeValue DiskSize=null;
		QualitativeValue StorageType = null;
		Element e3 = ite.next();
		Iterator<Element> itemp3 = e3.children().iterator();
		itemp3.next();//first is the name
		counter = 0;
		while(itemp3.hasNext())
		{
			String datac = itemp3.next().text();
			if(itemp3.hasNext())
			{
				String[] ds2 = datac.split(" x ");
				String[] ds2temp = ds2[1].split(" ");
				DiskSize = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
				DiskSize.addType(CLOUDEnum.DISKSIZE.getConceptURI());
				DiskSize.setValue(Double.parseDouble(ds2[0]) * Double.parseDouble(ds2temp[0]));
				if(ds2temp[1].equals("GB"))
					DiskSize.setUnitOfMeasurement("E34");
				
				StorageType = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
				StorageType.addType(CLOUDEnum.STORAGETYPE.getConceptURI());
				StorageType.setHasLabel(ds2temp[2]);
				
				if(counter == 0)
				{
					s1QuantFeat.add(DiskSize);
					s1QualFeat.add(StorageType);
					counter++;
				}
				else if(counter == 1)
				{
					s2QuantFeat.add(DiskSize);
					s2QualFeat.add(StorageType);
					counter++;
				}
				else if(counter == 2)
				{
					s3QuantFeat.add(DiskSize);
					s3QualFeat.add(StorageType);
					counter++;
				}
				else if(counter == 3)
				{
					s4QuantFeat.add(DiskSize);
					s4QualFeat.add(StorageType);
					counter++;
				}
				
			}
		}
		
		
		/////////////////////////////////////////////////////////////////////////////END OF 1ST TABLE//////////////////////////////////////////////////////////////////////////////
		
		
		
		///////////////////////////////////////////////////////////////////////3RD TABLE //////////////////////////////////////////////////////////////////////////////////////////
		
		Element tableb = doc.select("#Linux > div:nth-child(3) > div > table").first();

		Iterator<Element> iteb = tableb.select("tr").iterator();
		
		QualitativeValue feature_installation_launch=null;
		Element e4 = iteb.next();//first line
		Iterator<Element> itemp4 = e4.children().iterator();
		counter = 0;
		itemp4.next();//skip the element since its the name
		while(itemp4.hasNext())
		{
			String datad= itemp4.next().text();
			if(itemp4.hasNext())
			{
				feature_installation_launch = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
				feature_installation_launch.addType(CLOUDEnum.FEATURE.getConceptURI());
				feature_installation_launch.setComment("Time it takes for the instance to be available for consumption.");
				feature_installation_launch.setHasLabel(datad);
				
				if(counter == 0)
				{
					s1QualFeat.add(feature_installation_launch);
					counter++;
				}
				else if(counter == 1)
				{
					s2QualFeat.add(feature_installation_launch);
					counter++;
				}
				else if(counter == 2)
				{
					s3QualFeat.add(feature_installation_launch);
					counter++;
				}
				else if(counter == 3)
				{
					s4QualFeat.add(feature_installation_launch);
					counter++;
				}
			}
		}
		
		
		
		iteb.next();//skip the next line
		
		iteb.next();//skip the next line - REMOVE

		//access mode, depends on the OS of the service although, every offering has a WEB option available
		/*QualitativeValue console=null,gui=null;
		Element e5 = iteb.next();//3rd line
		Iterator<Element> itemp5 = e5.children().iterator();
		counter = 0;
		itemp5.next();//skip the element since its the name
		while(itemp5.hasNext())
		{
			String ds6 = itemp5.next().text();
			if(itemp5.hasNext())
			{
				String[] dtp = ds6.split(" ");
				for(String s : dtp)
					System.out.println(s);
			}
			
		}*/
		
		iteb.next();//skip the next line
		iteb.next();//skip the next line

		QualitativeValue monitoring = null;
		Element e6 = iteb.next();// 3rd line
		Iterator<Element> itemp6 = e6.children().iterator();
		counter = 0;
		itemp6.next();// skip the element since its the name
		while (itemp6.hasNext()) {
			
			String ds7 = itemp6.next().text();
			if (itemp6.hasNext()) {
				monitoring = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
				monitoring.addType(CLOUDEnum.MONITORING.getConceptURI());
				if(ds7.toLowerCase().equals("basic"))
				{
					monitoring.setHasLabel("Basic");
					monitoring.setComment(" servicio gratuito que monitoriza el estado (encendido/apagado) del servidor y los recursos CPU, RAM y transferencia. A través del panel recibirá avisos cuando se produzca una alarma y en el caso de encendido/apagado del servidor también recibirá notificación por mail. En el panel podrá ver el estado actual de cada uno de los recursos, un apartado de gráficas con la evolución temporal y un histórico de los eventos generados durante el último mes. Los umbrales de generación de alarmas no son editables y se han definido como: Warning Critical ");
				}
				if(counter == 0)
				{
					s1QualFeat.add(monitoring);
					counter++;
				}
				else if(counter == 1)
				{
					s2QualFeat.add(monitoring);
					counter++;
				}
				else if(counter == 2)
				{
					s3QualFeat.add(monitoring);
					counter++;
				}
				else if(counter == 3)
				{
					s4QualFeat.add(monitoring);
					counter++;
				}
			}
		}
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		///////////////////////////////////////////////////////////////////////5TH TABLE //////////////////////////////////////////////////////////////////////////////////////////
		
		Element tablec = doc.select("#Linux > div:nth-child(5) > div > table").first();

		Iterator<Element> itec = tablec.select("tr").iterator();
		
		QualitativeValue Language = null;
		Element e5 = itec.next();//first line
		Iterator<Element> itemp5 = e5.children().iterator();
		counter = 0;
		String[] languages = itemp5.next().text().split(", ");
		
		while (itemp5.hasNext()) {
			String dataf = itemp5.next().text();
			if (itemp5.hasNext()) {
				if (dataf.toLowerCase().equals("yes")) {
					for (String h : languages) {
						Language = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
						Language.addType(CLOUDEnum.LANGUAGE.getConceptURI());
						Language.setHasLabel(h);
						
						if(counter == 0)
							s1QualFeat.add(Language);
						else if(counter == 1)
							s2QualFeat.add(Language);
						else if(counter == 2)
							s3QualFeat.add(Language);
						else if(counter == 3)
							s4QualFeat.add(Language);
					}
					counter++;
				}
			}

		}
		
		QualitativeValue Platform = null;
		Element e7 = itec.next();//second line
		Iterator<Element> itemp7 = e7.children().iterator();
		counter = 0;
		String db = itemp7.next().text();
		
		while (itemp7.hasNext()) {
			String datafb = itemp7.next().text();
			if (itemp7.hasNext()) {
				if (datafb.toLowerCase().equals("yes")) {
					Platform = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
					Platform.addType(CLOUDEnum.PLATFORM.getConceptURI());
					Platform.setHasLabel(db);

					if (counter == 0)
						s1QualFeat.add(Platform);
					else if (counter == 1)
						s2QualFeat.add(Platform);
					else if (counter == 2)
						s3QualFeat.add(Platform);
					else if (counter == 3)
						s4QualFeat.add(Platform);
					counter++;
				}
			}

		}
		
		Element e8 = itec.next();//third line
		Iterator<Element> itemp8 = e8.children().iterator();
		counter = 0;
		String dbc = itemp8.next().text();
		
		while (itemp8.hasNext()) {
			String datafc = itemp8.next().text();
			if (itemp8.hasNext()) {
				if (datafc.toLowerCase().equals("yes")) {
					Platform = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
					Platform.addType(CLOUDEnum.PLATFORM.getConceptURI());
					Platform.setHasLabel(dbc);

					if (counter == 0)
						s1QualFeat.add(Platform);
					else if (counter == 1)
						s2QualFeat.add(Platform);
					else if (counter == 2)
						s3QualFeat.add(Platform);
					else if (counter == 3)
						s4QualFeat.add(Platform);
					counter++;
				}
			}

		}
		
		
	
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		///////////////////////////////////////////////////////////////////////6TH TABLE //////////////////////////////////////////////////////////////////////////////////////////
		
		
		Element tabled = doc.select("#Linux > div:nth-child(6) > div > table").first();

		Iterator<Element> ited = tabled.select("tr").iterator();
		
		QualitativeValue Security = null;
		ited.next();ited.next();
		Element e10 = ited.next();//first line
		Iterator<Element> itemp10 = e10.children().iterator();
		counter = 0;

		while (itemp10.hasNext()) {
			String data10 = itemp10.next().text();
			if (itemp10.hasNext()) {
				String tt = e10.text().replaceAll("YES", "");
				String[] lab = tt.replaceAll("NO", "").split(" ");
				
				
				Security = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
				Security.addType(CLOUDEnum.SECURITY.getConceptURI());
				Security.setHasLabel(lab[0]);
				lab[0]="";
				Security.setComment(Arrays.toString(lab));
				
				if(counter == 0)
				{
					if(data10.toLowerCase().equals("yes"))
						s1QualFeat.add(Security);
					counter++;
				}
				else if(counter == 1)
				{
					if(data10.toLowerCase().equals("yes"))
						s2QualFeat.add(Security);
					counter ++;
					
				}
				else if(counter == 2)
				{
					if(data10.toLowerCase().equals("yes"))
						s3QualFeat.add(Security);
					counter ++;
					
				}
				else if(counter == 3)
				{
					if(data10.toLowerCase().equals("yes"))
						s4QualFeat.add(Security);
					counter ++;
				}
			}
		}
		
		
		
		Element e11 = ited.next();//first line
		Iterator<Element> itemp11 = e11.children().iterator();
		counter = 0;

		while (itemp11.hasNext()) {
			String data11 = itemp11.next().text();
			if (itemp11.hasNext()) {
				
				String tt = e11.text().replaceAll("YES", "");
				String[] lab = tt.replaceAll("NO", "").split(" ");
				
				
				Security = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
				Security.addType(CLOUDEnum.SECURITY.getConceptURI());
				Security.setHasLabel(lab[0]);
				lab[0]="";
				Security.setComment(Arrays.toString(lab));
				
				if(counter == 0)
				{
					if(data11.toLowerCase().equals("yes"))
						s1QualFeat.add(Security);
					counter++;
				}
				else if(counter == 1)
				{
					if(data11.toLowerCase().equals("yes"))
						s2QualFeat.add(Security);
					counter ++;
					
				}
				else if(counter == 2)
				{
					if(data11.toLowerCase().equals("yes"))
						s3QualFeat.add(Security);
					counter ++;
					
				}
				else if(counter == 3)
				{
					if(data11.toLowerCase().equals("yes"))
						s4QualFeat.add(Security);
					counter ++;
				}
			}
		}
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//Finished extracting the basic information for the services. Now we add the OS and their control management which, depends on the OS of the VM. CentOS is free, any other OS has a fee assossiated with it

		//////////////////////////////////////////////////////////////////////////////////// OS TABLE //////////////////////////////////////////////////////////////////////////////
		
		
		Element tableos = doc.select("#Linux > div:nth-child(2) > div").first();

		Iterator<Element> iteos = tableos.select("tr").iterator();
		
		
		QualitativeValue console , gui , web ;
		console=new QualitativeValue("qv"+"_TIME"+System.nanoTime());
		gui=new QualitativeValue("qv"+"_TIME"+System.nanoTime());
		web=new QualitativeValue("qv"+"_TIME"+System.nanoTime());
		
		//this is the only manual information on the scrapper
		web.addType(CLOUDEnum.WEB.getConceptURI());
		web.setHasLabel("Server Control Panel");
		
		
		console.addType(CLOUDEnum.CONSOLE.getConceptURI());
		console.setHasLabel("SSH");
		
		
		gui.addType(CLOUDEnum.GUI.getConceptURI());
		gui.setHasLabel("Remote Desktop");
		
		
		
		
		while (iteos.hasNext()) {
			Element e12 = iteos.next();// first line
			if (iteos.hasNext()) {
				Iterator<Element> itemp12 = e12.children().iterator();
				counter = 0;
				String OSstring = itemp12.next().text();
				
				while (itemp12.hasNext()) {
					String data12 = itemp12.next().text();
					System.out.println(OSstring);
					if (itemp12.hasNext()) {
						if (data12.toLowerCase().equals("free")) {
							if (counter == 0) {
								
								QualitativeValue OS = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
								if (OSstring.equals("CentOS 6") || OSstring.equals("Red Hat Enterprise Linux 6")) {
									OS.addType(CLOUDEnum.UNIX.getConceptURI());
									OS.setHasLabel(OSstring);
								} else {
									OS.addType(CLOUDEnum.WINDOWS.getConceptURI());
									OS.setHasLabel(OSstring);
								}
								
								Service serv = new Service(s1);
								serv.setName(serv.getName().replaceAll("_TIME\\d+.*", "") + "_TIME"+System.nanoTime());
								ArrayList<QuantitativeValue> tquant = new ArrayList<QuantitativeValue>();
								for(QuantitativeValue qv : s1QuantFeat)
									tquant.add(new QuantitativeValue(qv));
								
								ArrayList<QualitativeValue> tqual = new ArrayList<QualitativeValue>();
								for(QualitativeValue qv : s1QualFeat)
									tqual.add(qv);
								
								OS.setHasLabel(OSstring);
								tqual.add(OS);
								
								if(OSstring.equals("CentOS 6") || OSstring.equals("Red Hat Enterprise Linux 6"))
								{
									tqual.add(console);
									tqual.add(web);
								}
								else
								{
									tqual.add(gui);
									tqual.add(web);
								}
								
								serv.setQuantfeatures(tquant);
								serv.setQualfeatures(tqual);
								serv.setName(serv.getName() + "_TIME"+System.nanoTime());
								
								Offering off = createOffering(serv, serv.getName() + OSstring, s1price);
								offs.add(off);

								counter++;
							} else if (counter == 1) {
								Service serv = new Service(s2);
								serv.setName(serv.getName().replaceAll("_TIME\\d+.*", "") + "_TIME"+System.nanoTime());

								ArrayList<QuantitativeValue> tquant = new ArrayList<QuantitativeValue>();
								for(QuantitativeValue qv : s2QuantFeat)
									tquant.add(new QuantitativeValue(qv));
								
								ArrayList<QualitativeValue> tqual = new ArrayList<QualitativeValue>();
								for(QualitativeValue qv : s2QualFeat)
									tqual.add(qv);
								
								if(OSstring.equals("CentOS 6") || OSstring.equals("Red Hat Enterprise Linux 6"))
								{
									tqual.add(console);
									tqual.add(web);
								}
								else
								{
									tqual.add(gui);
									tqual.add(web);
								}
								
								QualitativeValue OS = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
								if (OSstring.equals("CentOS 6") || OSstring.equals("Red Hat Enterprise Linux 6")) {
									OS.addType(CLOUDEnum.UNIX.getConceptURI());
									OS.setHasLabel(OSstring);
								} else {
									OS.addType(CLOUDEnum.WINDOWS.getConceptURI());
									OS.setHasLabel(OSstring);
								}
								
								OS.setHasLabel(OSstring);
								tqual.add(OS);
								serv.setQuantfeatures(tquant);
								serv.setQualfeatures(tqual);
								serv.setName(serv.getName() + "_TIME"+System.nanoTime() );

								Offering off = createOffering(serv, serv.getName() + OSstring, s2price);
								offs.add(off);

								counter++;
							} else if (counter == 2) {
								Service serv = new Service(s3);
								serv.setName(serv.getName().replaceAll("_TIME\\d+.*", "") + "_TIME"+System.nanoTime());
								
								ArrayList<QuantitativeValue> tquant = new ArrayList<QuantitativeValue>();
								for(QuantitativeValue qv : s3QuantFeat)
									tquant.add(new QuantitativeValue(qv));
								
								ArrayList<QualitativeValue> tqual = new ArrayList<QualitativeValue>();
								for(QualitativeValue qv : s3QualFeat)
									tqual.add(qv);
								
								if(OSstring.equals("CentOS 6") || OSstring.equals("Red Hat Enterprise Linux 6"))
								{
									tqual.add(console);
									tqual.add(web);
								}
								else
								{
									tqual.add(gui);
									tqual.add(web);
								}
								
								QualitativeValue OS = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
								if (OSstring.equals("CentOS 6") || OSstring.equals("Red Hat Enterprise Linux 6")) {
									OS.addType(CLOUDEnum.UNIX.getConceptURI());
									OS.setHasLabel(OSstring);
								} else {
									OS.addType(CLOUDEnum.WINDOWS.getConceptURI());
									OS.setHasLabel(OSstring);
								}
								
								OS.setHasLabel(OSstring);
								tqual.add(OS);
								serv.setQuantfeatures(tquant);
								serv.setQualfeatures(tqual);
								serv.setName(serv.getName() + "_TIME"+System.nanoTime() );

								Offering off = createOffering(serv, serv.getName() + OSstring, s3price);
								offs.add(off);

								counter++;
							} else if (counter == 3) {
								Service serv = new Service(s4);
								serv.setName(serv.getName().replaceAll("_TIME\\d+.*", "") + "_TIME"+System.nanoTime());

								ArrayList<QuantitativeValue> tquant = new ArrayList<QuantitativeValue>();
								for(QuantitativeValue qv : s4QuantFeat)
									tquant.add(new QuantitativeValue(qv));
								
								ArrayList<QualitativeValue> tqual = new ArrayList<QualitativeValue>();
								for(QualitativeValue qv : s4QualFeat)
									tqual.add(qv);
								
								if(OSstring.equals("CentOS 6") || OSstring.equals("Red Hat Enterprise Linux 6"))
								{
									tqual.add(console);
									tqual.add(web);
								}
								else
								{
									tqual.add(gui);
									tqual.add(web);
								}
								
								QualitativeValue OS = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
								if (OSstring.equals("CentOS 6") || OSstring.equals("Red Hat Enterprise Linux 6")) {
									OS.addType(CLOUDEnum.UNIX.getConceptURI());
									OS.setHasLabel(OSstring);
								} else {
									OS.addType(CLOUDEnum.WINDOWS.getConceptURI());
									OS.setHasLabel(OSstring);
								}
								
								OS.setHasLabel(OSstring);
								tqual.add(OS);
								serv.setQuantfeatures(tquant);
								serv.setQualfeatures(tqual);
								serv.setName(serv.getName() + "_TIME"+System.nanoTime() );

								Offering off = createOffering(serv, serv.getName() + OSstring, s4price);
								offs.add(off);

								counter++;
							}
						}
						else
						{
							String[] priceinfo = data12.split("/");
							double extrafee = Double.parseDouble(priceinfo[0].substring(1));
							
							if (counter == 0) {
								Service serv = new Service(s1);
								serv.setName(serv.getName().replaceAll("_TIME\\d+.*", "") + "_TIME"+System.nanoTime());

								ArrayList<QuantitativeValue> tquant = new ArrayList<QuantitativeValue>();
								for(QuantitativeValue qv : s1QuantFeat)
									tquant.add(new QuantitativeValue(qv));
								
								ArrayList<QualitativeValue> tqual = new ArrayList<QualitativeValue>();
								for(QualitativeValue qv : s1QualFeat)
									tqual.add(qv);
								
								if(OSstring.equals("CentOS 6") || OSstring.equals("Red Hat Enterprise Linux 6"))
								{
									tqual.add(console);
									tqual.add(web);
								}
								else
								{
									tqual.add(gui);
									tqual.add(web);
								}
								
								QualitativeValue OS = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
								if (OSstring.equals("CentOS 6") || OSstring.equals("Red Hat Enterprise Linux 6")) {
									OS.addType(CLOUDEnum.UNIX.getConceptURI());
									OS.setHasLabel(OSstring);
								} else {
									OS.addType(CLOUDEnum.WINDOWS.getConceptURI());
									OS.setHasLabel(OSstring);
								}
								OS.setHasLabel(OSstring);
								tqual.add(OS);
								serv.setQuantfeatures(tquant);
								serv.setQualfeatures(tqual);
								serv.setName(serv.getName() +  "_TIME"+System.nanoTime());
								
								
								Offering off = createOffering(serv, serv.getName() + OSstring, s1price,extrafee);
								offs.add(off);

								counter++;
							} else if (counter == 1) {
								Service serv = new Service(s2);
								serv.setName(serv.getName().replaceAll("_TIME\\d+.*", "") + "_TIME"+System.nanoTime());
								
								ArrayList<QuantitativeValue> tquant = new ArrayList<QuantitativeValue>();
								for(QuantitativeValue qv : s2QuantFeat)
									tquant.add(new QuantitativeValue(qv));
								
								ArrayList<QualitativeValue> tqual = new ArrayList<QualitativeValue>();
								for(QualitativeValue qv : s2QualFeat)
									tqual.add(qv);
								
								if(OSstring.equals("CentOS 6") || OSstring.equals("Red Hat Enterprise Linux 6"))
								{
									tqual.add(console);
									tqual.add(web);
								}
								else
								{
									tqual.add(gui);
									tqual.add(web);
								}
								
								QualitativeValue OS = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
								if (OSstring.equals("CentOS 6") || OSstring.equals("Red Hat Enterprise Linux 6")) {
									OS.addType(CLOUDEnum.UNIX.getConceptURI());
									OS.setHasLabel(OSstring);
								} else {
									OS.addType(CLOUDEnum.WINDOWS.getConceptURI());
									OS.setHasLabel(OSstring);
								}
								OS.setHasLabel(OSstring);
								tqual.add(OS);
								serv.setQuantfeatures(tquant);
								serv.setQualfeatures(tqual);
								serv.setName(serv.getName() + "_TIME"+System.nanoTime() );

								Offering off = createOffering(serv, serv.getName() + OSstring, s2price,extrafee);
								offs.add(off);

								counter++;
							} else if (counter == 2) {
								Service serv = new Service(s3);
								serv.setName(serv.getName().replaceAll("_TIME\\d+.*", "") + "_TIME"+System.nanoTime());
								
								ArrayList<QuantitativeValue> tquant = new ArrayList<QuantitativeValue>();
								for(QuantitativeValue qv : s3QuantFeat)
									tquant.add(new QuantitativeValue(qv));
								
								ArrayList<QualitativeValue> tqual = new ArrayList<QualitativeValue>();
								for(QualitativeValue qv : s3QualFeat)
									tqual.add(qv);
								
								if(OSstring.equals("CentOS 6") || OSstring.equals("Red Hat Enterprise Linux 6"))
								{
									tqual.add(console);
									tqual.add(web);
								}
								else
								{
									tqual.add(gui);
									tqual.add(web);
								}
								
								QualitativeValue OS = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
								if (OSstring.equals("CentOS 6") || OSstring.equals("Red Hat Enterprise Linux 6")) {
									OS.addType(CLOUDEnum.UNIX.getConceptURI());
									OS.setHasLabel(OSstring);
								} else {
									OS.addType(CLOUDEnum.WINDOWS.getConceptURI());
									OS.setHasLabel(OSstring);
								}
								OS.setHasLabel(OSstring);
								tqual.add(OS);
								serv.setQuantfeatures(tquant);
								serv.setQualfeatures(tqual);
								serv.setName(serv.getName()  + "_TIME"+System.nanoTime());

								Offering off = createOffering(serv, serv.getName() + OSstring, s3price,extrafee);
								offs.add(off);

								counter++;
							} else if (counter == 3) {
								Service serv = new Service(s4);
								serv.setName(serv.getName().replaceAll("_TIME\\d+.*", "") + "_TIME"+System.nanoTime());
								
								ArrayList<QuantitativeValue> tquant = new ArrayList<QuantitativeValue>();
								for(QuantitativeValue qv : s4QuantFeat)
									tquant.add(new QuantitativeValue(qv));
								
								ArrayList<QualitativeValue> tqual = new ArrayList<QualitativeValue>();
								for(QualitativeValue qv : s4QualFeat)
									tqual.add(qv);
								
								
								if(OSstring.equals("CentOS 6") || OSstring.equals("Red Hat Enterprise Linux 6"))
								{
									tqual.add(console);
									tqual.add(web);
								}
								else
								{
									tqual.add(gui);
									tqual.add(web);
								}
								
								QualitativeValue OS = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
								if (OSstring.equals("CentOS 6") || OSstring.equals("Red Hat Enterprise Linux 6")) {
									OS.addType(CLOUDEnum.UNIX.getConceptURI());
									OS.setHasLabel(OSstring);
								} else {
									OS.addType(CLOUDEnum.WINDOWS.getConceptURI());
									OS.setHasLabel(OSstring);
								}
								OS.setHasLabel(OSstring);
								tqual.add(OS);
								serv.setQuantfeatures(tquant);
								serv.setQualfeatures(tqual);
								serv.setName(serv.getName()  + "_TIME"+System.nanoTime());
								Offering off = createOffering(serv, serv.getName() + OSstring, s4price,extrafee);
								offs.add(off);

								counter++;
							}
						}
					}
				}
			}
		}		
		
		jmodel.setOfferings(offs);
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	}
	
	public static  boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    @SuppressWarnings("unused")
		double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
	
	public  Offering createOffering(Service serv,String name,double baseprice, double extrafee)
	{
		Offering of = new Offering();
		of.setName(name + "_TIME"+System.nanoTime());
		of.setComment("Offering of the "+name+" service");
		of.addService(serv);
		addPricePlan(of,baseprice,extrafee);
		
		return of;
	}
	
	public  Offering createOffering(Service serv,String name,double baseprice)
	{
		Offering of = new Offering();
		of.setName(name + "_TIME"+System.nanoTime());
		of.setComment("Offering of the "+name+" service");
		of.addService(serv);
		addPricePlan(of,baseprice);
		
		return of;
	}
	
	public  void addPricePlan(Offering of,double baseprice)
	{
		PricePlan pp = new PricePlan();
		of.setPricePlan(pp);
		pp.setComment("PricePlan of the " + of.getName() + " offering.");
		pp.setName(of.getName()+"PP" +  "_TIME"+System.nanoTime());
		addPriceComponent(pp,baseprice,true);
	}
	
	public  void addPricePlan(Offering of,double baseprice,double extrafee)
	{
		PricePlan pp = new PricePlan();
		of.setPricePlan(pp);
		pp.setComment("PricePlan of the " + of.getName() + " offering.");
		pp.setName(of.getName()+"PP" + "_TIME"+System.nanoTime());
		addPriceComponent(pp,baseprice,true);
		addPriceComponent(pp,extrafee,false);

	}
	
	public  void addPriceComponent(PricePlan pp,double constant,boolean baseprice)
	{
		PriceComponent pc = new PriceComponent();
		pp.addPriceComponent(pc);
		
		PriceFunction pf = new PriceFunction();
		pc.setPriceFunction(pf);
		
		
		Usage NumberOfMonths = new Usage();
		String varname = "NumberOfMonths" + "TIME";
		NumberOfMonths.setName(varname + System.nanoTime());
		NumberOfMonths.setComment("Number of months that you'll be needing the VM.");
		
		Provider Price = new Provider();
		String varnameb=null;
		if(baseprice)
		{
			varnameb = "BasePrice" + "TIME" ;
			Price.setName(varnameb + System.nanoTime());
		}
		else
		{
			varnameb = "ExtraFee" + "TIME" ;
			Price.setName(varnameb + System.nanoTime());
		}
		
		QuantitativeValue val = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
		val.setValue(constant);
		
		Price.setValue(val);
		
		pf.addUsageVariable(NumberOfMonths);
		pf.addProviderVariable(Price);
		pf.setStringFunction(NumberOfMonths.getName() + "*" +Price.getName());
	}
	
	public static  void main(String[] args) throws IOException, InvalidLinkedUSDLModelException
	{
		LinkedUSDLModel jmodel;
		
		Arsys arsys = new Arsys(); 
		jmodel = LinkedUSDLModelFactory.createEmptyModel();
		arsys.parse(jmodel);
		
		jmodel.setBaseURI("http://PricingAPIArsysOfferings.com");
		Model instance = jmodel.WriteToModel();//transform the java models to a semantic representation

		File outputFile = new File("./ProviderSets/arsys_fullset.ttl");
		if (!outputFile.exists()) {
			outputFile.createNewFile();
		}

		FileOutputStream out = new FileOutputStream(outputFile);
		instance.write(out, "Turtle");
		out.close();
	}
	
}

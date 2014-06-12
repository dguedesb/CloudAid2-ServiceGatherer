package Amazon;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import usdl.constants.enums.CLOUDEnum;
import usdl.servicemodel.LinkedUSDLModel;
import usdl.servicemodel.LinkedUSDLModelFactory;
import usdl.servicemodel.Offering;
import usdl.servicemodel.PriceComponent;
import usdl.servicemodel.PriceFunction;
import usdl.servicemodel.PricePlan;
import usdl.servicemodel.PriceSpec;
import usdl.servicemodel.Provider;
import usdl.servicemodel.QualitativeValue;
import usdl.servicemodel.QuantitativeValue;
import usdl.servicemodel.Service;
import usdl.servicemodel.Usage;

import com.hp.hpl.jena.rdf.model.Model;

import exceptions.InvalidLinkedUSDLModelException;

public class AmazonReservedInstances {

	static List<String> urls_light = Arrays.asList("http://aws-assets-pricing-prod.s3.amazonaws.com/pricing/ec2/linux-ri-light.js", "http://aws-assets-pricing-prod.s3.amazonaws.com/pricing/ec2/rhel-ri-light.js",
			"http://aws-assets-pricing-prod.s3.amazonaws.com/pricing/ec2/sles-ri-light.js","http://aws-assets-pricing-prod.s3.amazonaws.com/pricing/ec2/mswin-ri-light.js","http://aws-assets-pricing-prod.s3.amazonaws.com/pricing/ec2/mswinSQL-ri-light.js",
			"http://aws-assets-pricing-prod.s3.amazonaws.com/pricing/ec2/mswinSQLWeb-ri-light.js");
	
	static List<String> urls_medium = Arrays.asList("http://aws-assets-pricing-prod.s3.amazonaws.com/pricing/ec2/linux-ri-medium.js", "http://aws-assets-pricing-prod.s3.amazonaws.com/pricing/ec2/rhel-ri-medium.js",
			"http://aws-assets-pricing-prod.s3.amazonaws.com/pricing/ec2/sles-ri-medium.js","http://aws-assets-pricing-prod.s3.amazonaws.com/pricing/ec2/mswin-ri-medium.js","http://aws-assets-pricing-prod.s3.amazonaws.com/pricing/ec2/mswinSQL-ri-medium.js",
			"http://aws-assets-pricing-prod.s3.amazonaws.com/pricing/ec2/mswinSQLWeb-ri-medium.js");
	
	static List<String> urls_heavy= Arrays.asList("http://aws-assets-pricing-prod.s3.amazonaws.com/pricing/ec2/linux-ri-heavy.js", "http://aws-assets-pricing-prod.s3.amazonaws.com/pricing/ec2/rhel-ri-heavy.js",
			"http://aws-assets-pricing-prod.s3.amazonaws.com/pricing/ec2/sles-ri-heavy.js","http://aws-assets-pricing-prod.s3.amazonaws.com/pricing/ec2/mswin-ri-heavy.js","http://aws-assets-pricing-prod.s3.amazonaws.com/pricing/ec2/mswinSQL-ri-heavy.js",
			"http://aws-assets-pricing-prod.s3.amazonaws.com/pricing/ec2/mswinSQLWeb-ri-heavy.js");
	
	private static String datatransf_url ="http://aws-assets-pricing-prod.s3.amazonaws.com/pricing/ec2/pricing-data-transfer-with-regions.js";
	static int counter=0;
	
	public static void main(String[] args) throws MalformedURLException, IOException, ParseException, InvalidLinkedUSDLModelException
	{
		AmazonReservedInstances worker = new AmazonReservedInstances();
		AmazonBaseServices abase = new AmazonBaseServices();
		

		ArrayList<Service> basicServ =abase.AmazonBServices();
		
		for(String url : urls_light)
		{
			LinkedUSDLModel jmodel;

			jmodel = LinkedUSDLModelFactory.createEmptyModel();
			
			worker.AmazonReservedOffering(jmodel,basicServ,url,0);

			jmodel.setBaseURI("http://PricingAPIAmazonReservedOfferings.com");
			Model instance = jmodel.WriteToModel();//transform the java models to a semantic representation

			File outputFile = new File("./ProviderSets/amazonReservedInstances_fullset-Light"+counter+++".ttl");
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}

			FileOutputStream out = new FileOutputStream(outputFile);
			instance.write(out, "Turtle");
			out.close();
		}
		counter=0;
		for(String url : urls_medium)
		{
			LinkedUSDLModel jmodel;

			jmodel = LinkedUSDLModelFactory.createEmptyModel();
			
			worker.AmazonReservedOffering(jmodel,basicServ,url,1);

			jmodel.setBaseURI("http://PricingAPIAmazonReservedOfferings.com");
			Model instance = jmodel.WriteToModel();//transform the java models to a semantic representation

			File outputFile = new File("./ProviderSets/amazonReservedInstances_fullset-Medium"+counter+++".ttl");
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}

			FileOutputStream out = new FileOutputStream(outputFile);
			instance.write(out, "Turtle");
			out.close();
		}
		counter=0;
		for(String url : urls_heavy)
		{
			LinkedUSDLModel jmodel;

			jmodel = LinkedUSDLModelFactory.createEmptyModel();
			
			worker.AmazonReservedOffering(jmodel,basicServ,url,2);

			jmodel.setBaseURI("http://PricingAPIAmazonReservedOfferings.com");
			Model instance = jmodel.WriteToModel();//transform the java models to a semantic representation

			File outputFile = new File("./ProviderSets/amazonReservedInstances_fullset-Heavy"+counter+++".ttl");
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}

			FileOutputStream out = new FileOutputStream(outputFile);
			instance.write(out, "Turtle");
			out.close();
		}
	}
	
	public void AmazonReservedOffering(LinkedUSDLModel jmodel, ArrayList<Service> basicServices,String url,int type) throws IOException, ParseException
	{
		// First, we fetch the JSON String from AmazonEC2

		// String JsonString = IOUtils.toString(new URL(url).openStream());

		/************************************************************/
		 //File file=null; 
		// Keep a copy on disk
		/*if (counter==0)
		{
			if(type == 0)
				file = new File("./HTMLCopies/amazon_ReservedInstances-linux-Light.json");
			else if(type==1)
				file = new File("./HTMLCopies/amazon_ReservedInstances-linux-Medium.json");
			else if(type==2)
				file = new File("./HTMLCopies/amazon_ReservedInstances-linux-Heavy.json");
		}
		else if (counter==1) {
			if(type == 0)
				file = new File("./HTMLCopies/amazon_ReservedInstances-rhel-Light.json");
			else if(type==1)
				file = new File("./HTMLCopies/amazon_ReservedInstances-rhel-Medium.json");
			else if(type==2)
				file = new File("./HTMLCopies/amazon_ReservedInstances-rhel-Heavy.json");
		}
		else if  (counter==2) {
			if(type == 0)
				file = new File("./HTMLCopies/amazon_ReservedInstances-sles-Light.json");
			else if(type==1)
				file = new File("./HTMLCopies/amazon_ReservedInstances-sles-Medium.json");
			else if(type==2)
				file = new File("./HTMLCopies/amazon_ReservedInstances-sles-Heavy.json");
		}
		else if  (counter==3) {
			if(type == 0)
				file = new File("./HTMLCopies/amazon_ReservedInstances-mswin-Light.json");
			else if(type==1)
				file = new File("./HTMLCopies/amazon_ReservedInstances-mswin-Medium.json");
			else if(type==2)
				file = new File("./HTMLCopies/amazon_ReservedInstances-mswin-Heavy.json");
		}
		else if (counter==4) {
			if(type == 0)
				file = new File("./HTMLCopies/amazon_ReservedInstances-mswinSQL-Light.json");
			else if(type==1)
				file = new File("./HTMLCopies/amazon_ReservedInstances-mswinSQL-Medium.json");
			else if(type==2)
				file = new File("./HTMLCopies/amazon_ReservedInstances-mswinSQL-Heavy.json");
		}
		else {
			if(type == 0)
				file = new File("./HTMLCopies/amazon_ReservedInstances-mswinSQLWeb-Light.json");
			else if(type==1)
				file = new File("./HTMLCopies/amazon_ReservedInstances-mswinSQLWeb-Medium.json");
			else if(type==2)
				file = new File("./HTMLCopies/amazon_ReservedInstances-mswinSQLWeb-Heavy.json");
		}
		 // if file doesnt exists, then create it
		  if (!file.exists()) {
		  file.createNewFile(); }
		  
		  FileWriter fw = new FileWriter(file.getAbsoluteFile());
		  BufferedWriter bw = new BufferedWriter(fw);
		  bw.write(JsonString);
		  bw.close();*/
		 
		/************************************************************/
		String path="./HTMLCopies/";
		if (counter == 0)
		{
			if(type==0)
				path += "amazon_ReservedInstances-linux-Light.json";
			else if(type==1)
				path += "amazon_ReservedInstances-linux-Medium.json";
			else if(type==2)
				path += "amazon_ReservedInstances-linux-Heavy.json";
		}
		else if (counter == 1) {
			if(type==0)
				path += "amazon_ReservedInstances-rhel-Light.json";
			else if(type==1)
				path += "amazon_ReservedInstances-rhel-Medium.json";
			else if(type==2)
				path += "amazon_ReservedInstances-rhel-Heavy.json";
		} else if (counter == 2) {
			if(type==0)
				path += "amazon_ReservedInstances-sles-Light.json";
			else if(type==1)
				path += "amazon_ReservedInstances-sles-Medium.json";
			else if(type==2)
				path += "amazon_ReservedInstances-sles-Heavy.json";
		} else if (counter == 3) {
			if(type==0)
				path += "amazon_ReservedInstances-mswin-Light.json";
			else if(type==1)
				path += "amazon_ReservedInstances-mswin-Medium.json";
			else if(type==2)
				path += "amazon_ReservedInstances-mswin-Heavy.json";
		} else if (counter == 4) {
			if(type==0)
				path += "amazon_ReservedInstances-mswinSQL-Light.json";
			else if(type==1)
				path += "amazon_ReservedInstances-mswinSQL-Medium.json";
			else if(type==2)
				path += "amazon_ReservedInstances-mswinSQL-Heavy.json";
		} else{
			if(type==0)
				path+="amazon_ReservedInstances-mswinSQLWeb-Light.json";
			else if(type==1)
				path+="amazon_ReservedInstances-mswinSQLWeb-Medium.json";
			else if(type==2)
				path+="amazon_ReservedInstances-mswinSQLWeb-Heavy.json";
		}
		String JsonString = new Scanner(new File(path)).useDelimiter("\\Z").next();// read the json file from the disk
		JsonString = JsonString.substring(9, JsonString.length() - 1);// remove some unnecessary characters

		JSONParser parser = new JSONParser();
		Object obj = parser.parse(JsonString);

		JSONObject jsonObject = (JSONObject) obj;

		// Double version = (Double) jsonObject.get("vers");

		JSONObject configs = (JSONObject) jsonObject.get("config");
		
		
		JSONArray regions = (JSONArray) (configs.get("regions"));
		Iterator<JSONObject> iterator = regions.iterator();

		// first region
		ArrayList<Offering> offs = new ArrayList<Offering>();
		while (iterator.hasNext()) {
			 JSONObject reg = iterator.next();
             String region = (String) reg.get("region");
             if(region.equals("us-west-1"))
            	 region="us-west";
             else if(region.equals("eu-west-1"))
            	 region="eu-ireland";
             else if(region.equals("ap-southeast-1"))
            	 region="apac-sin";
             else if(region.equals("ap-southeast-2"))
            	 region="apac-syd";
             else if(region.equals("ap-northeast-1"))
            	 region="apac-tokyo";
             
             QualitativeValue Location = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
             Location.addType(CLOUDEnum.LOCATION.getConceptURI());
             Location.setHasLabel(region);
             
             JSONArray instance_types = (JSONArray) reg.get("instanceTypes");
             Iterator<JSONObject> itemp = instance_types.iterator(); 
             
             while(itemp.hasNext())
             {
            	 JSONObject fdt= itemp.next();
                 String instanceType = (String) fdt.get("type");
                 
                 JSONArray instance_details = (JSONArray) fdt.get("sizes");
                 Iterator<JSONObject> itdetail = instance_details.iterator(); 
                 while(itdetail.hasNext())
                 {
                	 JSONObject cont = itdetail.next();
                     String instname = (String) cont.get("size");

                     JSONArray priceopt = (JSONArray) cont.get("valueColumns");
                     Iterator<JSONObject> priceit = priceopt.iterator(); 
                     double upfront=-1;double perh=-1;
                    
                     int nfee=0;
                     String feetype="";
                     while(priceit.hasNext())
                     {   
                    	 
                    	 JSONObject osj = priceit.next();
                    	 
  						JSONObject osprice = (JSONObject) osj.get("prices");

  						Pattern p = Pattern.compile(".*[a-zA-Z].*");
  						Matcher m = p.matcher(((String) osprice.get("USD")));
  						if (m.find())
  							continue;

  						Double ospricej = Double.parseDouble((String) osprice.get("USD"));

                      	 feetype = (String) osj.get("name");
                 		 
                 		nfee++;
                 		if(nfee%2 !=0 )
                 			upfront = ospricej;
                 		else if(nfee%2 == 0)
                 			perh=ospricej;
                    	 if(nfee%2 == 0)
                    	 {
                    		 String OSName="";
                    		 QualitativeValue OS = null;
                             QualitativeValue Platform = null;
                              if(counter == 0)
                              {
                             	 OS = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
                             	 OS.addType(CLOUDEnum.UNIX.getConceptURI());
                             	 OS.setHasLabel("Linux");
                             	 OSName="Linux";
                              }
                              else if(counter == 1)
                              {
                             	 OS = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
                             	 OS.addType(CLOUDEnum.WINDOWS.getConceptURI());
                             	 OS.setHasLabel("Red Hat Enterprise Linux");
                             	OSName="Red Hat Enterprise Linux";
                              }
                              else if(counter == 2)
                              {
                             	 OS = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
                             	 OS.addType(CLOUDEnum.WINDOWS.getConceptURI());
                             	 OS.setHasLabel("SUSE Linux Enterprise Server");
                             	OSName="SUSE Linux Enterprise Server";
                              }
                              else if(counter == 3)
                              {
                             	 OS = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
                             	 OS.addType(CLOUDEnum.WINDOWS.getConceptURI());
                             	 OS.setHasLabel("Windows");
                             	OSName="Windows";
                              }
                              else if(counter == 4)
                              {
                             	 OS = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
                             	 OS.addType(CLOUDEnum.WINDOWS.getConceptURI());
                             	 OS.setHasLabel("Windows");
                             	OSName="Windows";
                             	 
                             	 Platform = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
                             	 Platform.addType(CLOUDEnum.PLATFORM.getConceptURI());
                             	 OS.setHasLabel("MySql Standard");
                              }
                              else if(counter == 5)
                              {
                             	 OS = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
                             	 OS.addType(CLOUDEnum.WINDOWS.getConceptURI());
                             	 OS.setHasLabel("Windows");
                             	OSName="Windows";
                             	
                             	 Platform = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
                             	 Platform.addType(CLOUDEnum.PLATFORM.getConceptURI());
                             	 OS.setHasLabel("MySql Web");
                              }
                              
                              
                              for(Service s : basicServices)
                              {
                             	 if(s.getName().equals(instname))
                             	 {
                             		 Service scp = new Service(s);
                             		scp.setName(scp.getName().replaceAll("_TIME\\d+.*", "") + "_TIME"+System.nanoTime());
                             		 scp.addQualitativeFeature(OS);
                             		 scp.addQualitativeFeature(Location);
                             		 
                             		QualitativeValue ServiceType =  new QualitativeValue("qv"+"_TIME"+System.nanoTime());
                         			ServiceType.addType(CLOUDEnum.FEATURE.getConceptURI());
                         			ServiceType.setHasLabel("Virtual Machine");
                         			scp.addQualFeature(ServiceType);
                             		 
                             		// System.out.println(""+scp.getName() + " at "+Location.getHasValue() + " with price:  "+ospricej);
                             		 if(Platform != null)
                             			 scp.addQualitativeFeature(Platform);
                             		 //create offering
                             		 Offering of = new Offering();
                             		 if(feetype.equals("yrTerm1Hourly"))
                             		 {
                             			 if(type==0)
                             				 of.setName(scp.getName()+"-"+OSName+"-AmazonReservedInstance-1Year-Light" + "_TIME"+System.nanoTime());
                             			 else if(type==1)
                             				of.setName(scp.getName()+"-"+OSName+"-AmazonReservedInstance-1Year-Medium" +"_TIME" +System.nanoTime());
                             			else if(type==2)
                             				of.setName(scp.getName()+"-"+OSName+"-AmazonReservedInstance-1Year-Heavy" +"_TIME" +System.nanoTime());
                             		 }
                             			 
                             		 else
                             		 {
                             			 if(type==0)
                             				 of.setName(scp.getName()+"-"+OSName+"-AmazonReservedInstance-3Year-Light" +"_TIME"+System.nanoTime());
                             			else if(type==1)
                             				of.setName(scp.getName()+"-"+OSName+"-AmazonReservedInstance-3Year-Medium" +"_TIME"+System.nanoTime());
                             			else if(type==2)
                             				of.setName(scp.getName()+"-"+OSName+"-AmazonReservedInstance-3Year-Heavy" +"_TIME" +System.nanoTime());
                             		 }

                             		 of.addService(scp);
                             		 
                             		 //create the PricePlan for the offering
                             		 PricePlan pp = new PricePlan();
                             		 of.setPricePlan(pp);
                             		 //System.out.println("upf: "+upfront+"    ph: "+perh + "        region:"+region + "     counter: "+counter);
                             		 addReservedInstancesPriceComponents(pp,upfront,perh,region);
                             		 offs.add(of);
                             	 }
                              }
                              upfront=-1;
                      		 perh=-1;
                    	 }
                     }
                 }
             }
             
             jmodel.setOfferings(offs); 
		}
	}
	
	
	public void addReservedInstancesPriceComponents(PricePlan pp,double upfront, double priceph,String region) throws MalformedURLException, IOException, ParseException
	{
		addPricePerHourComponent(pp,priceph);
		addDataTransferralsPriceComponent(pp,region);
		addUpFrontPriceComponent(pp,upfront);
	}
	
	public void addUpFrontPriceComponent(PricePlan pp,double upfrontfee)
	{
		PriceComponent pc_upfront = new PriceComponent();//Component that represents the initial fee required from the customer
		pp.addPriceComponent(pc_upfront);
		pc_upfront.setName("UpFrontPC-PP" +"_TIME"+System.nanoTime());
		
		PriceSpec upfront = new PriceSpec();
		pc_upfront.setPrice(upfront);
		upfront.setValue(upfrontfee);
		upfront.setCurrency("USD");
	}
	
	public void addPricePerHourComponent(PricePlan pp,double priceph)
	{
		PriceComponent pc_hourly = new PriceComponent();//Component that is responsible for calculating the price per hour of the instance
		pp.addPriceComponent(pc_hourly);
		pc_hourly.setName("HourlyPC" +"_TIME" +System.nanoTime());
		
		PriceFunction pf_hourly = new PriceFunction();
		pc_hourly.setPriceFunction(pf_hourly);
		pf_hourly.setName("hourly_cost_function" +"_TIME" +System.nanoTime());
		
		Usage NumberOfHours = new Usage();
		pf_hourly.addUsageVariable(NumberOfHours);
		NumberOfHours.setName("usagehours"+"TIME"+System.nanoTime());
		NumberOfHours.setComment("The number of hours that you'll be using the instance.");
		
		Provider CostPerHour = new Provider();
		pf_hourly.addProviderVariable(CostPerHour);
		CostPerHour.setName("costph" + "TIME" +System.nanoTime());
		QuantitativeValue val = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
		CostPerHour.setValue(val);
		val.setValue(priceph);
		val.setUnitOfMeasurement("USD");
		
		pf_hourly.setStringFunction(CostPerHour.getName() + "*" +NumberOfHours.getName() );
	}
	
	
	public void addDataTransferralsPriceComponent(PricePlan pp,String region) throws MalformedURLException, IOException, ParseException
	{
		
		PriceComponent traffic_pc = new PriceComponent();//Component responsible for calculating the total price to pay related only to the Data transferral on Amazon EC2
		pp.addPriceComponent(traffic_pc);
		traffic_pc.setName("DataCostPC-PP" +"_TIME"+System.nanoTime());
		
		PriceFunction data_cost_pf = new PriceFunction();
		traffic_pc.setPriceFunction(data_cost_pf);
		data_cost_pf.setName("data_transferrals_cost" +"_TIME"+System.nanoTime());
		
		// fetch the JSON String from AmazonEC2

		//String JsonString = IOUtils.toString(new URL(datatransf_url).openStream());

		/************************************************************/
		// Keep a copy on disk

		
		 /* File file = new File("./HTMLCopies/amaon_datatransf.json"); 
		 // if file doesnt exists, then create it 
		  if (!file.exists()) {
			  file.createNewFile(); 
		  }
		 
		  FileWriter fw = new FileWriter(file.getAbsoluteFile());
		  BufferedWriter bw = new BufferedWriter(fw);
		  bw.write(JsonString);
		  bw.close();*/
		 
		/************************************************************/
		String JsonString =  new Scanner(new File("./HTMLCopies/amazon_datatransf.json")).useDelimiter("\\Z").next();//read the json file from the disk
		JsonString = JsonString.substring(9, JsonString.length() - 1);// remove some unnecessary characters

		JSONParser parser = new JSONParser();
		Object obj = parser.parse(JsonString);

		JSONObject jsonObject = (JSONObject) obj;

		// Double version = (Double) jsonObject.get("vers");

		JSONObject configs = (JSONObject) jsonObject.get("config");
		
		 JSONArray regions = (JSONArray) (configs.get("regions"));  
		Iterator<JSONObject> iterator = regions.iterator();
		Provider price10=null,price40=null,price100=null,price350=null;
		while (iterator.hasNext()) {
			JSONObject reg = iterator.next();
            String region_p = (String) reg.get("region");
            if(region_p.equals(region))
            {
            	JSONArray types = (JSONArray) reg.get("types");
                Iterator<JSONObject> itemp = types.iterator(); 
                while(itemp.hasNext())
                {
                	JSONObject type = itemp.next();
                    String typeName = (String) type.get("name");
                    if(typeName.equals("dataXferOutInternet"))
                    {
                    	JSONArray tiers = (JSONArray) type.get("tiers");
                        Iterator<JSONObject> tiersite = tiers.iterator(); 
                        while(tiersite.hasNext())
                        {
                        	JSONObject tier = tiersite.next();
                            String tierName = (String) tier.get("name");
                            
                            if(tierName.equals("firstGBout"))
                            {
                            	//code to deal with < 1gb transf
                            }
                            else if(tierName.equals("upTo10TBout"))
                            {
                                JSONObject tierPrice = (JSONObject) tier.get("prices");
                            	double price = Double.parseDouble((String)tierPrice.get("USD"));
                            	
                            	price10 = new Provider();
                        		data_cost_pf.addProviderVariable(price10);
                        		price10.setName("price10"+"TIME" +System.nanoTime());
                        		QuantitativeValue valb = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
                        		price10.setValue(valb);
                        		valb.setValue(price);
                        		valb.setUnitOfMeasurement("USD");
                            }
                            else if(tierName.equals("next40TBout"))
                            {
                            	JSONObject tierPrice = (JSONObject) tier.get("prices");
                            	double price = Double.parseDouble((String)tierPrice.get("USD"));
                            	
                            	price40 = new Provider();
                        		data_cost_pf.addProviderVariable(price40);
                        		price40.setName("price40"+"TIME" +System.nanoTime());
                        		QuantitativeValue valc = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
                        		price40.setValue(valc);
                        		valc.setValue(price);
                        		valc.setUnitOfMeasurement("USD");
                            }
                            else if(tierName.equals("next100TBout"))
                            {
                            	JSONObject tierPrice = (JSONObject) tier.get("prices");
                            	double price = Double.parseDouble((String)tierPrice.get("USD"));
                            	
                            	price100 = new Provider();
                        		data_cost_pf.addProviderVariable(price100);
                        		price100.setName("price100"+"TIME"+System.nanoTime());
                        		QuantitativeValue vald = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
                        		price100.setValue(vald);
                        		vald.setValue(price);
                        		vald.setUnitOfMeasurement("USD");
                            }
                            
                            else if(tierName.equals("next350TBout"))
                            {
                            	JSONObject tierPrice = (JSONObject) tier.get("prices");
                            	double price = Double.parseDouble((String)tierPrice.get("USD"));
                            	
                            	price350 = new Provider();
                        		data_cost_pf.addProviderVariable(price350);
                        		price350.setName("price350"+"TIME"+System.nanoTime());
                        		QuantitativeValue vale = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
                        		price350.setValue(vale);
                        		vale.setValue(price);
                        		vale.setUnitOfMeasurement("USD");
                            }
                        }
                    }
                }
            }
		}
		
		Usage gbout = new Usage();
		data_cost_pf.addUsageVariable(gbout);
		gbout.setName("gbout"+"TIME" +System.nanoTime());
		gbout.setComment("Total GB of data that you expect to send out from Amazon EC2 to the internet.");
		data_cost_pf.setStringFunction("  IF ("+gbout.getName()+"<= 1) ; 1 * 0.00 ~"
				+ " ELSEIF"+gbout.getName()+" > 1 && "+gbout.getName()+" <= 10*1024 ; 1*0.00 + ("+gbout.getName()+"-1) * "+price10.getName()+" ~ "
				+ "ELSEIF ("+gbout.getName()+" > 10*1024) && ("+gbout.getName()+"<= 40*1024) ; 1*0.00 + 10*1024*"+price10.getName()+" + ("+gbout.getName()+"-10*1024-1)*"+price40.getName()+" ~ "
				+ "ELSEIF ("+gbout.getName()+" >= 40*1024) && ("+gbout.getName()+" < 100*1024) ; 1*0.00 + 10*1024*"+price10.getName()+" + 40*1024*"+price40.getName()+" + ("+gbout.getName()+"-1-10*1024-40*1024)*"+price100.getName()+" ~ "
				+ "ELSEIF ("+gbout.getName()+" >= 100*1024) && ("+gbout.getName()+" < 350*1024) ; 1*0.00 + 10*1024*"+price10.getName()+" + 40*1024*"+price40.getName()+" + 100*1024*"+price100.getName()+" + ("+gbout.getName()+"-1-10*1024-40*1024-100*1024)*"+price350.getName()+"");
	}
	
	
	
}

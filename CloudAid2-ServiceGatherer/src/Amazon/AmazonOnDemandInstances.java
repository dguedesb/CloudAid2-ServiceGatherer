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
import usdl.servicemodel.Provider;
import usdl.servicemodel.QualitativeValue;
import usdl.servicemodel.QuantitativeValue;
import usdl.servicemodel.Service;
import usdl.servicemodel.Usage;

import com.hp.hpl.jena.rdf.model.Model;

import exceptions.InvalidLinkedUSDLModelException;

public class AmazonOnDemandInstances {
	static List<String> urls = Arrays.asList("http://aws-assets-pricing-prod.s3.amazonaws.com/pricing/ec2/linux-od.js", "http://aws-assets-pricing-prod.s3.amazonaws.com/pricing/ec2/rhel-od.js",
			"http://aws-assets-pricing-prod.s3.amazonaws.com/pricing/ec2/sles-od.js","http://aws-assets-pricing-prod.s3.amazonaws.com/pricing/ec2/mswin-od.js","http://aws-assets-pricing-prod.s3.amazonaws.com/pricing/ec2/mswinSQL-od.js",
			"http://aws-assets-pricing-prod.s3.amazonaws.com/pricing/ec2/mswinSQLWeb-od.js");
	private static String datatransf_url ="http://aws-assets-pricing-prod.s3.amazonaws.com/pricing/ec2/pricing-data-transfer-with-regions.js";
	static int counter=0;

	public static void main(String[] args) throws MalformedURLException, IOException, ParseException, InvalidLinkedUSDLModelException
	{
		AmazonOnDemandInstances worker = new AmazonOnDemandInstances();
		AmazonBaseServices abase = new AmazonBaseServices();
		
		ArrayList<Service> basicServ =abase.AmazonBServices();
		


		for(String url : urls)
		{
			LinkedUSDLModel jmodel;

			jmodel = LinkedUSDLModelFactory.createEmptyModel();
			
			worker.AmazonOnDemandOffering(jmodel,basicServ,url);
			
			
			jmodel.setBaseURI("http://PricingAPIAmazonOnDemandOfferings.com");
			
			Model instance = jmodel.WriteToModel();//transform the java models to a semantic representation

			File outputFile = new File("./ProviderSets/amazonOnDemand_fullset"+counter+++".ttl");
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}

			FileOutputStream out = new FileOutputStream(outputFile);
			instance.write(out, "Turtle");
			out.close();
		}
		
	}
	
	public void AmazonOnDemandOffering(LinkedUSDLModel jmodel, ArrayList<Service> basicServices,String url) throws IOException, ParseException
	{
		// First, we fetch the JSON String from AmazonEC2

		 //String JsonString = IOUtils.toString(new URL(url).openStream());

		/************************************************************/
		 File file=null; 
		// Keep a copy on disk
		/*if (counter==0)
			file = new File("./HTMLCopies/amazon_ondemandInstances-linux.json");
		else if (counter==1) {
			file = new File("./HTMLCopies/amazon_ondemandInstances-rhel.json");
		}
		else if  (counter==2) {
			file = new File("./HTMLCopies/amazon_ondemandInstances-sles.json");
		}
		else if  (counter==3) {
			file = new File("./HTMLCopies/amazon_ondemandInstances-mswin.json");
		}
		else if (counter==4) {
			file = new File("./HTMLCopies/amazon_ondemandInstances-mswinSQL.json");
		}
		else {
			file = new File("./HTMLCopies/amazon_ondemandInstances-mswinSQLWeb.json");
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
			path += "amazon_ondemandInstances-linux.json";
		else if (counter == 1) {
			path += "amazon_ondemandInstances-rhel.json";
		} else if (counter == 2) {
			path += "amazon_ondemandInstances-sles.json";
		} else if (counter == 3) {
			path += "amazon_ondemandInstances-mswin.json";
		} else if (counter == 4) {
			path += "amazon_ondemandInstances-mswinSQL.json";
		} else {
			path+="amazon_ondemandInstances-mswinSQLWeb.json";
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

                     JSONArray osoption = (JSONArray) cont.get("valueColumns");
                     Iterator<JSONObject> osit = osoption.iterator(); 
                     while(osit.hasNext())
                     {
                    	 JSONObject osj = osit.next();
                         String OSName = (String) osj.get("name");
                         
                        QualitativeValue OS = null;
                        QualitativeValue Platform = null;
                         if(OSName.equals("linux"))
                         {
                        	 OS = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
                        	 OS.addType(CLOUDEnum.UNIX.getConceptURI());
                        	 OS.setHasLabel("Linux");
                         }
                         else if(OSName.equals("rhel"))
                         {
                        	 OS = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
                        	 OS.addType(CLOUDEnum.WINDOWS.getConceptURI());
                        	 OS.setHasLabel("Red Hat Enterprise Linux");
                         }
                         else if(OSName.equals("sles"))
                         {
                        	 OS = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
                        	 OS.addType(CLOUDEnum.WINDOWS.getConceptURI());
                        	 OS.setHasLabel("SUSE Linux Enterprise Server");
                         }
                         else if(OSName.equals("mswin"))
                         {
                        	 OS = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
                        	 OS.addType(CLOUDEnum.WINDOWS.getConceptURI());
                        	 OS.setHasLabel("Windows");
                         }
                         else if(OSName.equals("mswinSQL"))
                         {
                        	 OS = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
                        	 OS.addType(CLOUDEnum.WINDOWS.getConceptURI());
                        	 OS.setHasLabel("Windows");
                        	 
                        	 Platform = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
                        	 Platform.addType(CLOUDEnum.PLATFORM.getConceptURI());
                        	 OS.setHasLabel("MySql Standard");
                         }
                         else if(OSName.equals("mswinSQLWeb"))
                         {
                        	 OS = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
                        	 OS.addType(CLOUDEnum.WINDOWS.getConceptURI());
                        	 OS.setHasLabel("Windows");
                        	 
                        	 Platform = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
                        	 Platform.addType(CLOUDEnum.PLATFORM.getConceptURI());
                        	 OS.setHasLabel("MySql Web");
                         }
                         
                         JSONObject osprice = (JSONObject) osj.get("prices");
                         
                         Pattern p = Pattern.compile(".*[a-zA-Z].*");   
                         Matcher m = p.matcher(((String)osprice.get("USD")));    
                         if(m.find())    
                             continue;

                 		Double ospricej = Double.parseDouble((String) osprice.get("USD"));
                 		
                 		 for(Service s : basicServices)
                         {
                        	 if(s.getName().equals(instname))
                        	 {
                        		 Service scp = new Service(s);
                        		 scp.setName(scp.getName().replaceAll("_TIME\\d+.*", "") + "_TIME"+System.nanoTime());
                        		 
                        		 QualitativeValue ServiceType =  new QualitativeValue("qv"+"_TIME"+System.nanoTime());
                     			ServiceType.addType(CLOUDEnum.FEATURE.getConceptURI());
                     			ServiceType.setHasLabel("Virtual Machine");
                     			scp.addQualFeature(ServiceType);
                        		 
                        		 scp.addQualitativeFeature(OS);
                        		 scp.addQualitativeFeature(Location);
                        		// System.out.println(""+scp.getName() + " at "+Location.getHasValue() + " with price:  "+ospricej);
                        		 if(Platform != null)
                        			 scp.addQualitativeFeature(Platform);
                        		 //create offering
                        		 Offering of = new Offering();
                        		 of.setName(scp.getName()+OSName+"-AmazonOnDemandInstance" +  "_TIME" +System.nanoTime());
                        		 of.addService(scp);
                        		 //create the PricePlan for the offering
                        		 PricePlan pp = new PricePlan("pp_TIME"+System.nanoTime());
                        		 of.setPricePlan(pp);
                        		 addOnDemandPriceComponents(pp,ospricej,region);
                        		 offs.add(of);
                        	 }
                         }
                     }
                 }
             }
             
             jmodel.setOfferings(offs); 
		}
	}
	
	
	
	public void addOnDemandPriceComponents(PricePlan pp, double priceph,String region) throws MalformedURLException, IOException, ParseException
	{
		addPricePerHourComponent(pp,priceph);
		addDataTransferralsPriceComponent(pp,region);
	}
	
	public void addPricePerHourComponent(PricePlan pp,double priceph)
	{
		PriceComponent pc_hourly = new PriceComponent();//Component that is responsible for calculating the price per hour of the instance
		pp.addPriceComponent(pc_hourly);
		pc_hourly.setName("HourlyPC" + "_TIME" +System.nanoTime());
		
		PriceFunction pf_hourly = new PriceFunction();
		pc_hourly.setPriceFunction(pf_hourly);
		pf_hourly.setName("hourly_cost_function" +  "_TIME" +System.nanoTime());
		
		Usage NumberOfHours = new Usage();
		pf_hourly.addUsageVariable(NumberOfHours);
		NumberOfHours.setName("usagehours"+"TIME" +System.nanoTime());
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
		traffic_pc.setName("DataCostPC-PP" + "_TIME" +System.nanoTime());
		
		PriceFunction data_cost_pf = new PriceFunction();
		traffic_pc.setPriceFunction(data_cost_pf);
		data_cost_pf.setName("data_transferrals_cost" +  "_TIME" +System.nanoTime());
		
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
                        		price10.setName("price10"+"TIME"+System.nanoTime());
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
                        		price100.setName("price100"+"TIME" +System.nanoTime());
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
                        		price350.setName("price350"+"TIME" +System.nanoTime());
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

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
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
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

import com.google.gson.Gson;
import com.hp.hpl.jena.rdf.model.Model;

import exceptions.InvalidLinkedUSDLModelException;
import gsonModels.AmazonGlacier_DataTransferJSONModel;
import gsonModels.AmazonGlacier_RequestsJSONModel;
import gsonModels.AmazonGlacier_StorageJSONModel;



public class AmazonGlacier {

	private static String datatransf_url ="https://a0.awsstatic.com/pricing/1/glacier/pricing-data-transfer.min.js";
	private static String storage_url ="https://a0.awsstatic.com/pricing/1/glacier/pricing-storage.min.js";
	private static String requests_url ="https://a0.awsstatic.com/pricing/1/glacier/pricing-requests.min.js";
	private static boolean localCopy = false;
	public static void main(String[] args) throws MalformedURLException, IOException, ParseException, InvalidLinkedUSDLModelException
	{
			Service s = basicAmazonGlacier();
			ArrayList<Offering> ofs = AddPricingData(s);
			
			LinkedUSDLModel jmodel;

			jmodel = LinkedUSDLModelFactory.createEmptyModel();
			
			jmodel.setBaseURI("http://PricingAPIAmazonGlacierOfferings.com");
			jmodel.setOfferings(ofs);
			Model instance = jmodel.WriteToModel();//transform the java models to a semantic representation

			File outputFile = new File("./ProviderSets/AmazonGlacier_fullset.ttl");
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}

			FileOutputStream out = new FileOutputStream(outputFile);
			instance.write(out, "Turtle");
			out.close();
	}
	
	public static void createLocalCopy(String JsonString,int type) throws IOException
	{
		File file = null;
		if(type==0)
			file = new File("./HTMLCopies/AmazonGlacier_datatransf.json");
		else if(type==1)
			file = new File("./HTMLCopies/AmazonGlacier_storage.json");
		else if(type==2)
			file = new File("./HTMLCopies/AmazonGlacier_requests.json");

		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(JsonString);
		bw.close();
	}
	
	@SuppressWarnings("resource")
	public static ArrayList<Offering> AddPricingData(Service s) throws MalformedURLException, IOException
	{
		ArrayList<Offering> ofs = new ArrayList<Offering>();
		
//		String JsonString = IOUtils.toString(new URL(datatransf_url).openStream());
//		JsonString = JsonString.substring(201, JsonString.length() - 2);// remove some unnecessary characters
		
		String JsonString = new Scanner(new File("./HTMLCopies/AmazonGlacier_datatransf.json")).useDelimiter("\\Z").next();
		
//		System.out.println(JsonString);
		if(localCopy)
			createLocalCopy(JsonString,0);
		
		AmazonGlacier_DataTransferJSONModel transfer = new Gson().fromJson(JsonString, AmazonGlacier_DataTransferJSONModel.class);
		
		addDataTransferPricing(s,ofs,transfer);
//		JsonString = IOUtils.toString(new URL(storage_url).openStream());
//		JsonString = JsonString.substring(201, JsonString.length() - 2);// remove some unnecessary characters
		
		JsonString = new Scanner(new File("./HTMLCopies/AmazonGlacier_storage.json")).useDelimiter("\\Z").next();
		
//		System.out.println(JsonString);
		if(localCopy)
			createLocalCopy(JsonString,1);
		
		AmazonGlacier_StorageJSONModel storage = new Gson().fromJson(JsonString, AmazonGlacier_StorageJSONModel.class);
		
		addStoragePricing(ofs,storage);
//		JsonString = IOUtils.toString(new URL(requests_url).openStream());
//		JsonString = JsonString.substring(201, JsonString.length() - 2);// remove some unnecessary characters
		
		JsonString = new Scanner(new File("./HTMLCopies/AmazonGlacier_requests.json")).useDelimiter("\\Z").next();
		
//		System.out.println(JsonString);
		if(localCopy)
			createLocalCopy(JsonString,2);
		
		AmazonGlacier_RequestsJSONModel req = new Gson().fromJson(JsonString, AmazonGlacier_RequestsJSONModel.class);
		
		addRequestPricing(ofs,req);
		
		
		return ofs;
	}
	
	private static void addRequestPricing(ArrayList<Offering> container,AmazonGlacier_RequestsJSONModel data)
	{
		double us_east_fee= 0.01;
		double us_west2_fee = 0.01;
		double us_west_fee = 0.011;
		double ireland_fee = 0.011;
		double tokyo_fee = 0.0114;
		double syd_fee = 0.012;
		
		for(AmazonGlacier_RequestsJSONModel.Region r : data.getConfig().getRegions())
		{
			for(Offering of : container)
			{
				for(QualitativeValue loc : of.getIncludes().get(0).getQualfeatures())
				{
					for(String type : loc.getTypes())
					{
						if(type.equals(CLOUDEnum.LOCATION.getConceptURI()))
						{
							if(loc.getHasValue().equals(r.getRegion()))
							{
								PriceComponent requests_pc = new PriceComponent();//Component responsible for calculating the total price to pay related only to the Data transferral on Amazon EC2
								of.getPricePlan().addPriceComponent(requests_pc);
								requests_pc.setName("RequestsPC-PP" + "_TIME" +System.nanoTime());

								PriceFunction requests_cost_pf = new PriceFunction();
								requests_pc.setPriceFunction(requests_cost_pf);
								requests_cost_pf.setName("glacier_requests_cost" +  "_TIME" +System.nanoTime());
								
//								System.out.println(requests_cost_pf.getName());
								
								Provider fee = new Provider();
								requests_cost_pf.addProviderVariable(fee);
								fee.setName("requestsfee"+"TIME"+System.nanoTime());
		                		QuantitativeValue valb = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
		                		
		                		for(AmazonGlacier_RequestsJSONModel.Tier t : r.getTiers())
		                		{
		                			if(t.getName().equals("uploadRetrieval"))
		                			{
		                				double price = Double.parseDouble(t.getPrices().getUSD());
		                				fee.setValue(valb);
				                		valb.setValue(price);
				                		valb.setUnitOfMeasurement("USD");
		                			}
		                		}
		                		
		                		Usage number_requests = new Usage();
		                		requests_cost_pf.addUsageVariable(number_requests);
		    					number_requests.setName("nrequests"+"TIME" +System.nanoTime());
		    					number_requests.setComment("Total number of Upload/Retrieval requests that you expect to perform on Amazon Glacier.");
		    					
		    					requests_cost_pf.setStringFunction("("+number_requests.getName() + " * "+fee.getName()+" )" + " / 1000 ");
		    					
		    					
		    					
		    					PriceComponent requestData_pc = new PriceComponent();//Component responsible for calculating the total price to pay related only to the Data transferral on Amazon EC2
								of.getPricePlan().addPriceComponent(requestData_pc);
								requestData_pc.setName("RequestDataPC-PP" + "_TIME" +System.nanoTime());
								
								PriceFunction requestData_cost_pf = new PriceFunction();
								requestData_pc.setPriceFunction(requestData_cost_pf);
								requestData_cost_pf.setName("glacier_datarequest_cost" +  "_TIME" +System.nanoTime());
								
								Usage gbs_to_store = new Usage();
								requestData_cost_pf.addUsageVariable(gbs_to_store);
		    					gbs_to_store.setName("gbTOStore"+"TIME" +System.nanoTime());
		    					gbs_to_store.setComment("Total GB of data that you expect to store on Amazon Glacier.");
		    					
		    					Usage time_to_fetch = new Usage();
								requestData_cost_pf.addUsageVariable(time_to_fetch);
								time_to_fetch.setName("retrievalTime"+"TIME" +System.nanoTime());
								time_to_fetch.setComment("Length of time for data retrieval. (Hours)");
								
								Usage data_to_fetch = new Usage();
								requestData_cost_pf.addUsageVariable(data_to_fetch);
								data_to_fetch.setName("retrievaData"+"TIME" +System.nanoTime());
								data_to_fetch.setComment("Amount of data to retrieve (GB).");
								
								if(r.getRegion().equalsIgnoreCase("us-east-virginia"))
								{
									requestData_cost_pf.setStringFunction("  IF ("+time_to_fetch.getName()+">=  24) ; (   ("+data_to_fetch.getName()+"   /  "+time_to_fetch.getName()+") - ( ( "+gbs_to_store.getName()+" * 0.05 ) / (30*24) )           ) * "+us_east_fee+" * 720 ~"
											+ " ELSEIF"+time_to_fetch.getName()+" < 24 "+" ;  (("+data_to_fetch.getName()+" / "+time_to_fetch.getName()+") - ( ( "+gbs_to_store.getName()+" * 0.05 )  / (30*"+time_to_fetch.getName()+" ))           ) *  "+us_east_fee+" * 720 "
											);
								}
								else if(r.getRegion().equalsIgnoreCase("us-west-2"))
								{
									requestData_cost_pf.setStringFunction("  IF ("+time_to_fetch.getName()+">=  24) ; (   ("+data_to_fetch.getName()+"   /  "+time_to_fetch.getName()+") - ( ( "+gbs_to_store.getName()+" * 0.05 ) / (30*24) )           ) * "+us_west2_fee+" * 720 ~"
											+ " ELSEIF"+time_to_fetch.getName()+" < 24 "+" ;  (("+data_to_fetch.getName()+" / "+time_to_fetch.getName()+") - ( ( "+gbs_to_store.getName()+" * 0.05 )  / (30*"+time_to_fetch.getName()+" ))           ) *  "+us_west2_fee+" * 720 "
											);
								}
								else if(r.getRegion().equalsIgnoreCase("us-west"))
								{
									requestData_cost_pf.setStringFunction("  IF ("+time_to_fetch.getName()+">=  24) ; (   ("+data_to_fetch.getName()+"   /  "+time_to_fetch.getName()+") - ( ( "+gbs_to_store.getName()+" * 0.05 ) / (30*24 ))           ) * "+us_west_fee+" * 720 ~"
											+ " ELSEIF"+time_to_fetch.getName()+" < 24 "+" ;  (("+data_to_fetch.getName()+" / "+time_to_fetch.getName()+") - ( ( "+gbs_to_store.getName()+" * 0.05 )  / (30*"+time_to_fetch.getName()+" ))           ) *  "+us_west_fee+" * 720 "
											);
								}
								else if(r.getRegion().equalsIgnoreCase("eu-ireland"))
								{
									requestData_cost_pf.setStringFunction("  IF ("+time_to_fetch.getName()+">=  24) ; (   ("+data_to_fetch.getName()+"   /  "+time_to_fetch.getName()+") - ( ( "+gbs_to_store.getName()+" * 0.05 ) / (30*24 ))           ) * "+ireland_fee+" * 720 ~"
											+ " ELSEIF"+time_to_fetch.getName()+" < 24 "+" ;  (("+data_to_fetch.getName()+" / "+time_to_fetch.getName()+") - ( ( "+gbs_to_store.getName()+" * 0.05 )  / (30*"+time_to_fetch.getName()+" ))           ) *  "+ireland_fee+" * 720 "
											);
								}
								else if(r.getRegion().equalsIgnoreCase("apac-tokyo"))
								{
									requestData_cost_pf.setStringFunction("  IF ("+time_to_fetch.getName()+">=  24) ; (   ("+data_to_fetch.getName()+"  /  "+time_to_fetch.getName()+") - ( ( "+gbs_to_store.getName()+" * 0.05 ) / (30*24) )           ) * "+tokyo_fee+" * 720 ~"
											+ " ELSEIF"+time_to_fetch.getName()+" < 24 "+" ;  (("+data_to_fetch.getName()+" / "+time_to_fetch.getName()+") - ( ( "+gbs_to_store.getName()+" * 0.05 )  / (30*"+time_to_fetch.getName()+" ))           ) *  "+tokyo_fee+" * 720 "
											);
								}
								else if(r.getRegion().equalsIgnoreCase("apac-syd"))
								{
									requestData_cost_pf.setStringFunction("  IF ("+time_to_fetch.getName()+">=  24) ; (   ("+data_to_fetch.getName()+"  /  "+time_to_fetch.getName()+") - ( ( "+gbs_to_store.getName()+" * 0.05 ) / (30*24) )           ) * "+syd_fee+" * 720 ~"
											+ " ELSEIF"+time_to_fetch.getName()+" < 24 "+" ;  (("+data_to_fetch.getName()+" / "+time_to_fetch.getName()+") - ( ( "+gbs_to_store.getName()+" * 0.05 )  / (30*"+time_to_fetch.getName()+" ))           ) *  "+syd_fee+" * 720 "
											);
								}
							}
						}
					}
				}
			}
		}
		
	}
	
	private static void addStoragePricing(ArrayList<Offering> container,AmazonGlacier_StorageJSONModel data)
	{
		for(AmazonGlacier_StorageJSONModel.Region r : data.getConfig().getRegions())
		{
			for(Offering of : container)
			{
				for(QualitativeValue loc : of.getIncludes().get(0).getQualfeatures())
				{
					for(String type : loc.getTypes())
					{
						if(type.equals(CLOUDEnum.LOCATION.getConceptURI()))
						{
							if(loc.getHasValue().equals(r.getRegion()))
							{
								PriceComponent storage_pc = new PriceComponent();//Component responsible for calculating the total price to pay related only to the Data transferral on Amazon EC2
								of.getPricePlan().addPriceComponent(storage_pc);
								storage_pc.setName("StoragePC-PP" + "_TIME" +System.nanoTime());
								
								PriceFunction storage_cost_pf = new PriceFunction();
								storage_pc.setPriceFunction(storage_cost_pf);
								storage_cost_pf.setName("glacier_storage_cost" +  "_TIME" +System.nanoTime());
								
								Provider fee = new Provider();
								storage_cost_pf.addProviderVariable(fee);
								fee.setName("storagefee"+"TIME"+System.nanoTime());
		                		QuantitativeValue valb = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
		                		
		                		for(AmazonGlacier_StorageJSONModel.Type t : r.getTypes())
		                		{
		                			for(AmazonGlacier_StorageJSONModel.Value val : t.getValues())
		                			{
		                				double price = Double.parseDouble(val.getPrices().getUSD());
		                				fee.setValue(valb);
				                		valb.setValue(price);
				                		valb.setUnitOfMeasurement("USD");
		                			}
		                		}
		                		
		                		Usage gbs_to_store = new Usage();
		    					storage_cost_pf.addUsageVariable(gbs_to_store);
		    					gbs_to_store.setName("gbTOStore"+"TIME" +System.nanoTime());
		    					gbs_to_store.setComment("Total GB of data that you expect to store on Amazon Glacier.");
		    					
		    					Usage duration = new Usage();
		    					storage_cost_pf.addUsageVariable(duration);
		    					duration.setName("usagedays"+"TIME" +System.nanoTime());
		    					duration.setComment("How long will you be keeping the data on AmazonGlacier? (Days)");
		    					
		    					storage_cost_pf.setStringFunction("("+duration.getName() + " / 30 )" + " * " + gbs_to_store.getName() + " * " + fee.getName());

							}
						}
					}
				}
			}
		}
	}
	
	private static void addDataTransferPricing(Service s, ArrayList<Offering> container,AmazonGlacier_DataTransferJSONModel data)
	{
			
		for(AmazonGlacier_DataTransferJSONModel.Region r : data.getConfig().getRegions())
		{
			Service scp = new Service(s);
			scp.setName(scp.getName().replaceAll("_TIME\\d+.*", "") + "_TIME"+System.nanoTime());
			
			QualitativeValue ServiceType =  new QualitativeValue("qv"+"_TIME"+System.nanoTime());
			ServiceType.addType(CLOUDEnum.FEATURE.getConceptURI());
			ServiceType.setHasLabel("Database - Long duration");
			scp.addQualFeature(ServiceType);
			
			
			
			 Offering of = new Offering();
			 of.setName(scp.getName()+"-AmazonGlacierInstance" +  "_TIME" +System.nanoTime());
			 of.addService(scp);
			
			PricePlan pp = new PricePlan("pp-datatransfer-AmazonGlacier"+"_TIME"+System.nanoTime());
			of.setPricePlan(pp);
			
			PriceComponent traffic_pc = new PriceComponent();//Component responsible for calculating the total price to pay related only to the Data transferral on Amazon EC2
			pp.addPriceComponent(traffic_pc);
			traffic_pc.setName("DataCostPC-PP" + "_TIME" +System.nanoTime());
			
			PriceFunction data_cost_pf = new PriceFunction();
			traffic_pc.setPriceFunction(data_cost_pf);
			data_cost_pf.setName("glacier_data_transferrals_cost" +  "_TIME" +System.nanoTime());
			
			QualitativeValue Location = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
			Location.addType(CLOUDEnum.LOCATION.getConceptURI());
			Location.setHasLabel(r.getRegion());
			scp.addQualitativeFeature(Location);
			
			
			Provider price10=null,price40=null,price100=null,price350=null;
			for(AmazonGlacier_DataTransferJSONModel.Type t : r.getTypes())
			{
				if(t.getName().equalsIgnoreCase("dataXferOutGlacier"))
				{
					for(AmazonGlacier_DataTransferJSONModel.Tier tier : t.getTiers())
					{
						if(tier.getName().equals("upTo10TBout"))
	                    {

	                    	double price = Double.parseDouble(tier.getPrices().getUSD());
	                    	
	                    	price10 = new Provider();
	                		data_cost_pf.addProviderVariable(price10);
	                		price10.setName("price10"+"TIME"+System.nanoTime());
	                		QuantitativeValue valb = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
	                		price10.setValue(valb);
	                		valb.setValue(price);
	                		valb.setUnitOfMeasurement("USD");
	                    }
	                    else if(tier.getName().equals("next40TBout"))
	                    {
	                    	double price = Double.parseDouble(tier.getPrices().getUSD());
	                    	
	                    	price40 = new Provider();
	                		data_cost_pf.addProviderVariable(price40);
	                		price40.setName("price40"+"TIME" +System.nanoTime());
	                		QuantitativeValue valc = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
	                		price40.setValue(valc);
	                		valc.setValue(price);
	                		valc.setUnitOfMeasurement("USD");
	                    }
	                    else if(tier.getName().equals("next100TBout"))
	                    {
	                    	double price = Double.parseDouble(tier.getPrices().getUSD());
	                    	
	                    	price100 = new Provider();
	                		data_cost_pf.addProviderVariable(price100);
	                		price100.setName("price100"+"TIME" +System.nanoTime());
	                		QuantitativeValue vald = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
	                		price100.setValue(vald);
	                		vald.setValue(price);
	                		vald.setUnitOfMeasurement("USD");
	                    }
	                    
	                    else if(tier.getName().equals("next350TBout"))
	                    {
	                    	double price = Double.parseDouble(tier.getPrices().getUSD());
	                    	
	                    	price350 = new Provider();
	                		data_cost_pf.addProviderVariable(price350);
	                		price350.setName("price350"+"TIME" +System.nanoTime());
	                		QuantitativeValue vale = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
	                		price350.setValue(vale);
	                		vale.setValue(price);
	                		vale.setUnitOfMeasurement("USD");
	                    }
					}
					
					
					Usage gbout = new Usage();
					data_cost_pf.addUsageVariable(gbout);
					gbout.setName("gbout"+"TIME" +System.nanoTime());
					gbout.setComment("Total GB of data that you expect to send out from Amazon Glacier to the internet.");
					
					data_cost_pf.setStringFunction("  IF ("+gbout.getName()+"<= 1) ; 1 * 0.00 ~"
							+ " ELSEIF"+gbout.getName()+" > 1 && "+gbout.getName()+" <= 10*1024 ; 1*0.00 + ("+gbout.getName()+"-1) * "+price10.getName()+" ~ "
							+ "ELSEIF ("+gbout.getName()+" > 10*1024) && ("+gbout.getName()+"<= 40*1024) ; 1*0.00 + 10*1024*"+price10.getName()+" + ("+gbout.getName()+"-10*1024-1)*"+price40.getName()+" ~ "
							+ "ELSEIF ("+gbout.getName()+" >= 40*1024) && ("+gbout.getName()+" < 100*1024) ; 1*0.00 + 10*1024*"+price10.getName()+" + 40*1024*"+price40.getName()+" + ("+gbout.getName()+"-1-10*1024-40*1024)*"+price100.getName()+" ~ "
							+ "ELSEIF ("+gbout.getName()+" >= 100*1024) && ("+gbout.getName()+" < 350*1024) ; 1*0.00 + 10*1024*"+price10.getName()+" + 40*1024*"+price40.getName()+" + 100*1024*"+price100.getName()+" + ("+gbout.getName()+"-1-10*1024-40*1024-100*1024)*"+price350.getName()+"");
                    
				}
			}
			container.add(of);
		}
	}
	
	//manual population
	public static Service basicAmazonGlacier()
	{
		// extracted information from https://aws.amazon.com/glacier/ 
		Service s = new Service();
		s.setName("AmazonGlacier");
		
		
		QualitativeValue SSL =  new QualitativeValue("qv"+"_TIME"+System.nanoTime());
		SSL.addType(CLOUDEnum.SSL.getConceptURI());
		SSL.setHasLabel("yes");
		SSL.setComment("Amazon Glacier supports secure transfer of your data over Secure Sockets Layer (SSL)");
		s.addQualFeature(SSL);
		
		QualitativeValue Encryption = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
		Encryption.addType(CLOUDEnum.ENCRYPTION.getConceptURI());
		Encryption.setHasLabel("Advanced Encryption Standard (AES) 256-bit");
		s.addQualFeature(Encryption);
		
		
		QuantitativeValue Durability = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
		Durability.addType(CLOUDEnum.DURABILITY.getConceptURI());
		Durability.setValue(99.999999999);
		s.addQuantFeature(Durability);
		
		QuantitativeValue StorageCapacity = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
		StorageCapacity.addType(CLOUDEnum.STORAGECAPACITY.getConceptURI());
		StorageCapacity.setValue(Double.MAX_VALUE);
		s.addQuantFeature(StorageCapacity);
		
		QualitativeValue API = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
		API.addType(CLOUDEnum.API.getConceptURI());
		API.setHasLabel("REST");
		s.addQualFeature(API);
		
		QuantitativeValue PUTReq = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
		PUTReq.addType(CLOUDEnum.PUTREQUESTS.getConceptURI());
		PUTReq.setValue(Double.MAX_VALUE);
		s.addQuantFeature(PUTReq);
		
		
		QuantitativeValue POSTReq = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
		POSTReq.addType(CLOUDEnum.POSTREQUESTS.getConceptURI());
		POSTReq.setValue(Double.MAX_VALUE);
		s.addQuantFeature(POSTReq);
		
		QuantitativeValue DELETEReq = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
		DELETEReq.addType(CLOUDEnum.DELETEREQUESTS.getConceptURI());
		DELETEReq.setValue(Double.MAX_VALUE);
		s.addQuantFeature(DELETEReq);
		
		QuantitativeValue LISTReq = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
		LISTReq.addType(CLOUDEnum.LISTREQUESTS.getConceptURI());
		LISTReq.setValue(Double.MAX_VALUE);
		s.addQuantFeature(LISTReq);
		
		QuantitativeValue GETReq = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
		GETReq.addType(CLOUDEnum.GETREQUESTS.getConceptURI());
		GETReq.setValue(Double.MAX_VALUE);
		s.addQuantFeature(GETReq);
		
		return s;
	}
}

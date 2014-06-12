package Amazon;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
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
import gsonModels.AmazonRDSDBCostJSONModel;
import gsonModels.AmazonRDSDataTransferJSONModel;
import gsonModels.AmazonRDSStorage_IOPSJSONModel;


//-------------------------------------------MYSQL----------------------------------------------
//On Demand:
//
//Single:https://a0.awsstatic.com/pricing/1/rds/mysql/pricing-standard-deployments.min.js
//Multi: https://a0.awsstatic.com/pricing/1/rds/mysql/pricing-multiAZ-deployments.min.js
//
//Reserved:
//--------Light---------
//Single and Multi: https://a0.awsstatic.com/pricing/1/rds/mysql/pricing-light-utilization-reserved-instances.min.js
//
//--------Medium--------
//Single and Multi: https://a0.awsstatic.com/pricing/1/rds/mysql/pricing-medium-utilization-reserved-instances.min.js
//
//-------Heavy----------
//Single and Multi: https://a0.awsstatic.com/pricing/1/rds/mysql/pricing-heavy-utilization-reserved-instances.min.js
//
//Standard Storage: 
//Single: https://a0.awsstatic.com/pricing/1/rds/mysql/pricing-piops-standard-deploy.min.js
//Multi: https://a0.awsstatic.com/pricing/1/rds/mysql/pricing-piops-multiAZ-deploy.min.js
//
//Provisioned:
//Single:https://a0.awsstatic.com/pricing/1/rds/mysql/pricing-provisioned-db-standard-deploy.min.js
//Multi:https://a0.awsstatic.com/pricing/1/rds/mysql/pricing-provisioned-db-multiAZ-deploy.min.js
//
//Data Transfer: https://a0.awsstatic.com/pricing/1/rds/mysql/pricing-data-transfer.min.js

public class AmazonRDSMySQL {
	private static String onDemandSingle="https://a0.awsstatic.com/pricing/1/rds/mysql/pricing-standard-deployments.min.js";
	private static String onDemandMulti="https://a0.awsstatic.com/pricing/1/rds/mysql/pricing-multiAZ-deployments.min.js";
	
	private static String standardStorageSingle = "https://a0.awsstatic.com/pricing/1/rds/mysql/pricing-piops-standard-deploy.min.js";
	private static String standardStorageMulti = " https://a0.awsstatic.com/pricing/1/rds/mysql/pricing-piops-multiAZ-deploy.min.js";
	
	private static String provisionedIOPSSingle = "https://a0.awsstatic.com/pricing/1/rds/mysql/pricing-provisioned-db-standard-deploy.min.js";
	private static String provisionedIOPSMulti = "https://a0.awsstatic.com/pricing/1/rds/mysql/pricing-provisioned-db-multiAZ-deploy.min.js";
	
	private static String dataTransfer = "https://a0.awsstatic.com/pricing/1/rds/mysql/pricing-data-transfer.min.js";
	
	private static boolean localCopy = false;
	public static void main(String[] args) throws MalformedURLException, IOException, ParseException, InvalidLinkedUSDLModelException
	{
		AmazonRDSBaseServices worker = new AmazonRDSBaseServices();

		
		ArrayList<Service> basicServ =worker.baseServices();
		
		ArrayList<Offering> ofs = new ArrayList<Offering>();
		
		ofs.addAll(buildSingleOnDemandOfferings(basicServ));
		
		
		System.out.println(ofs.size());
		
//		for(Offering ofz : ofs)
//			System.out.println(ofz.getName());
//		
		LinkedUSDLModel jmodel;

		jmodel = LinkedUSDLModelFactory.createEmptyModel();
		
		jmodel.setBaseURI("http://PricingAPIAmazonRDS-MySQLOfferings.com");
		jmodel.setOfferings(ofs);
		Model instance = jmodel.WriteToModel();//transform the java models to a semantic representation

		File outputFile = new File("./ProviderSets/AmazonRDS-MySQL.ttl");
		if (!outputFile.exists()) {
			outputFile.createNewFile();
		}

		FileOutputStream out = new FileOutputStream(outputFile);
		instance.write(out, "Turtle");
		out.close();
	}

	@SuppressWarnings("resource")
	private static ArrayList<Offering> buildSingleOnDemandOfferings(ArrayList<Service> basicServ) throws MalformedURLException, IOException {
		ArrayList<Offering> basic_ofs = new ArrayList<Offering>();
		
		/////////////////////////////////////////////////////////////////// Standard Storage /////////////////////////////////////////////////////////////
		
//		JsonString = IOUtils.toString(new URL(standardStorageSingle).openStream());
//		JsonString = JsonString.substring(201, JsonString.length() - 2);// remove some unnecessary characters
		
		String JsonString = new Scanner(new File("./HTMLCopies/AmazonRDSMySQL_Single-StandardStorage.json")).useDelimiter("\\Z").next();
//		System.out.println(JsonString);
		if(localCopy)
			createLocalCopy(JsonString,"AmazonRDSMySQL_Single-StandardStorage");
		
		AmazonRDSStorage_IOPSJSONModel standardstorage_data = new Gson().fromJson(JsonString, AmazonRDSStorage_IOPSJSONModel.class);
		
		ArrayList<Offering> ofs_ss = StandardStorageOfferings(basicServ,standardstorage_data);
		
		///////////////////////////////////////////////////////////////////Provisioned ///////////////////////////////////////////////////////////////////////
		
//		JsonString = IOUtils.toString(new URL(provisionedIOPSSingle).openStream());
//		JsonString = JsonString.substring(201, JsonString.length() - 2);// remove some unnecessary characters
		
		JsonString = new Scanner(new File("./HTMLCopies/AmazonRDSMySQL_Single-ProvisionedIOPS.json")).useDelimiter("\\Z").next();
//		System.out.println(JsonString);
		if(localCopy)
			createLocalCopy(JsonString,"AmazonRDSMySQL_Single-ProvisionedIOPS");
		
		AmazonRDSStorage_IOPSJSONModel provisioned_data = new Gson().fromJson(JsonString, AmazonRDSStorage_IOPSJSONModel.class);
		
		ArrayList<Offering> ofs_provisioned = ProvisionedOfferings(basicServ,provisioned_data);
		
		ArrayList<Offering> ofs = new ArrayList<Offering>();
		
		ofs.addAll(ofs_ss);
		ofs.addAll(ofs_provisioned);
		System.out.println("Total:"+ofs.size());
		//////////////////////////////////////////////////////////////// DB COST /////////////////////////////////////////////////////////////////////////////////////
		
//		String JsonString = IOUtils.toString(new URL(onDemandSingle).openStream());
//		JsonString = JsonString.substring(201, JsonString.length() - 2);// remove some unnecessary characters
		
		JsonString = new Scanner(new File("./HTMLCopies/AmazonRDSMySQL_SingleDBCost.json")).useDelimiter("\\Z").next();
//		System.out.println(JsonString);
		if(localCopy)
			createLocalCopy(JsonString,"AmazonRDSMySQL_SingleDBCost");
		
		AmazonRDSDBCostJSONModel dbcost_data = new Gson().fromJson(JsonString, AmazonRDSDBCostJSONModel.class);
		
		addDBInstanceCost(ofs,dbcost_data);
		
		///////////////////////////////////////////////////////////////////// DATA TRANSFERRALS ////////////////////////////////////////////////////////////////////
		
//		JsonString = IOUtils.toString(new URL(dataTransfer).openStream());
//		JsonString = JsonString.substring(201, JsonString.length() - 2);// remove some unnecessary characters
		
		JsonString = new Scanner(new File("./HTMLCopies/AmazonRDSMySQL_DataTransfer.json")).useDelimiter("\\Z").next();
//		System.out.println(JsonString);
		if(localCopy)
			createLocalCopy(JsonString,"AmazonRDSMySQL_DataTransfer");
		
		AmazonRDSDataTransferJSONModel datatransf_data = new Gson().fromJson(JsonString, AmazonRDSDataTransferJSONModel.class);
		
		addDataTransfPricing(ofs,datatransf_data);
		
		return ofs;
	}
	
	
	
	private static void addDataTransfPricing(ArrayList<Offering> ofs,AmazonRDSDataTransferJSONModel datatransf_data) {
		
		for(Offering of : ofs)
		{
			PricePlan pp = of.getPricePlan();
			
			PriceComponent traffic_pc = new PriceComponent();//Component responsible for calculating the total price to pay related only to the Data transferral on Amazon EC2
			pp.addPriceComponent(traffic_pc);
			traffic_pc.setName("DataCostPC-PP" + "_TIME" +System.nanoTime());
			
			PriceFunction data_cost_pf = new PriceFunction();
			traffic_pc.setPriceFunction(data_cost_pf);
			data_cost_pf.setName("AmazonRDS_data_transferrals_cost" +  "_TIME" +System.nanoTime());
			Provider price10=null,price40=null,price100=null,price350=null;
			for(AmazonRDSDataTransferJSONModel.Region reg : datatransf_data.getConfig().getRegions())
			{
				for(QualitativeValue qv : of.getIncludes().get(0).getQualfeatures()){
					for(String type : qv.getTypes())
					{
						if(type.equals(CLOUDEnum.LOCATION.getConceptURI()))
						{
							if(qv.getHasValue().equals(reg.getRegion()))
							{
								for(AmazonRDSDataTransferJSONModel.Type typ : reg.getTypes())
								{
									if(typ.getName().equals("dataXferOutInternetRDS"))
									{
										for(AmazonRDSDataTransferJSONModel.Tier tier : typ.getTiers())
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
										gbout.setComment("Total GB of data that you expect to send out from Amazon RDS to the internet.");
										
										data_cost_pf.setStringFunction("  IF ("+gbout.getName()+"<= 1) ; 1 * 0.00 ~"
												+ " ELSEIF"+gbout.getName()+" > 1 && "+gbout.getName()+" <= 10*1024 ; 1*0.00 + ("+gbout.getName()+"-1) * "+price10.getName()+" ~ "
												+ "ELSEIF ("+gbout.getName()+" > 10*1024) && ("+gbout.getName()+"<= 40*1024) ; 1*0.00 + 10*1024*"+price10.getName()+" + ("+gbout.getName()+"-10*1024-1)*"+price40.getName()+" ~ "
												+ "ELSEIF ("+gbout.getName()+" >= 40*1024) && ("+gbout.getName()+" < 100*1024) ; 1*0.00 + 10*1024*"+price10.getName()+" + 40*1024*"+price40.getName()+" + ("+gbout.getName()+"-1-10*1024-40*1024)*"+price100.getName()+" ~ "
												+ "ELSEIF ("+gbout.getName()+" >= 100*1024) && ("+gbout.getName()+" < 350*1024) ; 1*0.00 + 10*1024*"+price10.getName()+" + 40*1024*"+price40.getName()+" + 100*1024*"+price100.getName()+" + ("+gbout.getName()+"-1-10*1024-40*1024-100*1024)*"+price350.getName()+"");
					                    
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private static ArrayList<Offering> ProvisionedOfferings(ArrayList<Service> basicServ,AmazonRDSStorage_IOPSJSONModel provisioned_data) {
		ArrayList<Offering> provOfs = new ArrayList<Offering>();
		
		for(AmazonRDSStorage_IOPSJSONModel.Region reg : provisioned_data.getConfig().getRegions())
		{
			for(Service serv : basicServ)
			{
				Service scp = new Service(serv);
				scp.setName(scp.getName().replaceAll("_TIME\\d+.*", "") + "_TIME"+System.nanoTime());
				
				QualitativeValue ServiceType =  new QualitativeValue("qv"+"_TIME"+System.nanoTime());
     			ServiceType.addType(CLOUDEnum.FEATURE.getConceptURI());
     			ServiceType.setHasLabel("Database");
     			scp.addQualFeature(ServiceType);
				
				 Offering of = new Offering();
				 of.setName(scp.getName()+"-AmazonRDSOnDemand-MySQLSingleProvisioned" +  "_TIME" +System.nanoTime());
				 of.addService(scp);
				
				PricePlan pp = new PricePlan("pp-datatransfer-AmazonRDSMySQL-SingleProvisioned"+"_TIME"+System.nanoTime());
				of.setPricePlan(pp);
				
				
				QualitativeValue Location = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
				Location.addType(CLOUDEnum.LOCATION.getConceptURI());
				Location.setHasLabel(reg.getRegion());
				scp.addQualitativeFeature(Location);
				
				QualitativeValue DBEngine = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
				DBEngine.addType(CLOUDEnum.PLATFORM.getConceptURI());
				DBEngine.setHasLabel("MySQL");
				scp.addQualitativeFeature(DBEngine);
				for(AmazonRDSStorage_IOPSJSONModel.Rate rate : reg.getRates())
				{
					if(rate.getType().equals("storageRate"))
					{
						PriceComponent storagecost_pc = new PriceComponent();//Component responsible for calculating the total price to pay related only to the Data transferral on Amazon EC2
						pp.addPriceComponent(storagecost_pc);
						storagecost_pc.setName("DBStoragePC-PP" + "_TIME" +System.nanoTime());
						
						PriceFunction dbstorage_cost_pf = new PriceFunction();
						storagecost_pc.setPriceFunction(dbstorage_cost_pf);
						dbstorage_cost_pf.setName("rds_singlemysql__storagecost" +  "_TIME" +System.nanoTime());
						
						Usage GBStorageWanted = new Usage();
						dbstorage_cost_pf.addUsageVariable(GBStorageWanted);
						GBStorageWanted.setName("GBStorageWanted"+"TIME" +System.nanoTime());
						GBStorageWanted.setComment("Number of GB you desire for your database.");
						
						Provider CostPerGB = new Provider();
						dbstorage_cost_pf.addProviderVariable(CostPerGB);
						CostPerGB.setName("costpgb" + "TIME" +System.nanoTime());
						QuantitativeValue val = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
						CostPerGB.setValue(val);
						
						Usage NumberOfMonths = new Usage();
						dbstorage_cost_pf.addUsageVariable(NumberOfMonths);
						NumberOfMonths.setName("NumberOfMonths"+"TIME" +System.nanoTime());
						NumberOfMonths.setComment("The number of months that you'll be using the service.");
						
						double price=0;
                		try
                		{
                			price =Double.parseDouble(rate.getPrices().getUSD());
                		}catch(NumberFormatException e)
                		{
                			return null;
                		}catch(NullPointerException e){
                			return null;
                		}
						
						val.setValue(price);
						val.setUnitOfMeasurement("USD");
						
						dbstorage_cost_pf.setStringFunction(CostPerGB.getName() + "*" +GBStorageWanted.getName() + " * " + NumberOfMonths.getName() );
					}
					else if(rate.getType().equals("piopsRate"))
					{
						PriceComponent iopscost_pc = new PriceComponent();//Component responsible for calculating the total price to pay related only to the Data transferral on Amazon EC2
						pp.addPriceComponent(iopscost_pc);
						iopscost_pc.setName("DBIOPSPC-PP" + "_TIME" +System.nanoTime());
						
						PriceFunction dbiops_cost_pf = new PriceFunction();
						iopscost_pc.setPriceFunction(dbiops_cost_pf);
						dbiops_cost_pf.setName("rds_singlemysql__iopscost" +  "_TIME" +System.nanoTime());
						
						Usage IOPSWanted = new Usage();
						dbiops_cost_pf.addUsageVariable(IOPSWanted);
						IOPSWanted.setName("IOPSWanted"+"TIME" +System.nanoTime());
						IOPSWanted.setComment("Number of I/O operations you expect to perform per second.");
						
						Provider CostPer1mIO = new Provider();
						dbiops_cost_pf.addProviderVariable(CostPer1mIO);
						CostPer1mIO.setName("costpIOPS" + "TIME" +System.nanoTime());
						QuantitativeValue val = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
						CostPer1mIO.setValue(val);
						
						Usage NumberOfHours = new Usage();
						dbiops_cost_pf.addUsageVariable(NumberOfHours);
						NumberOfHours.setName("usagehours"+"TIME" +System.nanoTime());
						NumberOfHours.setComment("The number of hours that you'll be using the service.");
						
						double price=0;
                		try
                		{
                			price =Double.parseDouble(rate.getPrices().getUSD());
                		}catch(NumberFormatException e)
                		{
                			System.out.println("error");
                			return null;
                		}catch(NullPointerException e){
                			System.out.println("error");
                			return null;
                		}
						
						val.setValue(price);
						val.setUnitOfMeasurement("USD");
						
						dbiops_cost_pf.setStringFunction("("+NumberOfHours.getName() + " / 732) * " + CostPer1mIO.getName() + " * " + IOPSWanted.getName());
					}
				}
				
				provOfs.add(of);
			}
		}
		
		return provOfs;
	}

	private static ArrayList<Offering> StandardStorageOfferings(ArrayList<Service> basicServ,AmazonRDSStorage_IOPSJSONModel standardstorage_data) {
		ArrayList<Offering> ssOfs = new ArrayList<Offering>();
		
		for(AmazonRDSStorage_IOPSJSONModel.Region reg : standardstorage_data.getConfig().getRegions())
		{
			for(Service serv : basicServ)
			{
				Service scp = new Service(serv);
				
				 Offering of = new Offering();
				 of.setName(scp.getName()+"-AmazonRDSOnDemand-MySQLSingleStandardStorage" +  "_TIME" +System.nanoTime());
				 of.addService(scp);
				
				PricePlan pp = new PricePlan("pp-datatransfer-AmazonRDSMySQL-SingleStandardStorage"+"_TIME"+System.nanoTime());
				of.setPricePlan(pp);
				
				QualitativeValue Location = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
				Location.addType(CLOUDEnum.LOCATION.getConceptURI());
				Location.setHasLabel(reg.getRegion());
				scp.addQualitativeFeature(Location);
				
				QualitativeValue DBEngine = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
				DBEngine.addType(CLOUDEnum.PLATFORM.getConceptURI());
				DBEngine.setHasLabel("MySQL");
				scp.addQualitativeFeature(DBEngine);
				
				for(AmazonRDSStorage_IOPSJSONModel.Rate rate : reg.getRates())
				{
					if(rate.getType().equals("storageRate"))
					{
						PriceComponent storagecost_pc = new PriceComponent();//Component responsible for calculating the total price to pay related only to the Data transferral on Amazon EC2
						pp.addPriceComponent(storagecost_pc);
						storagecost_pc.setName("DBStoragePC-PP" + "_TIME" +System.nanoTime());
						
						PriceFunction dbstorage_cost_pf = new PriceFunction();
						storagecost_pc.setPriceFunction(dbstorage_cost_pf);
						dbstorage_cost_pf.setName("rds_singlemysql__storagecost" +  "_TIME" +System.nanoTime());
						
						Usage GBStorageWanted = new Usage();
						dbstorage_cost_pf.addUsageVariable(GBStorageWanted);
						GBStorageWanted.setName("GBStorageWanted"+"TIME" +System.nanoTime());
						GBStorageWanted.setComment("Number of GB you desire for your database.");
						
						Provider CostPerGB = new Provider();
						dbstorage_cost_pf.addProviderVariable(CostPerGB);
						CostPerGB.setName("costpgb" + "TIME" +System.nanoTime());
						QuantitativeValue val = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
						CostPerGB.setValue(val);
						
						Usage NumberOfMonths = new Usage();
						dbstorage_cost_pf.addUsageVariable(NumberOfMonths);
						NumberOfMonths.setName("NumberOfMonths"+"TIME" +System.nanoTime());
						NumberOfMonths.setComment("The number of months that you'll be using the service.");
						
						double price=0;
                		try
                		{
                			price =Double.parseDouble(rate.getPrices().getUSD());
                		}catch(NumberFormatException e)
                		{
                			return null;
                		}catch(NullPointerException e){
                			return null;
                		}
						
						val.setValue(price);
						val.setUnitOfMeasurement("USD");
						
						dbstorage_cost_pf.setStringFunction(CostPerGB.getName() + "*" +GBStorageWanted.getName() + " * " + NumberOfMonths.getName() );
					}
					else if(rate.getType().equals("ioRate"))
					{
						PriceComponent iopscost_pc = new PriceComponent();//Component responsible for calculating the total price to pay related only to the Data transferral on Amazon EC2
						pp.addPriceComponent(iopscost_pc);
						iopscost_pc.setName("DBIOPSPC-PP" + "_TIME" +System.nanoTime());
						
						PriceFunction dbiops_cost_pf = new PriceFunction();
						iopscost_pc.setPriceFunction(dbiops_cost_pf);
						dbiops_cost_pf.setName("rds_singlemysql__iopscost" +  "_TIME" +System.nanoTime());
						
						Usage IOPSWanted = new Usage();
						dbiops_cost_pf.addUsageVariable(IOPSWanted);
						IOPSWanted.setName("IOPSWanted"+"TIME" +System.nanoTime());
						IOPSWanted.setComment("Number of I/O operations you expect to perform per second.");
						
						Provider CostPer1mIO = new Provider();
						dbiops_cost_pf.addProviderVariable(CostPer1mIO);
						CostPer1mIO.setName("costp1MIO" + "TIME" +System.nanoTime());
						QuantitativeValue val = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
						CostPer1mIO.setValue(val);
						
						Usage NumberOfHours = new Usage();
						dbiops_cost_pf.addUsageVariable(NumberOfHours);
						NumberOfHours.setName("usagehours"+"TIME" +System.nanoTime());
						NumberOfHours.setComment("The number of hours that you'll be using the service.");
						
						double price=0;
                		try
                		{
                			price =Double.parseDouble(rate.getPrices().getUSD());
                		}catch(NumberFormatException e)
                		{
                			return null;
                		}catch(NullPointerException e){
                			return null;
                		}
						
						val.setValue(price);
						val.setUnitOfMeasurement("USD");
						
						dbiops_cost_pf.setStringFunction("(("+NumberOfHours.getName()+"*60*60 *" +IOPSWanted.getName() + ") / 1000000) *" + CostPer1mIO.getName() );
					}
				}
				ssOfs.add(of);
			}
		}
		
		return ssOfs;
	}

	private static void addDBInstanceCost(ArrayList<Offering> ofs, AmazonRDSDBCostJSONModel data) {
		
		for(Offering of : ofs)
		{
			for(AmazonRDSDBCostJSONModel.Region reg : data.getConfig().getRegions())
			{
				for(QualitativeValue qv : of.getIncludes().get(0).getQualfeatures()){
					for(String type : qv.getTypes())
					{
						if(type.equals(CLOUDEnum.LOCATION.getConceptURI()))
						{
							if(qv.getHasValue().equals(reg.getRegion()))
							{
								PricePlan pp = of.getPricePlan();
								PriceComponent dbcost_pc = new PriceComponent();//Component responsible for calculating the total price to pay related only to the Data transferral on Amazon EC2
								pp.addPriceComponent(dbcost_pc);
								dbcost_pc.setName("DBCostPC-PP" + "_TIME" +System.nanoTime());
								
								PriceFunction dbcost_cost_pf = new PriceFunction();
								dbcost_pc.setPriceFunction(dbcost_cost_pf);
								dbcost_cost_pf.setName("rds_singlemysql_Instancecost" +  "_TIME" +System.nanoTime());
								for(AmazonRDSDBCostJSONModel.Type typ : reg.getTypes())
								{
									for(AmazonRDSDBCostJSONModel.Tier tier : typ.getTiers())
									{
										
										if(of.getIncludes().get(0).getName().contains(tier.getName()))
										{
											Usage NumberOfHours = new Usage();
											dbcost_cost_pf.addUsageVariable(NumberOfHours);
											NumberOfHours.setName("usagehours"+"TIME" +System.nanoTime());
											NumberOfHours.setComment("The number of hours that you'll be using the service.");
											
											Provider CostPerHour = new Provider();
											dbcost_cost_pf.addProviderVariable(CostPerHour);
											CostPerHour.setName("costph" + "TIME" +System.nanoTime());
											QuantitativeValue val = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
											CostPerHour.setValue(val);
											
											double price=0;
					                		try
					                		{
					                			price =Double.parseDouble(tier.getPrices().getUSD());
					                		}catch(NumberFormatException e)
					                		{
					                			return;
					                		}catch(NullPointerException e){
					                			return;
					                		}
											
											val.setValue(price);
											val.setUnitOfMeasurement("USD");
											
											dbcost_cost_pf.setStringFunction(CostPerHour.getName() + "*" +NumberOfHours.getName() );
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public static void createLocalCopy(String JsonString,String name) throws IOException
	{
		File file =  new File("./HTMLCopies/"+name+".json");

		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(JsonString);
		bw.close();
	}
}

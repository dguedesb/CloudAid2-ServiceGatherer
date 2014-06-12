package Amazon;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.net.URL;

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
import gsonModels.AmazonElasticLBJSONModel;


public class AmazonElasticLB {
	private static String url ="http://a0.awsstatic.com/pricing/1/elasticloadbalancer/pricing-elb.min.js";
	
	private static boolean localCopy = false;
	public static void main(String[] args) throws MalformedURLException, IOException, ParseException, InvalidLinkedUSDLModelException
	{
			ArrayList<Service> services = basicAmazonLB();
			ArrayList<Offering> ofs = AddPricingData(services);
			
			LinkedUSDLModel jmodel;

			jmodel = LinkedUSDLModelFactory.createEmptyModel();
			
			jmodel.setBaseURI("http://PricingAPIAmazonElasticLBOfferings.com");
			jmodel.setOfferings(ofs);
			Model instance = jmodel.WriteToModel();//transform the java models to a semantic representation

			File outputFile = new File("./ProviderSets/AmazonElasticLB_fullset.ttl");
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}

			FileOutputStream out = new FileOutputStream(outputFile);
			instance.write(out, "Turtle");
			out.close();
	}
	
	@SuppressWarnings("resource")
	public static ArrayList<Offering> AddPricingData(ArrayList<Service> services) throws MalformedURLException, IOException
	{
		ArrayList<Offering> ofs = new ArrayList<Offering>();
		
//		String JsonString = IOUtils.toString(new URL(url).openStream());
//		JsonString = JsonString.substring(201, JsonString.length() - 2);// remove some unnecessary characters
		
		String JsonString = new Scanner(new File("./HTMLCopies/AmazonElasticLB.json")).useDelimiter("\\Z").next();
//		System.out.println(JsonString);
		if(localCopy)
			createLocalCopy(JsonString);
		
		AmazonElasticLBJSONModel data = new Gson().fromJson(JsonString, AmazonElasticLBJSONModel.class);
		
		for(Service serv : services)
		{
			for(AmazonElasticLBJSONModel.Region reg : data.getConfig().getRegions())
			{
				Service scp = new Service(serv);
				scp.setName(scp.getName().replaceAll("_TIME\\d+.*", "") + "_TIME"+System.nanoTime());
				
				
				QualitativeValue Location = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
				Location.addType(CLOUDEnum.LOCATION.getConceptURI());
				Location.setHasLabel(reg.getRegion());
				scp.addQualitativeFeature(Location);
				
				Offering of = new Offering();
				of.setName("AmazonElasticLB-"+reg.getRegion()+"_TIME"+System.nanoTime());
				of.addService(scp);
				
				PricePlan pp = new PricePlan("pp-AmazonElasticLB"+"_TIME"+System.nanoTime());
				of.setPricePlan(pp);
				
				for(AmazonElasticLBJSONModel.Type t : reg.getTypes())
				{
					for(AmazonElasticLBJSONModel.Value val : t.getValues())
					{
						if(val.getRate().equals("perELBHour"))//PC that represents the cost per hour per LB
						{

							PriceComponent cost_perh_perLB = new PriceComponent();
							pp.addPriceComponent(cost_perh_perLB);
							cost_perh_perLB.setName("AmazonCostperHperLBPC-PP" + "_TIME" +System.nanoTime());
							
							PriceFunction cost_perh_perLB_pf = new PriceFunction();
							cost_perh_perLB.setPriceFunction(cost_perh_perLB_pf);
							cost_perh_perLB_pf.setName("amazonElasticLB_lb_cost" +  "_TIME" +System.nanoTime());
							
							Provider lb_cost = new Provider();
							cost_perh_perLB_pf.addProviderVariable(lb_cost);
	                		lb_cost.setName("Amazonlbcost"+"TIME"+System.nanoTime());
	                		QuantitativeValue valb = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
	                		lb_cost.setValue(valb);
	                		
	                		double price=0;
	                		try
	                		{
	                			price =Double.parseDouble(val.getPrices().getUSD());
	                		}catch(NumberFormatException e)
	                		{
	                			return null;
	                		}catch(NullPointerException e){
	                			return null;
	                		}
	                		valb.setValue(price);
	                		valb.setUnitOfMeasurement("USD");
	                		
	    					Usage nlb = new Usage();
	    					cost_perh_perLB_pf.addUsageVariable(nlb);
	    					nlb.setName("amazonnlb"+"TIME" +System.nanoTime());
	    					nlb.setComment("How many Load Balancers do you need?");
	    					
	    					Usage usagehours = new Usage();
	    					cost_perh_perLB_pf.addUsageVariable(usagehours);
	    					usagehours.setName("usagehours"+"TIME" +System.nanoTime());
	    					usagehours.setComment("How long will you be needing the Load Balancers? (Hours)");
	    					
	    					cost_perh_perLB_pf.setStringFunction(""+lb_cost.getName() + " * " + usagehours.getName() + " * " + nlb.getName());
						}
						else if(val.getRate().equals("perGBProcessed"))//PC that represents the cost per GB processed by the LB
						{
							PriceComponent cost_perGBprocessed = new PriceComponent();
							pp.addPriceComponent(cost_perGBprocessed);
							cost_perGBprocessed.setName("CostperGBProcessedPC-PP" + "_TIME" +System.nanoTime());
							
							PriceFunction cost_perGBprocessed_pf = new PriceFunction();
							cost_perGBprocessed.setPriceFunction(cost_perGBprocessed_pf);
							cost_perGBprocessed_pf.setName("amazonElasticLB_GB_cost" +  "_TIME" +System.nanoTime());
							
							Provider gb_cost = new Provider();
							cost_perGBprocessed_pf.addProviderVariable(gb_cost);
							gb_cost.setName("AmazonLBGBcost"+"TIME"+System.nanoTime());
	                		QuantitativeValue valb = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
	                		gb_cost.setValue(valb);
	                		
	                		double price=0;
	                		try
	                		{
	                			price =Double.parseDouble(val.getPrices().getUSD());
	                		}catch(NumberFormatException e)
	                		{
	                			return null;
	                		}catch(NullPointerException e){
	                			return null;
	                		}
	                		valb.setValue(price);
	                		valb.setUnitOfMeasurement("USD");
	                		
	                		Usage gbin = new Usage();
	                		cost_perGBprocessed_pf.addUsageVariable(gbin);
	    					gbin.setName("gbin"+"TIME" +System.nanoTime());
	    					gbin.setComment("How many GB per month do you expect to receive on your service?");
	    					
	    					cost_perGBprocessed_pf.setStringFunction(gbin.getName() + " * " +gb_cost.getName());
						}
					}
				}
				
				ofs.add(of);
			}
		}

		
		
		return ofs;
	}
	
	public static void createLocalCopy(String JsonString) throws IOException
	{
		File file =  new File("./HTMLCopies/AmazonElasticLB.json");

		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(JsonString);
		bw.close();
	}
	
	//manual population
	public static ArrayList<Service> basicAmazonLB()
	{
		ArrayList<Service> services = new ArrayList<Service>();
		String[] OSs = {"Linux","Windows"};
		// extracted information from http://aws.amazon.com/ec2/faqs/#elastic-load-balancing and http://aws.amazon.com/elasticloadbalancing/details/
		for(String os : OSs)
		{
			Service s = new Service();
			s.setName("AmazonElasticLB" + "_TIME" + System.nanoTime());
			
			QualitativeValue ServiceType =  new QualitativeValue("qv"+"_TIME"+System.nanoTime());
			ServiceType.addType(CLOUDEnum.FEATURE.getConceptURI());
			ServiceType.setHasLabel("Load Balancer");
			s.addQualFeature(ServiceType);
			
			
//			HTTP, HTTPS (Secure HTTP), SSL (Secure TCP) and TCP protocols.
			
			QualitativeValue Protocol =  new QualitativeValue("qv"+"_TIME"+System.nanoTime());
			Protocol.addType(CLOUDEnum.PROTOCOL.getConceptURI());
			Protocol.setHasLabel("HTTP");
			s.addQualFeature(Protocol);
			
			QualitativeValue Protocolb =  new QualitativeValue("qv"+"_TIME"+System.nanoTime());
			Protocolb.addType(CLOUDEnum.PROTOCOL.getConceptURI());
			Protocolb.setHasLabel("HTTPS");
			s.addQualFeature(Protocolb);
			
			QualitativeValue Protocolc =  new QualitativeValue("qv"+"_TIME"+System.nanoTime());
			Protocolc.addType(CLOUDEnum.PROTOCOL.getConceptURI());
			Protocolc.setHasLabel("TCP");
			s.addQualFeature(Protocolc);
			
			QualitativeValue Protocold =  new QualitativeValue("qv"+"_TIME"+System.nanoTime());
			Protocold.addType(CLOUDEnum.PROTOCOL.getConceptURI());
			Protocold.setHasLabel("SSL");
			s.addQualFeature(Protocold);
			
			QualitativeValue SSL =  new QualitativeValue("qv"+"_TIME"+System.nanoTime());
			SSL.addType(CLOUDEnum.SSL.getConceptURI());
			SSL.setHasLabel("SSL Offloading");
			SSL.setComment("Elastic Load Balancing supports SSL termination at the load balancer, including offloading SSL decryption from application instances, centralized management of SSL certificates, and encryption to back-end instances with optional public key authentication.");
			s.addQualFeature(SSL);
			
			QualitativeValue LoadBalancing =  new QualitativeValue("qv"+"_TIME"+System.nanoTime());
			LoadBalancing.addType(CLOUDEnum.LOADBALANCING.getConceptURI());
			LoadBalancing.setHasLabel("25, 80, 443, and 1024-65535.");
			LoadBalancing.setComment("TCP ports.");
			s.addQualFeature(LoadBalancing);
			
			QualitativeValue HealthCheck =  new QualitativeValue("qv"+"_TIME"+System.nanoTime());
			HealthCheck.addType(CLOUDEnum.RELIABILITY.getConceptURI());
			HealthCheck.setHasLabel("Health Checks");
			HealthCheck.setComment("Elastic Load Balancing can detect the health of Amazon EC2 instances. When it detects unhealthy Amazon EC2 instances, it no longer routes traffic to those instances and spreads the load across the remaining healthy instances.");
			s.addQualFeature(HealthCheck);
			
			QuantitativeValue DataProcessed = new QuantitativeValue("qv"+"_TIME"+System.nanoTime());
			DataProcessed.addType(CLOUDEnum.DATAPROCESSED.getConceptURI());
			DataProcessed.setValue(Double.MAX_VALUE);
			s.addQuantFeature(DataProcessed);
			
			QualitativeValue Scaling = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
			Scaling.addType(CLOUDEnum.SCALABILITY.getConceptURI());
			Scaling.setHasLabel("Auto-Scaling");
			s.addQualFeature(Scaling);
			
			QualitativeValue CONSOLE = new QualitativeValue("qv"+"_TIME"+System.nanoTime());
			CONSOLE.addType(CLOUDEnum.CONSOLE.getConceptURI());
			CONSOLE.setHasLabel("AWS Management Console");
			s.addQualFeature(CONSOLE);
			
			QualitativeValue OS =  new QualitativeValue("qv"+"_TIME"+System.nanoTime());
			if(os.equalsIgnoreCase("linux"))
				OS.addType(CLOUDEnum.UNIX.getConceptURI());
			else
				OS.addType(CLOUDEnum.WINDOWS.getConceptURI());
			
			OS.setHasLabel(os);
			s.addQualFeature(OS);
			
			services.add(s);
		}
		
		
		return services;
	}
}

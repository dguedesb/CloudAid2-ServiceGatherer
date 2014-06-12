package providers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import usdl.constants.enums.CLOUDEnum;
import usdl.servicemodel.LinkedUSDLModel;
import usdl.servicemodel.LinkedUSDLModelFactory;
import usdl.servicemodel.Offering;
import usdl.servicemodel.PriceComponent;
import usdl.servicemodel.PriceFunction;
import usdl.servicemodel.PricePlan;
import usdl.servicemodel.QualitativeValue;
import usdl.servicemodel.QuantitativeValue;
import usdl.servicemodel.Usage;

import com.hp.hpl.jena.rdf.model.Model;

import exceptions.InvalidLinkedUSDLModelException;
import exceptions.ReadModelException;

public class Reader {
	
	private static int counter = 0;
	public static void main(String[] args)
	{
		LinkedUSDLModel jmodel;
		try {
			jmodel = LinkedUSDLModelFactory.createFromModel("./ProviderSets/");//read a generic model
			//System.out.println(jmodel.toString());
			
			//set variables values
			List<Offering> myOfferings = jmodel.getOfferings();
			
			for(Offering of : myOfferings)
			{
				System.out.println(of.getName());
				for(QuantitativeValue qv : of.getIncludes().get(0).getQuantfeatures())
				{
					if(qv.getValue() >=0)
						System.out.println(qv.getTypes().get(0) + "->"+qv.getValue());
					else if(qv.getMaxValue() >= 0)
						System.out.println(qv.getTypes().get(0) + "->"+qv.getValue());
				}
				for(QualitativeValue qv : of.getIncludes().get(0).getQualfeatures())
				{
					System.out.println(qv.getTypes().get(0) + "->"+qv.getHasValue());
				}
			}
			
			
			System.out.println(myOfferings.size());
			HashMap<String,Usage> definedVariables = new HashMap<String,Usage>();
			for(Offering off : myOfferings)//for each offering
			{
				if(off.getPricePlan() != null)//if it has a price plan
				{
					PricePlan pp = off.getPricePlan();
					for(PriceComponent pc : pp.getPriceComponents())//for every component of the priceplan
					{
						if(pc.getPriceFunction() != null)//if it has a price function
						{
							PriceFunction pf = pc.getPriceFunction();
							//System.out.println("Off: "+off.getName());
							//System.out.println(pf.getSPARQLFunction());
							
							List<Usage> usageVars = pf.getUsageVariables();//fetch its usage variables
							Collections.sort(usageVars, new Comparator<Usage>() {//sort them alphabetically, this can be included in the API
							    public int compare(Usage s1, Usage s2) {
							        return s1.getName().compareTo(s2.getName());
							    }
							});
							
							for(Usage var : usageVars)//for each usage var, set a value.
							{
								if(!definedVariables.containsKey(var.getName().replaceAll("TIME\\d+.*", "").replaceAll("AUTO\\d+", "")))
								{
									QuantitativeValue val = new QuantitativeValue();
	
									@SuppressWarnings("resource")
									Scanner scan = new Scanner(System.in);
									System.out.println("Insert the value for the " + var.getName().replaceAll("TIME\\d+.*", "").replaceAll("AUTO\\d+", "") +" variable.\nDetails: \n"+var.getComment());
									double num = Double.parseDouble(scan.nextLine());
									
									val.setValue(num);
									var.setValue(val);//add the new value to the usage variable 
									definedVariables.put(var.getName().replaceAll("TIME\\d+.*", "").replaceAll("AUTO\\d+", ""),var);
								}
								else
								{
									var.setValue(new QuantitativeValue((QuantitativeValue)definedVariables.get(var.getName().replaceAll("TIME\\d+.*", "").replaceAll("AUTO\\d+", "")).getValue()));
								}
							}
						}
					}
				}
				else
					System.out.println("Offering with a null price plan");
			}
			jmodel.setBaseURI("http://test.com");
			Model instance = jmodel.WriteToModel();//after we've done our changes in the jmodels, we transform them into a new Semantic model
			
			//write model to file
			File outputFile = new File("amazonRIInstance.ttl");
			if (!outputFile.exists()) {
	        	outputFile.createNewFile();        	 
	        }

			FileOutputStream out = new FileOutputStream(outputFile);
			instance.write(out, "Turtle");
			out.close();
			
			//after applying the changes to the jmodels and transforming them to a semantic web representation, we can calculate the prices of every offerings.
			for(Offering off : myOfferings)
			{
				System.out.print(""+off.getName());
				for(QualitativeValue qv : off.getIncludes().get(0).getQualfeatures())
				{
					for(String type : qv.getTypes())
					{
						if(type.equals(CLOUDEnum.LOCATION.getConceptURI()))
							System.out.print("    , Location: "+qv.getHasValue());
					}
				}
				System.out.println(", Price:"+off.getPricePlan().calculatePrice(instance));
			}
			//jmodel.writeModelToFile("./serviceExamples/test.ttl", "baseURI/de_teste", "TTL");
		} catch (InvalidLinkedUSDLModelException | IOException
				| ReadModelException e) {
			e.printStackTrace();
		}
	}
}


package testing;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;

import exceptions.ReadModelException;

public class JenaReader {
	public static void main(String[] args) throws ReadModelException {

	Model data = FileManager.get().loadModel("C:/Users/daniel/workspace/CloudAid2-ServiceGatherer/ProviderSets/amazonOnDemand_fullset0.ttl", "TTL");

		test(data);
	}
	
	private static void test(Model model) {
		String q = "PREFIX core: <http://www.linked-usdl.org/ns/usdl-core#>  PREFIX price: <http://www.linked-usdl.org/ns/usdl-price#>  PREFIX pf: <http://jena.hpl.hp.com/ARQ/property#>  PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  PREFIX gr: <http://purl.org/goodrelations/v1#>  PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  PREFIX cloudtaxonomy: <http://rdfs.genssiz.org/CloudTaxonomy#> PREFIX spin: <http://spinrdf.org/spin#> SELECT REDUCED ?offering   WHERE {  {  SELECT REDUCED ?offering  WHERE {  ?offering rdf:type core:ServiceOffering.  ?offering core:includes ?serv .  {  ?serv gr:qualitativeProductOrServiceProperty ?f .  ?f rdf:type cloudtaxonomy:Location .  ?f rdfs:label ?value  FILTER regex(?value, 'tokyo', 'i')  }UNION{  ?serv core:hasServiceModel ?model .  ?model gr:qualitativeProductOrServiceProperty ?f .  ?f rdf:type cloudtaxonomy:Location .  ?f rdfs:label ?value  FILTER regex(?value, 'tokyo', 'i')  }  }  } . {  SELECT REDUCED ?offering  WHERE {  ?offering rdf:type core:ServiceOffering . ?offering core:includes ?serv .  {  ?serv gr:quantitativeProductOrServiceProperty cloudtaxonomy:MemorySize .  ?f gr:hasValue ?value  FILTER(?value >= 3.7)  }UNION{  ?serv gr:quantitativeProductOrServiceProperty ?f .  ?f rdf:type cloudtaxonomy:MemorySize .  ?f gr:hasValue ?value  FILTER(?value >= 3.7)  }UNION{  ?serv core:hasServiceModel ?model .  ?model gr:quantitativeProductOrServiceProperty cloudtaxonomy:MemorySize .  ?f gr:hasValue ?value  FILTER(?value >= 3.7)  }UNION{  ?serv core:hasServiceModel ?model .  ?model gr:quantitativeProductOrServiceProperty ?f .  ?f rdf:type cloudtaxonomy:MemorySize .  ?f gr:hasValue ?value  FILTER(?value >= 3.7)  } UNION{  ?serv core:hasServiceModel ?model .  ?model gr:quantitativeProductOrServiceProperty ?f .  ?f rdf:type cloudtaxonomy:MemorySize .  ?f gr:hasMinValue ?value  FILTER(?value >= 3.7)  }  }  } . {  SELECT REDUCED ?offering  WHERE {  ?offering rdf:type core:ServiceOffering . ?offering core:includes ?serv .  {  ?serv gr:quantitativeProductOrServiceProperty cloudtaxonomy:DiskSize .  ?f gr:hasValue ?value  FILTER(?value >= 150.0)  }UNION{  ?serv gr:quantitativeProductOrServiceProperty ?f .  ?f rdf:type cloudtaxonomy:DiskSize .  ?f gr:hasValue ?value  FILTER(?value >= 150.0)  }UNION{  ?serv core:hasServiceModel ?model .  ?model gr:quantitativeProductOrServiceProperty cloudtaxonomy:DiskSize .  ?f gr:hasValue ?value  FILTER(?value >= 150.0)  }UNION{  ?serv core:hasServiceModel ?model .  ?model gr:quantitativeProductOrServiceProperty ?f .  ?f rdf:type cloudtaxonomy:DiskSize .  ?f gr:hasValue ?value  FILTER(?value >= 150.0)  } UNION{  ?serv core:hasServiceModel ?model .  ?model gr:quantitativeProductOrServiceProperty ?f .  ?f rdf:type cloudtaxonomy:DiskSize .  ?f gr:hasMinValue ?value  FILTER(?value >= 150.0)  }  }  } . {  SELECT REDUCED ?offering  WHERE {  ?offering rdf:type core:ServiceOffering.  ?offering core:includes ?serv .  {  ?serv gr:qualitativeProductOrServiceProperty ?f .  ?f rdf:type cloudtaxonomy:Feature .  ?f rdfs:label ?value  FILTER regex(?value, 'Virtual Machine', 'i')  }UNION{  ?serv core:hasServiceModel ?model .  ?model gr:qualitativeProductOrServiceProperty ?f .  ?f rdf:type cloudtaxonomy:Feature .  ?f rdfs:label ?value  FILTER regex(?value, 'Virtual Machine', 'i')  }  }  }  }";

		Query query = QueryFactory.create(q);
		System.out.println(query.toString());

		QueryExecution exec = QueryExecutionFactory.create(query,model);

		ResultSet results = exec.execSelect();	
		ResultSetFormatter.out(System.out, results, query);

		exec.close();
	}
}

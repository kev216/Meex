package it.cefriel.swa.meex.moi;
import it.cefriel.swa.meex.Config;
import it.cefriel.swa.meex.grddl.XSLTTransformer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.xml.transform.TransformerException;

import com.evdb.javaapi.APIConfiguration;
import com.evdb.javaapi.EVDBAPIException;
import com.evdb.javaapi.EVDBRuntimeException;
import com.evdb.javaapi.data.Event;
import com.evdb.javaapi.data.SearchResult;
import com.evdb.javaapi.data.request.EventSearchRequest;
import com.evdb.javaapi.operations.EventOperations;
import com.hp.hpl.jena.rdf.model.Model;

public class Test_ZHANG_EVDBNew {
	/**
	 * @param args
	 * @throws EVDBAPIException
	 * @throws EVDBRuntimeException
	 * @throws IOException 
	 * @throws TransformerException 
	 */
	public static void main(String[] args) throws EVDBRuntimeException, EVDBAPIException, IOException, TransformerException {
		APIConfiguration.setApiKey("2SGLxsBVZJ25nF2Z");
		APIConfiguration.setEvdbUser("sw-book");
		APIConfiguration.setEvdbPassword("sw-book");
		String keywords="Metallica";
		EventSearchRequest esr=new EventSearchRequest();
		esr.setKeywords(keywords);
		esr.setCategory("Music");

		EventOperations eo=new EventOperations();
		SearchResult searchResult=eo.search(esr);
		
		
		FileWriter writer = new FileWriter(Config.TempBasePath+"ZHANG.xml");
		
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+"\n"
					+"<feed xmlns:grddl=\"http://www.w3.org/2003/g/data-view#\" grddl:transformation=\"file:///F:/WEB SEM/projet/Meex/Tomcat/webapps/meex/WEB-INF/onto/EVDB/evdb-to-rdf.xsl file:///F:/WEB SEM/projet/Meex/Tomcat/webapps/meex/WEB-INF/onto/EVDB/evdb-to-rdf.xsl\" xmlns=\"http://www.w3.org/2005/Atom\" xml:lang=\"en-US\" xmlns:g=\"http://base.google.com/ns/1.0\" xmlns:gd=\"http://schemas.google.com/g/2005\" xmlns:georss=\"http://www.georss.org/georss\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:media=\"http://search.yahoo.com/mrss/\" xmlns:opensearch=\"http://a9.com/-/spec/opensearch/1.1/\">"+"\n");
		
	
//		<entry xmlns=\"http://www.w3.org/2005/Atom\" xmlns:g=\"http://base.google.com/ns/1.0\" xmlns:gd=\"http://schemas.google.com/g/2005\" xmlns:georss=\"http://www.georss.org/georss\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:media=\"http://search.yahoo.com/mrss/\" xmlns:opensearch=\"http://a9.com/-/spec/opensearch/1.1/\">
		List<Event> events=searchResult.getEvents();
		int i=0;
		for(Event e:events)
			writer.write("\t<entry xmlns=\"http://www.w3.org/2005/Atom\" xmlns:g=\"http://base.google.com/ns/1.0\" xmlns:gd=\"http://schemas.google.com/g/2005\" xmlns:georss=\"http://www.georss.org/georss\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:media=\"http://search.yahoo.com/mrss/\" xmlns:opensearch=\"http://a9.com/-/spec/opensearch/1.1/\">\n"
						+"\t<id>"+"http://eventful.com/events/"+e.getSeid()+"</id>\n"
						+"\t"+"<title>"+e.getTitle()+"</title>"+"\n"
						+"\t"+"<gd:when startTime=\""+e.getStartTime()+"\" endTime=\""+e.getStopTime()+"\" />"+"\n"
						+"\t"+"<gd:where rel=\""+"http://schemas.google.com/g/2005#event"+"\" valueString=\""+e.getVenueCountryThreeLetterAbbreviation()+", "+e.getVenueName()+", "+e.getVenuePostalCode()+", "+e.getVenueRegionAbbreviation()+"\">\n"
						+"\t<gd:entryLink>\n"
						+"\t\t<entry xmlns=\"http://www.w3.org/2005/Atom\" xmlns:g=\"http://base.google.com/ns/1.0\" xmlns:gd=\"http://schemas.google.com/g/2005\" xmlns:georss=\"http://www.georss.org/georss\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:media=\"http://search.yahoo.com/mrss/\" xmlns:opensearch=\"http://a9.com/-/spec/opensearch/1.1/\">\n"
						+"\t\t\t<gd:contactSection label=\""+e.getVenue().getAddress()+","+e.getVenue().getCity()+","+e.getVenue().getCountry()+"\">\n"
						+"\t\t\t\t<gd:geoPt lat=\""+e.getVenueLatitude()+"\" lon=\""+e.getVenueLongitude()+"\" />\n"
						+"\t\t\t</gd:contactSection>\n"+
						"\t\t</entry>\n"
						+"\t</gd:entryLink>\n\t</gd:where>\n\t</entry>\n"
						);
	
		writer.write("\n</feed>");
		
		writer.close();
		
		String eventsFilename = Config.TempBasePath + "ZHANG.xml";
		Model model = XSLTTransformer.transformFromFile(
				eventsFilename,
				Config.OntoBasePath + "EVDB/evdb-to-rdf.xsl", 
				eventsFilename + ".rdf",
				"RDF/XML");
	}
}

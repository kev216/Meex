package it.cefriel.swa.meex.moi;

import it.cefriel.swa.meex.Config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Logger;

import com.evdb.javaapi.APIConfiguration;
import com.evdb.javaapi.EVDBAPIException;
import com.evdb.javaapi.EVDBRuntimeException;
import com.evdb.javaapi.data.Event;
import com.evdb.javaapi.data.SearchResult;
import com.evdb.javaapi.data.request.EventSearchRequest;
import com.evdb.javaapi.operations.EventOperations;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.ontology.OntModelSpec;

public class Jena_EVDBToRDF {

	static Logger log = Logger.getLogger("it.cefriel.swa.meex.evdb");

	/**
	 * Retrieves events from EVDB, translates them into RDF and bind them to the
	 * performer
	 * 
	 * @param performer
	 * @return
	 * @throws IOException
	 */
	// public static Model retrieveEvents(String performer, String
	// performerLabel) {

	public static void retrieveEvents(String performer) {
		String eventsFilename = Config.TempBasePath + performer
				+ ".WANG.atom.rdf";

		log.info("Invoking EVDB eventsFilename: " + eventsFilename + " ...");
		try {
			if (Config.InvokeEVDB) {
				// If the eventsFilename is older than
				// InvokeEVDBForFilesOlderThanDays
				if (checkFileOldness(eventsFilename,
						Config.InvokeEVDBForFilesOlderThanDays)) {

					// Retrieve events from EVDB
					log.info("Invoking EVDB for: " + performer + " ...");

					testEVDB(performer, eventsFilename);

				} else {
					log.info("Skipped invocation of EVDB for: " + performer);
				}
			}

		} catch (Exception e) {
			log.warning("CANNOT GET EVENT FROM EVDB for: " + performer
					+ " due to " + e.getMessage());
			log.fine(e.toString());
		}
	}

	/**
	 * Returns true if the file is older than some days
	 * 
	 * @param filename
	 * @param days
	 * @return
	 */
	static boolean checkFileOldness(String filename, int days) {
		File f = new File(filename);
		if (!f.exists())
			return true;
		else {
			if (System.currentTimeMillis() - f.lastModified() > (long) (days * 24 * 60 * 60 * 1000L))
				return true;
			else
				return false;
		}
	}

	/**
	 * Invokes the EVDB Http end point retriving the XML payload for events
	 * related to the input performer
	 * 
	 * @param keywords
	 * @return The filename of the retrieved file
	 * @throws IOException
	 */

	// ****************************************************************************
	// ****************************************************************************
	// ****************************************************************************

	public static void testEVDB(String keywords, String eventsFilename)
			throws EVDBRuntimeException, EVDBAPIException, IOException {

		APIConfiguration.setApiKey(Config.EVDBKey);
		APIConfiguration.setEvdbUser("");
		APIConfiguration.setEvdbPassword("");

		EventSearchRequest esr = new EventSearchRequest();
		esr.setKeywords(URLEncoder.encode(keywords, "UTF-8"));
		esr.setCategory("");

		EventOperations eo = new EventOperations();
		SearchResult searchResult = eo.search(esr);
		//
		List<Event> events = searchResult.getEvents();

		log.info("Received data from EVDB for: " + keywords);
		FileWriter writer = new FileWriter(eventsFilename);

		Model model = ModelFactory.createDefaultModel();

		String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
		String owl = "http://www.w3.org/2002/07/owl#";
		String evdb = "http://eventful.com/";
		String gd = "http://schemas.google.com/g/2005";
		String atom = "http://www.w3.org/2005/Atom";
		
		model.setNsPrefix("rdfs", rdfs);
		model.setNsPrefix("owl", owl);
		model.setNsPrefix("evdb", evdb);
		model.setNsPrefix("gd", gd);
		model.setNsPrefix("atom", atom);

		Resource root = model.createResource(evdb + "events/");

		Property label = model.createProperty(gd + "label");

		Property hasWhen = model.createProperty(evdb + "hasWhen");
		Property When = model.createProperty(gd + "When");
		Property startTime = model.createProperty(gd + "startTime");
		Property endTime = model.createProperty(gd + "endTime");

		Property hasWhere = model.createProperty(evdb + "hasWhere");
		Property Where = model.createProperty(gd + "Where");

		Property Wherelabel = model.createProperty(gd + "label");
		Property postaleAdress = model.createProperty(gd + "postaleAddress");
		Property hasGeoPt = model.createProperty(gd + "hasGeoPt");
		Property GeoPt = model.createProperty(gd + "GeoPt");
		Property lat = model.createProperty(gd + "lat");
		Property lon = model.createProperty(gd + "lon");
		writer.write("<?xml version=\"1.0\"?>\n");
		for (Event e : events) {
			// Resource m = model.createResource(root);

			// for (Event e : events) {

			model.createResource(root)
					.addProperty(
							hasWhere,
							model.createResource()
									.addProperty(
											Where,
											model.createResource()
													.addProperty(
															hasGeoPt,
															model.createResource()
																	.addProperty(
																			GeoPt,
																			model.createResource()
																					.addProperty(
																							lon,
																							Double.toString(e
																									.getVenueLongitude()))
																					.addProperty(
																							lat,
																							Double.toString(e
																									.getVenueLatitude()))))

													.addProperty(
															postaleAdress,
															e.getVenueCity()
																	+ ", "
																	+ e.getVenuePostalCode()
																	+ ", "
																	+ e.getVenue()
																			.getRegion()
																	+ ", "
																	+ e.getVenue()
																			.getCountryThreeLetterAbbreviation())
													.addProperty(Wherelabel,
															e.getVenueName())))

					.addProperty(
							hasWhen,
							model.createResource().addProperty(
									When,
									model.createResource(
											evdb + e.getSeid() + "_When")
											.addProperty(endTime,
													e.getStopTime() + "")
											.addProperty(startTime,
													e.getStartTime() + "")

							)).addProperty(label, e.getTitle());

		}
		model.write(writer);

		System.err.println("# -- nsA and cat defined");

	}

	public static void main(String args[]) {
		retrieveEvents("Beatles");
	}
}

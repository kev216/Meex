/*  Meex is a Semantic Web application that retrieves data from online
 *   music archives and event databases; merges them and let the users
 *   explore events related to artists that practice the required style.
 *
 *   Copyright(c) 2008 Emanuele Della Valle, Irene Celino, Dario Cerizza
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

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

/**
 * Class for accessing EVDB data
 */
public class EVDBToRDF_V1 {
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

					EVDBtoRDF(performer, eventsFilename);

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
	public static void EVDBtoRDF(String keywords, String eventsFilename)
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

		writer.write("<rdf:RDF" + "\n" + "\t"
				+ "xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""
				+ "\n" + "\t"
				+ "xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"" + "\n"
				+ "\t" + "xmlns:owl=\"http://www.w3.org/2002/07/owl#\"" + "\n"
				+ "\t" + "xmlns:evdb=\"http://eventful.com/\"" + "\n" + "\t"
				+ "xmlns:gd=\"http://schemas.google.com/g/2005\"" + "\n" + "\t"
				+ "xmlns:atom=\"http://www.w3.org/2005/Atom\">" + "\n");

		for (Event e : events) {

			writer.write("<evdb:Event rdf:about=\"http://eventful.com/events/"
					+ e.getSeid() + ">" + "\n" + "<gd:label>" + e.getTitle()
					+ "</gd:label>" + "\n" + "<evdb:hasWhen>" + "\n" + "\t"
					+ "<gd:When rdf:about=\"http://eventful.com/events/"
					+ e.getSeid() + "_When\">" + "\n" + "\t" + "\t"
					+ "<gd:startTime>" + e.getStartTime() + "</gd:startTime>"
					+ "\n" + "\t" + "\t" + "<gd:endTime>" + e.getStopTime()
					+ "</gd:endTime>" + "\n" + "\t" + "</gd:When>" + "\n"
					+ "</evdb:hasWhen>" + "\n" + "<evdb:hasWhere>" + "\n"
					+ "\t"
					+ "<gd:Where rdf:about=\"http://eventful.com/events/"
					+ e.getSeid() + "_Where\">" + "\n" + "\t" + "\t"
					+ "<gd:label>" + e.getVenueName() + "</gd:label>" + "\n"
					+ "\t" + "\t" + "<gd:postalAddress>" + e.getVenueCity()
					+ ", " + e.getVenuePostalCode() + ", "
					+ e.getVenue().getRegion() + ", "
					+ e.getVenue().getCountryThreeLetterAbbreviation()
					+ "</gd:postalAddress>" + "\n" + "\t" + "\t" + "\t"
					+ "<gd:hasGeoPt>" + "\n" + "\t" + "\t" + "\t"
					+ "<gd:GeoPt rdf:about=\"http://eventful.com/events/"
					+ e.getSeid() + "_GeoPt\">" + "\n" + "\t" + "\t" + "\t"
					+ "\t" + "<gd:lat>" + e.getVenueLatitude() + "</gd:lat>"
					+ "\n" + "\t" + "\t" + "\t" + "\t" + "<gd:lon>"
					+ e.getVenueLongitude() + "</gd:lon>" + "\n" + "\t" + "\t"
					+ "\t" + "</gd:GeoPt>" + "\n" + "\t" + "\t"
					+ "</gd:hasGeoPt>" + "\n" + "\t" + "</gd:Where>" + "\n"
					+ "</evdb:hasWhere>" + "\n" + "</evdb:Event>"+"\n");

		}
		writer.close();
		System.out.println("End of event list");

	}

}

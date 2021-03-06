/**
 * 
 */
package it.cefriel.swa.meex.moi;

import static org.junit.Assert.*;

import java.io.IOException;

import it.cefriel.swa.meex.moi.EVDBToRDF_V1;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.evdb.javaapi.EVDBAPIException;
import com.evdb.javaapi.EVDBRuntimeException;

/**
 * @author 
 *
 */
public class Test_EVDBToRDF {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link it.cefriel.swa.meex.logic.EVDB#retrieveEvents(java.lang.String, java.lang.String)}.
	 * @throws IOException 
	 * @throws EVDBAPIException 
	 * @throws EVDBRuntimeException 
	 */
	@Test
	public void testRetrieveEvents() throws EVDBRuntimeException, EVDBAPIException, IOException {
		EVDBToRDF_V1.retrieveEvents("Lady Gaga");
//		EVDBToRDF_V1.retrieveEvents("Metallica");
	}

}

package org.asv.postagger;

import org.junit.Test;
import junit.framework.TestCase;

/**
 * TrainFile test class
 * 
 * @author marvin, robert
 *
 */
public class TrainFileTest extends TestCase {

	private static final String path  = "";
	private static final String driver = "";
	private static final String user = "";
	private static final String pw = "";
	private static final String table = "";
	private static final String column = "";
	private static final String delimiter = "";
	private static final int rate = 10;
	private static final int limit = -1;
	
	@Test
	public void fromDB() {
		TrainFile trainFile = new TrainFile();
		trainFile.writeFileFromDB(path, driver, user, pw, table, column, delimiter, rate, limit);
	}
	
	
	private static final String pathToFile = "";
	
	@Test
	public void fromFile() {
		TrainFile trainFile = new TrainFile();
		trainFile.writeFileFromFile(pathToFile, path, delimiter, rate);
	}
	
}

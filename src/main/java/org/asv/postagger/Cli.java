package org.asv.postagger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;

//TODO from file

/**
 * 
 * @author robert, marvin
 *
 */
public class Cli {

	public static boolean genProps() {
		Properties prop = new Properties();
		OutputStream output = null;

		try {
			// Generate Prop File
			output = new FileOutputStream("./tagger.properties");

			// set the properties value
			prop.setProperty("dbAdress", "");
			prop.setProperty("dbUser", "");
			prop.setProperty("dbPassword", "");
			prop.setProperty("trainFile", "");
			prop.setProperty("table", "");
			prop.setProperty("sentence_column", "");
			prop.setProperty("delimiter", "\\|");
			prop.setProperty("testPercentage", "10");
			prop.setProperty("limit", "-1");

			//TODO Offset maybe?
			
			// save properties to project folder
			prop.store(output, "Tagger Properties:");

			return true;
		} catch (Exception e) {
			// Error Log
			return false;
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static Properties loadProps() throws IOException {
		Properties prop = new Properties();
		InputStream input = null;

		String filename = "./tagger.properties";
		input = new FileInputStream(filename);
		// input = getClass().getClassLoader().getResourceAsStream(filename);

		prop.load(input);

		return prop;

	}

	public HashMap<String, String> input() {
		Scanner in = new Scanner(System.in);

		// Input: String path, String databaseUrl, String user,
		// String pw, String tableName, String column, String delimiter
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			System.out.println("====== Input: ======\n");
			System.out.println("Name of the trainfile (in data folder):");
			map.put("trainFile", in.nextLine());
			System.out.println("Adress of the MySQL database:");
			map.put("dbAdress", in.nextLine());
			System.out.println("String MySQL username:");
			map.put("dbUser", in.nextLine());
			System.out.println("MySQL-User password:");
			map.put("sbPassword", in.nextLine());
			System.out.println("Table:");
			map.put("table", in.nextLine());
			System.out.println("Column with tagged sentences:");
			map.put("sentence_column", in.nextLine());
			System.out.println("Number of Rows you want to use:");
			map.put("limit", Integer.parseInt(in.nextLine()) + "");
			System.out.println("Token/Tag delimiter:\n" + "(Caution: Special Java String characters need Escaping! "
					+ "For example: input '\\|' instead of '|')");
			map.put("delimiter", in.nextLine());
			System.out.println("Percentage of Data to use for testing:");
			map.put("testPercentage", Integer.parseInt(in.nextLine()) + "");
		} catch (Exception e) {
			System.out.println("Error! Please check you input and try again.");
		}
		in.close();
		return map;
	}

	public static void fromdb(Properties prop) throws IOException {
		
		
		Database database = new Database();
		
		Tagger tagger = new Tagger();
		
		database.writeFileFromDB(prop.getProperty("trainFile"), prop.getProperty("dbAdress"), prop.getProperty("dbUser"),
				prop.getProperty("dbPassword"), prop.getProperty("table"), prop.getProperty("sentence_column"),
				prop.getProperty("delimiter"), Integer.parseInt(prop.getProperty("testPercentage")),
				Integer.parseInt(prop.getProperty("limit")));
		tagger.trainFromFile(prop.getProperty("trainFile"));
	}

	// Only from porpfile
	public static void main(String[] args) throws IOException {

		
		
		String arq = Arrays.toString(args);

		if (arq.contains("-genprops")) {// Generate new Property File: -genprops
			genProps();
		} else {
			Properties prop = loadProps();
			fromdb(prop);
		}
	}
}

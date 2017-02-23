package org.asv.postagger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
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
			prop.setProperty("output", "");
			prop.setProperty("table", "");
			prop.setProperty("sentence_column", "");
			prop.setProperty("delimiter", "\\|");
			prop.setProperty("testPercentage", "10");
			prop.setProperty("limit", "-1");
			prop.setProperty("input", "");

			// TODO Offset maybe?

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

	public static Properties loadProps(String filename) throws IOException {
		Properties prop = new Properties();
		InputStream input = null;

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
			map.put("output", in.nextLine());
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

		database.writeFileFromDB(prop.getProperty("output") + "corpus", prop.getProperty("dbAdress"),
				prop.getProperty("dbUser"), prop.getProperty("dbPassword"), prop.getProperty("table"),
				prop.getProperty("sentence_column"), prop.getProperty("delimiter"),
				Integer.parseInt(prop.getProperty("testPercentage")), Integer.parseInt(prop.getProperty("limit")));

	}
	
	public static void fromfile(Properties prop) throws IOException {

		Database database = new Database();

		Tagger tagger = new Tagger();

		database.writeFileFromFile(prop.getProperty("input"), prop.getProperty("ouput"), Integer.parseInt(prop.getProperty("testPercentage")));

	}

	public static void training(Properties prop) throws IOException {

		Tagger tagger = new Tagger();

		tagger.train(prop.getProperty("output") + "/corpus", "RDRPOSTagger/pSCRDRtagger/");
	}

	public static void eval(Properties prop) throws IOException {

		Tagset tagset = new Tagset();

		tagset.removeTags(prop.getProperty("output") + "corpus_test", prop.getProperty("output") + "corpus_test_wot",
				"/");

		Tagger tagger = new Tagger();

		tagger.tagfile(prop.getProperty("output") + "corpus.RDR", prop.getProperty("output") + "/corpus.DICT",
				prop.getProperty("output") + "/corpus_test_wot");

		Evaluation evaluation = new Evaluation();

		HashMap<String, Long> map = new HashMap<String, Long>();
		map = evaluation.evaluate(prop.getProperty("output") + "corpus_test",
				prop.getProperty("output") + "corpus_test_wot.TAGGED");

		float fa = map.get("false");
		float all = map.get("all");

		float accuracy = 1 - (fa / all);

		System.out.println(accuracy);

		genresult(prop, accuracy);
	}

	public static void genresult(Properties prop, float accuracy) {

		OutputStream output = null;

		try {
			output = new FileOutputStream(prop.getProperty("output") + "used.properties");

			prop.setProperty("accuracy", accuracy + "%");

			prop.store(output, "Used properties + accuracy");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}

	// TODO mkdir for output, skip db so use file in path
	// Only from porpfile
	public static void main(String[] args) throws IOException {

		String arq = Arrays.toString(args);

		if (arq.contains("-genprops")) {// Generate new Property File: -genprops
			genProps();
		} else {
			Properties prop = loadProps("sampleprop");
			if(prop.getProperty("input").equals("")){
				fromdb(prop);
			} else {
				fromfile(prop);
			}
			System.out.println("train...");
			training(prop);
			System.out.println("evaluate...");
			eval(prop);
		}
	}
}

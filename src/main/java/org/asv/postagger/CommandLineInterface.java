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
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.asv.postagger.Import;
import org.asv.postagger.RAWFormater;

/**
 * @author Robert Bielinski
 * 
 *         TODO: 
 *         - only write file 
 *         - StanfordTagger 
 *         - Checks for Errors while training or tagging (?)
 */

public class CommandLineInterface {

	public CommandLineInterface() {
	}

	public boolean genProps() {
		Properties prop = new Properties();
		OutputStream output = null;

		try {
			// Generate Prop File
			output = new FileOutputStream("./cfg/tagger.properties");

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

	public Properties loadProps() throws IOException {
		Properties prop = new Properties();
		InputStream input = null;

		String filename = "./cfg/tagger.properties";
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

	public void writeFileFromDB(String filename, String databaseUrl, String user, String pw, String tablename,
			String column, String delimiter, int rate, int limit) {
		Import imp = new Import();
		String path = "./data/" + filename;
		String driver = "com.mysql.cj.jdbc.Driver";
		float testrate = rate / 100;

		System.out.println("\n====== Writing Training-File ======");
		System.out.println("\nWriting Training-File: " + path + "\nand Test-File:         " + path + "_test");

		imp.write_train_file_from_DB(path, driver, databaseUrl, user, pw, tablename, column, delimiter, testrate,
				limit);
	}

	public void trainFromFile(String filename) throws IOException {
		RAWFormater rf = new RAWFormater();

		System.out.println("\n====== Training ======");
		System.out.println("\nTraining on Training-File...");
		rf.train("../../../data/" + filename);

		System.out.println("\n====== Testing ======");
		// Remove Tags
		System.out.println("\nGenerating Test Data...");
		rf.removeTags("./data/" + filename + "_test", "./data/" + filename + "_wot", "/");

		// Testing
		System.out.println("\nTagging Test Data...");
		rf.tagg("../../../data/" + filename + ".RDR", "../../../data/" + filename + ".DICT",
				"../../../data/" + filename + "_wot");

		// Eval
		System.out.println("\nEvaluating...");
		HashMap<String, Long> result = rf.eval("./data/" + filename + "_wot.TAGGED", "./data/" + filename + "_test");
		System.out.println("\n" + result.get("false") + "/" + result.get("all") + " Fehlerhaft");
		float f = result.get("false"), a = result.get("all");
		float rate = 100 - (f / a) * 100;
		System.out.println("=> Genauigkeit: " + rate + "%");
	}

	public static void main(String[] args) {
		CommandLineInterface cli = new CommandLineInterface();
		// Logging
		Logger log = Logger.getLogger("Import");
		Logger log2 = Logger.getLogger("RAWFormater");
		ConsoleHandler handler = new ConsoleHandler();
		if (Arrays.asList(args).contains("-log")) {
			Logger.getGlobal().getParent().getHandlers()[0].setLevel(Level.FINER);
		}
		handler.setFormatter(new SimpleFormatter());
		log.addHandler(handler);
		log2.addHandler(handler);
		
		try {
			for (int i = 0; i < args.length; i++) {
				String arg = args[i];
				if (arg.startsWith("-")) {
					arg = arg.substring(1, arg.length());
					if (arg.equals("genprops")) // Generate new Property File: -genprops
						cli.genProps();
					else { // Load Data from File: -file [PATH]
						if (args.equals("file") && i < args.length)
							cli.trainFromFile(args[i++]);
						else { // Load Data from DB
							if (args.equals("input")) { 
								// Input Info: -input  
								HashMap<String, String> map = cli.input();
								cli.writeFileFromDB(map.get("trainFile"), map.get("dbAdress"), map.get("dbUser"),
										map.get("dbPassword"), map.get("table"), map.get("sentence_column"),
										map.get("delimiter"), Integer.parseInt(map.get("testPercentage")),
										Integer.parseInt(map.get("limit")));
								cli.trainFromFile(map.get("trainFile"));
							} else { // other Arguments, i.e. Java Arguments like -Xmx4G
								// Load Info from Property File
								Properties prop = cli.loadProps();
								cli.writeFileFromDB(prop.getProperty("trainFile"), prop.getProperty("dbAdress"),
										prop.getProperty("dbUser"), prop.getProperty("dbPassword"), prop.getProperty("table"),
										prop.getProperty("sentence_column"), prop.getProperty("delimiter"),
										Integer.parseInt(prop.getProperty("testPercentage")),
										Integer.parseInt(prop.getProperty("limit")));
								cli.trainFromFile(prop.getProperty("trainFile"));
							}
						}
					}
				}
			}
			if(args.length == 0){ // No Arguments!
				// Load Info from Property File
				Properties prop = cli.loadProps();
				cli.writeFileFromDB(prop.getProperty("trainFile"), prop.getProperty("dbAdress"),
						prop.getProperty("dbUser"), prop.getProperty("dbPassword"), prop.getProperty("table"),
						prop.getProperty("sentence_column"), prop.getProperty("delimiter"),
						Integer.parseInt(prop.getProperty("testPercentage")),
						Integer.parseInt(prop.getProperty("limit")));
				cli.trainFromFile(prop.getProperty("trainFile"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

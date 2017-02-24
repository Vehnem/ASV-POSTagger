package org.asv.postagger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

/**
 * 
 * @author robert, marvin
 *
 * TODO all paths first new File -> getPath
 * TODO System.out Programmablauf überarbeiten
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

	public static void fromdb(Properties prop) throws IOException {

		Database database = new Database();

		database.writeFileFromDB(prop.getProperty("output") + "corpus", prop.getProperty("dbAdress"),
				prop.getProperty("dbUser"), prop.getProperty("dbPassword"), prop.getProperty("table"),
				prop.getProperty("sentence_column"), prop.getProperty("delimiter"),
				Integer.parseInt(prop.getProperty("testPercentage")), Integer.parseInt(prop.getProperty("limit")));

	}
	
	public static void fromfile(Properties prop) throws IOException {

		Database database = new Database();

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

	/**
	 * Copy of the input properties  file with accuracy parameter
	 * 
	 * @param prop
	 * @param accuracy
	 */
	public static void genresult(Properties prop, float accuracy) {

		OutputStream output = null;

		try {
			output = new FileOutputStream(prop.getProperty("output") + "used.properties");

			prop.setProperty("accuracy", accuracy + "%");

			prop.store(output, "Used properties + accuracy");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {

		String arq = Arrays.toString(args);
		
		// Generate new Property File: -genprops
		if (arq.contains("-genprops")) {
			genProps();
		//Only tagging
		} else if (args[0].equals("-tag")) {
			
			File pathtoModel = new File(args[1]);
			File pathuntagged = new File(args[2]);
		
			if(pathtoModel.exists() && pathuntagged.exists()) {
				Tagger tagger = new Tagger();
				
				tagger.tagfile(pathtoModel.getPath()+"/corpus.RDR", 
						pathtoModel.getPath()+"/corpus.DICT", pathuntagged.getPath());
			} else {
				printUsage();
			}
			
		//Only Eval, diff of two corpora
		} else if (arq.contains("-validate")) {
			if( 3 == args.length) {
				if( new File(args[1]).exists() && new File(args[2]).exists()) {
					
					Evaluation eval = new Evaluation();
					
					HashMap<String, Long > result = eval.evaluate(args[1], args[2]);
					
					float fa = result.get("false");
					float all = result.get("all");

					float accuracy = 1 - (fa / all);
					
					System.out.println("Words count "+all);
					System.out.println("Words false "+fa);
					System.out.println("accuracy    "+accuracy);
				} else {
					System.out.println("Corpus not found");
				}
			}else {
				printUsage();
			}
			
		} else if (arq.contains("-help")) {
			printUsage();
		} else {
			Properties prop = loadProps(args[0]);
			
			String folder_path = prop.getProperty("output");
			File folder = new File(folder_path);
			folder.mkdirs();
			
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
	
	public static void printUsage() {
		//TODO
		System.out.println("Verwendung: asv-postagger [propertiefile]");
		System.out.println("\t\t(Zum trainieren des Taggers)");
		System.out.println("\toder asvpostagger [-options]");
		System.out.println("\t\t(Zur Ausführung weiterer Funktionen)");
		System.out.println("Wobei options folgendes umfasst:");
		System.out.println("\t -genprops \t erzeugt neues tagger.properties\n");
		System.out.println("\t -validate \t <path/to/goldCorpus> <path/to/taggedCorpus>");
		System.out.println("\t\t\t Vergeleicht zwei Corpora\n");
		System.out.println("\t -tag \t\t <path/to/model/folder/> <path/to/untagged/file>");
		System.out.println("\t\t\t Taggen eines Corpus mit einem erstellten Model\n");
		System.out.println("\t -help \t Zeigt diese Hilfe");
	}
}

package org.asv.postagger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

/**
 * Command line interface and program runtime
 * 
 * @author robert, marvin
 *
 */
public class Cli {

	/**
	 * Generates empty properties file
	 *
	 */
	public static void genProps() {
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

		} catch (Exception e) {
			// Error Log
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

	/**
	 * Load input properties file
	 * 
	 * @param filename path to input properties file
	 * @return input properties
	 * @throws IOException IOException
	 */
	public static Properties loadProps(String filename) throws IOException {
		Properties prop = new Properties();
		InputStream input = null;

		input = new FileInputStream(filename);
		// input = getClass().getClassLoader().getResourceAsStream(filename);

		prop.load(input);

		return prop;

	}

	/**
	 * Create train data from database
	 * 
	 * @param prop input propertiess
	 * @throws IOException IOException
	 */
	public static void fromdb(Properties prop) throws IOException {

		TrainFile trainFile = new TrainFile();

		File outfile = new File(prop.getProperty("output"));
		
		trainFile.writeFileFromDB(outfile.getPath() + "/corpus", prop.getProperty("dbAdress"),
				prop.getProperty("dbUser"), prop.getProperty("dbPassword"), prop.getProperty("table"),
				prop.getProperty("sentence_column"), prop.getProperty("delimiter"),
				Integer.parseInt(prop.getProperty("testPercentage")), Integer.parseInt(prop.getProperty("limit")));

	}
	
	/**
	 * Create train data from file
	 * 
	 * @param prop input properties
	 * @throws IOException IOException
	 */
	public static void fromfile(Properties prop) throws IOException {

		TrainFile trainFile = new TrainFile();

		File outfile = new File(prop.getProperty("output"));
		
		trainFile.writeFileFromFile(prop.getProperty("input"), outfile.getPath()+"/corpus", prop.getProperty("delimiter"), Integer.parseInt(prop.getProperty("testPercentage")));

	}

	/**
	 * 
	 * Train access function
	 * 
	 * @param prop input properties
	 * @throws IOException IOException
	 */
	public static void training(Properties prop) throws IOException {

		Tagger tagger = new Tagger();

		tagger.train(prop.getProperty("output") + "/corpus", "RDRPOSTagger/pSCRDRtagger/");
	}

	/**
	 * Evaluation access function
	 * 
	 * @param prop input properties
	 * @throws IOException IOException
	 */
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

		System.out.println("Words count "+all);
		System.out.println("Words false "+fa);
		System.out.println("accuracy    "+accuracy);

		genresult(prop, accuracy);
	}

	/**
	 * Copy of the input properties  file with accuracy parameter
	 * 
	 * @param prop input properties
	 * @param accuracy additional accuracy
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

	/**
	 * Main function
	 *  
	 * @param args command line arguments 
	 * @throws IOException IOException
	 */
	public static void main(String[] args) throws IOException {

		if(0 == args.length) {
			printUsage();
			System.exit(0);
		}
		
		String arq = Arrays.toString(args);
		
		// Generate new properties file
		if (arq.contains("-genprops")) {
			System.out.println("=== Generiere Properties-File ===\n");
			System.out.println(">>> tagger.properties");
			genProps();
		//tag
		} else if (args[0].equals("-tag")) {
			
			System.out.println("=== Taggen eines Corpus ===\n");
			
			File pathtoModel = new File(args[1]);
			File pathuntagged = new File(args[2]);
		
			if(pathtoModel.exists() && pathuntagged.exists()) {
				Tagger tagger = new Tagger();
				
				tagger.tagfile(pathtoModel.getPath()+"/corpus.RDR", 
						pathtoModel.getPath()+"/corpus.DICT", pathuntagged.getPath());
				System.out.println(">>> "+args[2]+".TAGGED");
			} else {
				printUsage();
			}
			
		//validate
		} else if (arq.contains("-validate")) {
			if( 3 == args.length) {
				
				System.out.println("=== Valiediere Corpora ===\n");
				
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
		//help
		} else if (arq.contains("-help" )) {
			printUsage();
		//train
		} else {	
			System.out.println("=== Trainiere Model ===");
			Properties prop = loadProps(args[0]);
			
			String outp = new File(prop.getProperty("output")).getPath();
			
			String folder_path = prop.getProperty("output");
			File folder = new File(folder_path);
			folder.mkdirs();
			
			if(prop.getProperty("input").equals("")){
				System.out.println("Erzeuge Trainingsdaten von Datenbank...");
				fromdb(prop);
				System.out.println(">>> "+outp+"/corpus");
				System.out.println(">>> "+outp+"/corpus_test");
			} else {
				System.out.println("Erzeuge Trainingsdaten von Datei");
				fromfile(prop);
				System.out.println(">>> "+outp+"/corpus");
				System.out.println(">>> "+outp+"/corpus_test");
			}
			
			System.out.println("Trainiere auf Trainingsdatensatz...");
			training(prop);
			System.out.println(">>> "+outp+"/corpus.RDR");
			System.out.println(">>> "+outp+"/corpus.DICT");
			
			System.out.println("Evauliere auf Testdatensatz...");
			eval(prop);
			System.out.println(">>> "+outp+"/corpus_test_wot");
			System.out.println(">>> "+outp+"/corpus_test_wot.TAGGED");
			System.out.println(">>> "+outp+"/used.properties");
		} 
	}
	
	/**
	 * Print program usage on command line
	 */
	public static void printUsage() {
		System.out.println("Verwendung:");
		System.out.println("\t     asv-postagger [propertiefile]");
		System.out.println("\t\t(Zum trainieren des Taggers)\n");
		System.out.println("\toder asv-postagger [-options]");
		System.out.println("\t\t(Zur Ausführung weiterer Funktionen)\n");
		System.out.println("Wobei options folgendes umfasst:");
		System.out.println("\t -genprops \t erzeugt neues tagger.properties\n");
		System.out.println("\t -validate \t <path/to/goldCorpus> <path/to/taggedCorpus>");
		System.out.println("\t\t\t Vergeleicht zwei Corpora\n");
		System.out.println("\t -tag \t\t <path/to/model/folder/> <path/to/untagged/file>");
		System.out.println("\t\t\t Taggen eines Corpus mit einem erstellten Model\n");
		System.out.println("\t -help \t\t Zeigt diese Hilfe");
	}
}

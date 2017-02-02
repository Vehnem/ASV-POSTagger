package org.asv.postagger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

//TODO which is unused?

/**
 * 
 * @author marvin, robert
 *
 */
public class Tagger {
	
	private static final Logger LOGGER = Logger.getLogger(Tagger.class.getName());
	
	/**
	 * 
	 * @param goldCorpus
	 * @throws IOException
	 */
	public void train(String goldCorpus ) throws IOException {

		
		
//		python RDRPOSTagger.py train PATH-TO-GOLD-STANDARD-TRAINING-CORPUS
		ProcessBuilder pb = new ProcessBuilder("python", "RDRPOSTagger.py", "train",goldCorpus);
		pb.directory(new File("./tagger/RDRPOSTagger/pSCRDRtagger/"));
		Process p = pb.start();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String ret;
		
		while( null != (ret = in.readLine())) {
			LOGGER.log(Level.FINE, ret);
		}
	}
	
	public boolean tagfile(String model, String lexicon, String corpus) throws IOException {
		
//		python RDRPOSTagger.py tag ../Models/POS/German.RDR ../Models/POS/German.DICT ../data/GermanRawTest
		ProcessBuilder pb = new ProcessBuilder("python", "RDRPOSTagger.py", "tag",model,lexicon,corpus);
		pb.directory(new File("./tagger/RDRPOSTagger/pSCRDRtagger/"));
		Process p = pb.start();
	
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String ret;
// testweise Error output eingebaut. ungetestet.
		BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		String err = error.readLine();
		if(err != null) {
			do {
				LOGGER.log(Level.SEVERE, err);
			} while( null != (err = error.readLine()));
			return false;
		}
		
		while( null != (ret = in.readLine())) {
			LOGGER.log(Level.FINE, ret);
		}
		
		return true;
	}
	
	//TODO tagset and eval in cli
	public void trainFromFile(String filename) throws IOException {


		System.out.println("\n====== Training ======");
		System.out.println("\nTraining on Training-File...");
		train("../../../data/" + filename);

		Tagset tagset = new Tagset();
		
		System.out.println("\n====== Testing ======");
		// Remove Tags
		System.out.println("\nGenerating Test Data...");
		tagset.removeTags("./data/" + filename + "_test", "./data/" + filename + "_wot", "/");

		
		
		// Testing
		System.out.println("\nTagging Test Data...");
		tagfile("../../../data/" + filename + ".RDR", "../../../data/" + filename + ".DICT",
				"../../../data/" + filename + "_wot");

		
		// Eval
		Evaluation evaluation = new Evaluation();
		
		System.out.println("\nEvaluating...");
		HashMap<String, Long> result = evaluation.evaluate("./data/" + filename + "_wot.TAGGED", "./data/" + filename + "_test");
		System.out.println("\n" + result.get("false") + "/" + result.get("all") + " Fehlerhaft");
		float f = result.get("false"), a = result.get("all");
		float rate = 100 - (f / a) * 100;
		System.out.println("=> Genauigkeit: " + rate + "%");
	}

}

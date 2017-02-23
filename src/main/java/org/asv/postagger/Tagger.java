package org.asv.postagger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author marvin, robert
 *
 */
public class Tagger {
	
	private static final Logger LOGGER = Logger.getLogger(Tagger.class.getName());
	
	/**
	 * 
	 * 
	 * @param goldCorpus
	 * @throws IOException
	 */
	public void train(String pathtocorpus, String taggerpath ) throws IOException {

		//python RDRPOSTagger.py train PATH-TO-GOLD-STANDARD-TRAINING-CORPUS
		ProcessBuilder pb = new ProcessBuilder("python", "RDRPOSTagger.py", "train","../../"+pathtocorpus);
		pb.directory(new File(taggerpath));
		Process p = pb.start();
		
		//commandline out
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String ret;
		while( null != (ret = in.readLine())) {
			LOGGER.log(Level.FINE, ret);
		}
	}
	
	
	/**
	 * Tag a file with lexicon and corpus
	 * with RDRPostagger python
	 * 
	 * @param model 
	 * @param lexicon
	 * @param corpus 
	 * @return
	 * @throws IOException
	 */
	public boolean tagfile(String model, String lexicon, String corpus) throws IOException {
		
		//python RDRPOSTagger.py tag ../Models/POS/German.RDR ../Models/POS/German.DICT ../data/GermanRawTest
		ProcessBuilder pb = new ProcessBuilder("python", "RDRPOSTagger.py", "tag","../../"+model,"../../"+lexicon,"../../"+corpus);
		pb.directory(new File("RDRPOSTagger/pSCRDRtagger/"));
		Process p = pb.start();
	
		//commandline out
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String ret;
		
		//Error output
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
}

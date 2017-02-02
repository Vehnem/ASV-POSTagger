package org.asv.postagger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public class RAWFormater {
	
	private static final Logger LOGGER = Logger.getLogger(RAWFormater.class.getName());

	public void removeTags(String inpath, String outpath, String split) throws IOException {
		
		FileReader fr = new FileReader(inpath);
		BufferedReader br = new BufferedReader(fr);

		String line = "";

		File file = new File(outpath);

		// creates the file
		file.createNewFile();

		// creates a FileWriter Object
		FileWriter writer = new FileWriter(file);

		// Writes the content to the file
		

		while ((line = br.readLine()) != null) {

			ArrayList<String> new_line = new ArrayList<String>();

			String[] words = line.split(" ");

			for (String word : words) {
				
				new_line.add(word.split(split)[0]);
			}
			writer.write(String.join(" ", new_line)+"\n");
		}
		writer.flush();
		writer.close();
		br.close();
	}
	
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
	
	public boolean tagg(String model, String lexicon, String corpus) throws IOException {
		
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
	
	public HashMap<String, Long> eval(String goldCorpus, String taggedCorpus) throws IOException {
		
		FileReader fr1 = new FileReader(goldCorpus);
		BufferedReader gCr = new BufferedReader(fr1);
		
		FileReader fr2 = new FileReader(taggedCorpus);
		BufferedReader tCr = new BufferedReader(fr2);
		
		String goldline = "";
		String taggedline = "";
		
		long countWords = 0;
		long falseWords = 0;
		
		while ((goldline = gCr.readLine()) != null && null != (taggedline = tCr.readLine())) {
			
			String[] gLsplit = goldline.split(" ");
			String[] tLsplit = taggedline.split(" ");
			
			for(int i = 0; i < gLsplit.length; i++) {
				
				countWords++;
				
				if(!tLsplit[i].equals(gLsplit[i])) {
					falseWords++;
				}
			}
		}
		
		gCr.close();
		tCr.close();
		HashMap<String, Long> result = new HashMap<String, Long>();
		
		result.put("all", countWords);
		result.put("false", falseWords);
		
		return result;
	}
}

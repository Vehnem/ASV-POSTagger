package org.asv.postagger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Used to change Tag sets in a tagged corpus
 * 
 * @author Marvin
 *
 */
public class TagsetChanger {
	
	/**
	 * Property file like: oldTAG<TAB>newTAG
	 * 
	 * @param pathtotaggedcorpus
	 * @param pathtopropertyfile
	 * @throws IOException
	 */
	public void change(String pathtotaggedcorpus,String pathtopropertyfile) throws IOException {
		
		//load changing tags
		FileReader prop_file = new FileReader(pathtopropertyfile);
		BufferedReader prop_reader = new BufferedReader(prop_file);
		String line = "";
		HashMap<String, String> changemap = new HashMap<String, String>();
		
		while ((line = prop_reader.readLine()) != null) {
			String split[] = line.split("\t");
			changemap.put(split[0], split[1]);
		}
		prop_file.close();
		prop_reader.close();
		
		
		//Change
		FileReader tagged_file = new FileReader(pathtotaggedcorpus);
		BufferedReader tagged_reader = new BufferedReader(tagged_file);
		
		File file = new File(pathtotaggedcorpus+".NEW");
		file.createNewFile();
		FileWriter writer = new FileWriter(file);
		
		while ((line = tagged_reader.readLine()) != null) {
			List<String> lineout = new ArrayList<String>();
			String words[] = line.split(" ");
			
			for(String word : words) {
				String split[] = word.split("/");
				lineout.add(split[0] + "/" + changemap.get(split[1]));
			}
			writer.write(String.join(" ",lineout)+"\n");
		}
		tagged_file.close();
		tagged_reader.close();
		writer.close();
	}
}

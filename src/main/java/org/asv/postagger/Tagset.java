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
 * Handles tag sets for tagged corpora
 * 
 * @author marvin, robert
 *
 */
public class Tagset {

	/**
	 * change a tag set with a property file
	 * OLDTAG tab NEWTAG
	 * 
	 * @param pathtotaggedcorpus path to tagged corpus
	 * @param pathtochangefile path to change file
	 * @throws IOException IOException
	 */
	public void change(String pathtotaggedcorpus,String pathtochangefile) throws IOException {
		
		//load changing tags
		FileReader prop_file = new FileReader(pathtochangefile);
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
	
	/**
	 * Removes tags from a tagged corpus
	 * 
	 * @param inpath path to tagged corpus
	 * @param outpath result path
	 * @param split delimiter sign
	 * @throws IOException IOExceptio
	 */
	//TODO special cases for example " -> ''
	public void removeTags(String inpath, String outpath, String split) throws IOException {
		
		FileReader fr = new FileReader(inpath);
		BufferedReader br = new BufferedReader(fr);

		String line = "";

		File file = new File(outpath);

		// creates a file
		file.createNewFile();

		// creates a FileWriter Object
		FileWriter writer = new FileWriter(file);

		// Writes content to a file
		while ((line = br.readLine()) != null) {

			ArrayList<String> new_line = new ArrayList<String>();

			String[] words = line.split(" ");

			for (String word : words) {
				
				if(word.startsWith("/")) {
					new_line.add("/");
				} else {
					new_line.add(word.split(split)[0]);
				}
				
			}
			writer.write(String.join(" ", new_line)+"\n");
		}
		writer.flush();
		writer.close();
		br.close();
	}
}

package org.asv.postagger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * 
 * @author robert, marvin
 *
 */
public class Evaluation {

	/**
	 * 
	 * Evaluation over GoldCorpus and TaggedCorpus data
	 * 
	 * @param goldCorpus
	 * @param taggedCorpus
	 * @return
	 * @throws IOException
	 */
	public HashMap<String, Long> evaluate(String goldCorpus, String taggedCorpus) throws IOException {

		FileReader fr1 = new FileReader(goldCorpus);
		BufferedReader gCr = new BufferedReader(fr1);

		FileReader fr2 = new FileReader(taggedCorpus);
		BufferedReader tCr = new BufferedReader(fr2);

		String goldline = "";
		String taggedline = "";

		long countWords = 0;
		long falseWords = 0;

		while ((goldline = gCr.readLine()) != null && null != (taggedline = tCr.readLine())) {

			//System.out.println(goldline + " \n " + taggedline);

			String[] gLsplit = goldline.split(" ");
			String[] tLsplit = taggedline.split(" ");

			for (int i = 0; i < gLsplit.length; i++) {

				countWords++;

				try {
					if (!tLsplit[i].equals(gLsplit[i])) {
						falseWords++;
					}
				} catch (Exception e) {
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

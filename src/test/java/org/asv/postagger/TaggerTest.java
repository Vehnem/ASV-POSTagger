package org.asv.postagger;

import java.io.IOException;
import org.junit.Test;

/**
 * Tagger test class
 * 
 * @author marvin, robert
 *
 */
public class TaggerTest {

	//params
	private static final String pathtogoldcorpus ="";
	private static final String taggerpath ="";
	private static final String pathtomodel ="";
	private static final String pathtolexicon ="";
	private static final String pathtountaggedcorpus ="";
	
	@Test
	public void train() throws IOException {
		Tagger tagger = new Tagger();
		tagger.train(pathtogoldcorpus, taggerpath);
	}
	
	@Test
	public void tag() throws IOException {
		Tagger tagger = new Tagger();
		tagger.tagfile(pathtomodel, pathtolexicon, pathtountaggedcorpus);
	}
}

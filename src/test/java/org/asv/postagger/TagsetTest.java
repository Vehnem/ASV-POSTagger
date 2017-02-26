package org.asv.postagger;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

/**
 * Tagset test class
 * 
 * @author marvin, robert
 *
 */
public class TagsetTest {
		
	private static final String pathtotaggedcorpus = "";
	private static final String pathtochangefile = "";
	private static final String inpath = "";
	private static final String outpath = "";
	private static final String split = "";
	
	@Test
	public void changetagset() throws IOException {
		Tagset tagset = new Tagset();
		tagset.change(pathtotaggedcorpus, pathtochangefile);
	}
	
	@Test
	public void removetags() throws IOException {
		Tagset tagset = new Tagset();
		tagset.removeTags(inpath, outpath, split);
	}
}

package org.asv.postagger;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

//rename in TrainTest
public class TaggerTest {

	Tagger tagger = new Tagger();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}
	
	@Test
	public void train() throws IOException {
//		tagger.train("test","RDRPOSTagger/pSCRDRtagger/");
		Cli.printUsage();
	}
}

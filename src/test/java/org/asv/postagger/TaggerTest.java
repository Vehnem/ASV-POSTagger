package org.asv.postagger;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

public class TaggerTest {

	Tagger tagger = new Tagger();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}
	
	@Test
	public void train() throws IOException {
		tagger.new_train("test");
	}
	


}

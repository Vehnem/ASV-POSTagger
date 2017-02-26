package org.asv.postagger;

import org.junit.Test;
import junit.framework.TestCase;

public class TrainFileTest extends TestCase {

	@Test
	public void test() {
		TrainFile trainFile = new TrainFile();
		
		trainFile.writeFileFromDB("test", "localhost/test?useSSL=false", "root", "1234", "deu_news_2011", "sentence_tagged", "\\/",10, 1000);
		
	}
}
